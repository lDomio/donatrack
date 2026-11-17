package ar.edu.utn.frba.dds.common.messaging;

import ar.edu.utn.frba.dds.common.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@ConditionalOnBean(RestTemplate.class)
public class RestApiCallerService {
  private final RestTemplate restTemplate;
  private final GeneradorAccessTokenServicios generadorAccessTokenServicios;

  public RestApiCallerService(RestTemplate restTemplate, GeneradorAccessTokenServicios generadorAccessTokenServicios) {
    this.restTemplate = restTemplate;
    this.generadorAccessTokenServicios = generadorAccessTokenServicios;
  }

  public <T> T get(String url, Class<T> responseType){
    return exchange(url, HttpMethod.GET,null,responseType).getBody();

  }
  public <T> T post(String url,Object request, Class<T> responseType){
    return exchange(url, HttpMethod.POST,request,responseType).getBody();
  }
  public void put(String url,Object request){
    exchange(url, HttpMethod.PUT,request,Void.class);
  }
  public <T> T patch(String url,Object request, Class<T> responseType){
    return exchange(url, HttpMethod.PATCH,request,responseType).getBody();
  }
  private <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object requestBody, Class<T> responseType){
    String accessToken = generadorAccessTokenServicios.generarAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

    return restTemplate.exchange(
        url,
        method,
        requestEntity,
        responseType
    );
  }

}

