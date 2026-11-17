package ar.edu.utn.frba.dds.incentivos.main.config;

import ar.edu.utn.frba.dds.incentivos.main.dtos.ErrorResponse;
import ar.edu.utn.frba.dds.incentivos.main.exceptions.UnauthorizedException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex){
    return build(HttpStatus.FORBIDDEN, "unathorized", ex.getMessage());
  }
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<String> handleAuthenticationError(AuthenticationException ex) {
    System.out.println("El usuario no está autenticado: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Debes iniciar sesión o enviar un token válido para acceder.");
  }
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
    System.out.println("Fallo de autorización detectado: "+ ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("No tienes los permisos necesarios para realizar esta acción.");
  }
  private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(error, message, Instant.now()));
  }
}
