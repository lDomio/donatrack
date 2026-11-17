package ar.edu.utn.frba.dds.autenticacion.controllers;

import ar.edu.utn.frba.dds.autenticacion.dto.AuthResponseDTO;
import ar.edu.utn.frba.dds.autenticacion.dto.RefreshRequest;
import ar.edu.utn.frba.dds.autenticacion.dto.RegistroUsuarioDTO;
import ar.edu.utn.frba.dds.autenticacion.dto.TokenResponse;
import ar.edu.utn.frba.dds.autenticacion.dto.UsuarioResponseDTO;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.Usuario;
import ar.edu.utn.frba.dds.autenticacion.services.LoginService;
import ar.edu.utn.frba.dds.common.security.JwtUtil;
import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final LoginService loginService;
  private final JwtUtil jwtUtil;

  //RECIBIMOS DESDE EL AUTH PROVIDER DEL FRONTEND APP, UN JWT donde en el body se envia el user y password
  @PostMapping
  public ResponseEntity<AuthResponseDTO> loginApi(@RequestBody Map<String, String> credentials) {

    try {
      String username = credentials.get("username");
      String password = credentials.get("password");

      // Validación básica de credenciales
      if (username == null || username.trim().isEmpty() ||
          password == null || password.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }
      // Autenticar usuario usando el LoginService
      Usuario usuario = loginService.autenticarUsuario(username, password);

      // Generar tokens
      String accessToken = loginService.generarAccessToken(usuario);
      String refreshToken = loginService.generarRefreshToken(username);
      System.out.println("Se generaron los tokens: " + accessToken + " y " + refreshToken);
      AuthResponseDTO response = AuthResponseDTO.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .build();

      log.info("El usuario {} está logueado. El token generado es {}", username, accessToken);

      return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      System.out.println("Mensaje de error: " + e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
    try {
      Claims claims = jwtUtil.parsearClaims(request.refreshToken());

      // Validar que el token sea de tipo refresh
      if (!JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
        return ResponseEntity.badRequest().build();
      }

      String username = claims.getSubject();
      String newAccessToken = loginService.generarAccessTokenPorUsername(username);
      TokenResponse response = new TokenResponse(newAccessToken, request.refreshToken());

      return ResponseEntity.ok(response);
    }
    catch (ExpiredJwtException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // <-- Esto devuelve un 401
    }
    catch (Exception e) {
      System.out.println("Error " + e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
  public ResponseEntity<?> registerApi(@RequestBody RegistroUsuarioDTO request) {
    try {
      if (request.username() == null || request.username().trim().isEmpty() ||
          request.password() == null || request.password().trim().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario y contraseña son requeridos"));
      }

      UsuarioResponseDTO usuarioCreado = loginService.registrarUsuario(request);
      log.info("Nuevo usuario registrado: {} con rol {}", usuarioCreado.username(), usuarioCreado.rol());
      return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      log.error("Error al registrar usuario: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error interno al procesar el registro"));
    }
  }
}
