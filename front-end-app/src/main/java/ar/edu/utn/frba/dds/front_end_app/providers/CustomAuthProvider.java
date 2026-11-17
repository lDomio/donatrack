package ar.edu.utn.frba.dds.front_end_app.providers;

import ar.edu.utn.frba.dds.front_end_app.dto.AuthResponseDTO;
import ar.edu.utn.frba.dds.front_end_app.utils.JwtClaimsReader;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

  private final WebClient webClient;
  private final JwtClaimsReader jwtClaimsReader;
  private final String authServiceUrl;

  public CustomAuthProvider(
      JwtClaimsReader jwtClaimsReader,
      @Value("${auth.service.url}") String authServiceUrl
  ) {
    this.jwtClaimsReader = jwtClaimsReader;
    this.authServiceUrl = authServiceUrl;
    this.webClient = WebClient.builder().build();
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials() != null ? authentication.getCredentials().toString() : "";

    try {
      Map<String, String> credentials = Map.of(
          "username", username,
          "password", password
      );

      // Llamar al microservicio de autenticación (POST http://localhost:8084/api/auth)
      AuthResponseDTO response = webClient.post()
          .uri(authServiceUrl + "/auth")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(credentials)
          .retrieve()
          .bodyToMono(AuthResponseDTO.class)
          .block();

      if (response == null || response.accessToken() == null) {
        throw new BadCredentialsException("Respuesta inválida del servicio de autenticación");
      }

      // Decodificar claims del token para obtener rol, permisos e idUsuarioAuth
      Claims claims = jwtClaimsReader.leerClaims(response.accessToken());

      Object usuarioIdClaim = claims.get(JwtClaimsReader.CLAIM_USUARIO_ID);
      Long usuarioId = null;
      if (usuarioIdClaim instanceof Number num) {
        usuarioId = num.longValue();
      }

      // Guardar tokens JWT e idUsuarioAuth en la sesión HTTP para que WebApiCallerService y controllers los usen
      guardarTokensEnSesion(response.accessToken(), response.refreshToken(), usuarioId);

      List<GrantedAuthority> authorities = new ArrayList<>();

      // 1. Mapear el Rol con prefijo ROLE_ (convención de Spring Security para hasRole)
      String rol = claims.get(JwtClaimsReader.CLAIM_ROL, String.class);
      if (rol != null && !rol.isBlank()) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
      }

      // 2. Mapear los Permisos finos para hasAuthority
      List<?> permisos = claims.get(JwtClaimsReader.CLAIM_PERMISOS, List.class);
      if (permisos != null) {
        permisos.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.toString())));
      }

      return new UsernamePasswordAuthenticationToken(username, password, authorities);

    } catch (BadCredentialsException e) {
      throw e;
    } catch (Exception e) {
      throw new BadCredentialsException("Usuario o contraseña incorrectos", e);
    }
  }

  private void guardarTokensEnSesion(String accessToken, String refreshToken, Long usuarioId) {
    try {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        request.getSession().setAttribute("accessToken", accessToken);
        request.getSession().setAttribute("refreshToken", refreshToken);
        if (usuarioId != null) {
          request.getSession().setAttribute("idUsuarioAuth", usuarioId);
        }
      }
    } catch (Exception ignored) {
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
