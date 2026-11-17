package ar.edu.utn.frba.dds.front_end_app.services.internal;

import ar.edu.utn.frba.dds.front_end_app.dto.AuthResponseDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.RefreshTokenDTO;
import ar.edu.utn.frba.dds.front_end_app.exceptions.ExpiredTokenException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.NotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;

/**
 * Servicio genérico para hacer llamadas HTTP con manejo automático de tokens
 */
@Service
public class WebApiCallerService {

  private final WebClient webClient;
  private final String authServiceUrl;

  public WebApiCallerService(@Value("${auth.service.url}") String authServiceUrl) {
    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
        .build();
    this.webClient = WebClient.builder()
        .exchangeStrategies(strategies)
        .build();
    this.authServiceUrl = authServiceUrl;
  }

  /**
   * Ejecuta una llamada al API con manejo automático de refresh token
   * @param apiCall función que ejecuta la llamada al API
   * @return resultado de la llamada al API
   */
  public <T> T executeWithTokenRetry(ApiCall<T> apiCall) {
    String accessToken = getAccessTokenFromSession();
    String refreshToken = getRefreshTokenFromSession();

    if (accessToken == null) {
      throw new RuntimeException("No hay token de acceso disponible");
    }

    try {
      // Primer intento con el token actual
      return apiCall.execute(accessToken);
    } catch (WebClientResponseException e) {
      if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) && refreshToken != null) {
        try {
          // Token expirado, intentar refresh
          AuthResponseDTO newTokens = refreshToken(refreshToken);

          // Segundo intento con el nuevo token
          return apiCall.execute(newTokens.accessToken());
        } catch (Exception refreshError) {
          throw new RuntimeException("Error al refrescar token y reintentar: " + refreshError.getMessage(), refreshError);
        }
      }
      if(e.getStatusCode() == HttpStatus.NOT_FOUND){
        throw new NotFoundException(e.getMessage());
      }
      throw new RuntimeException("Error en llamada al API: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error de conexión con el servicio: " + e.getMessage(), e);
    }
  }

  /**
   * Ejecuta una llamada HTTP GET
   */
  public <T> T get(String url, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .get()
            .uri(url)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(responseType)
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP GET con una URL ya codificada.
   */
  public <T> T get(URI uri, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(responseType)
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP GET que retorna una lista
   */
  public <T> java.util.List<T> getList(String url, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .get()
            .uri(url)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToFlux(responseType)
            .collectList()
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP GET que retorna una lista con una URL ya codificada.
   */
  public <T> java.util.List<T> getList(URI uri, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .get()
            .uri(uri)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToFlux(responseType)
            .collectList()
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP POST
   */
  public <T> T post(String url, Object body, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + accessToken)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(responseType)
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP POST multipart enviando un archivo
   */
  public <T> java.util.List<T> postMultipartList(String url, String paramName, org.springframework.core.io.Resource resource, Class<T> responseType) {
    org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
    body.add(paramName, resource);

    return executeWithTokenRetry(accessToken ->
        webClient
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
            .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(body))
            .retrieve()
            .bodyToFlux(responseType)
            .collectList()
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP PUT
   */
  public <T> T put(String url, Object body, Class<T> responseType) {
    return executeWithTokenRetry(accessToken ->
        webClient
            .put()
            .uri(url)
            .header("Authorization", "Bearer " + accessToken)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(responseType)
            .block()
    );
  }

  /**
   * Ejecuta una llamada HTTP PATCH con cuerpo
   */
  public <T> T patch(String url, Object body, Class<T> responseType) {
    return executeWithTokenRetry(accessToken -> {
      var request = webClient
          .patch()
          .uri(url)
          .header("Authorization", "Bearer " + accessToken);

      if (body != null) {
        return request
            .bodyValue(body)
            .retrieve()
            .bodyToMono(responseType)
            .block();
      } else {
        return request
            .retrieve()
            .bodyToMono(responseType)
            .block();
      }
    });
  }

  /**
   * Ejecuta una llamada HTTP PATCH sin cuerpo
   */
  public <T> T patch(String url, Class<T> responseType) {
    return patch(url, null, responseType);
  }

  /**
   * Ejecuta una llamada HTTP DELETE
   */
  public void delete(String url) {
    executeWithTokenRetry(accessToken -> {
      webClient
          .delete()
          .uri(url)
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(Void.class)
          .block();
      return null;
    });
  }

  /**
   * Refresca el access token usando el refresh token
   */
  private AuthResponseDTO refreshToken(String refreshToken) {
    try {
      System.out.println("Pido con refresh token " + refreshToken);
      RefreshTokenDTO refreshRequest = RefreshTokenDTO.builder()
          .refreshToken(refreshToken)
          .build();
      System.out.println(refreshRequest.getRefreshToken());
      AuthResponseDTO response = webClient
          .post()
          .uri(authServiceUrl + "/auth/refresh")
          .bodyValue(refreshRequest)
          .retrieve()
          .bodyToMono(AuthResponseDTO.class)
          .block();

      // Actualizar tokens en sesión
      updateTokensInSession(response.accessToken(), response.refreshToken());
      return response;
    }
    catch (WebClientResponseException.Unauthorized e) {
      // Cayó acá porque el auth service devolvió un 401
      System.out.println("El refresh token expiró (Error 401). Hay que desloguear al usuario.");
      throw new ExpiredTokenException("El refresh token expiró");
    }
    catch (Exception e) {
      throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
    }
  }

  /**
   * Obtiene el access token de la sesión
   */
  private String getAccessTokenFromSession() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    return (String) request.getSession().getAttribute("accessToken");
  }

  /**
   * Obtiene el refresh token de la sesión
   */
  private String getRefreshTokenFromSession() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    return (String) request.getSession().getAttribute("refreshToken");
  }

  /**
   * Actualiza los tokens en la sesión
   */
  private void updateTokensInSession(String accessToken, String refreshToken) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    request.getSession().setAttribute("accessToken", accessToken);
    request.getSession().setAttribute("refreshToken", refreshToken);
  }

  /**
   * Interfaz funcional para ejecutar llamadas al API con token
   */
  @FunctionalInterface
  public interface ApiCall<T> {
    T execute(String accessToken) throws Exception;
  }
}
