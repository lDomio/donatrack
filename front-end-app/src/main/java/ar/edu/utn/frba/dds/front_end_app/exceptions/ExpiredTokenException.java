package ar.edu.utn.frba.dds.front_end_app.exceptions;

public class ExpiredTokenException extends RuntimeException {
  public ExpiredTokenException(String message) {
    super(message);
  }
}
