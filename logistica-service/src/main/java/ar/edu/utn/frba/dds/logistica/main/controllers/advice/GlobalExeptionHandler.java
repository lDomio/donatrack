package ar.edu.utn.frba.dds.logistica.main.controllers.advice;

import ar.edu.utn.frba.dds.logistica.main.dtos.ErrorResponse;
import ar.edu.utn.frba.dds.logistica.main.exception.EstructuraInesperadaException;
import ar.edu.utn.frba.dds.logistica.main.exception.UnauthorizedException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExeptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EstructuraInesperadaException.class)
    public ResponseEntity<String> handleEstructuraInesperada(EstructuraInesperadaException ex) {
        ex.printStackTrace(System.err);
        return ResponseEntity.ok("Error recibido");
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex){
        System.out.println(ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "unathorized", ex.getMessage());
    }
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
            .body(new ErrorResponse(error, message, Instant.now()));
    }
}
