package ar.edu.utn.frba.dds.front_end_app.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
  private final Map<String, String> fieldErrors = new HashMap<>();

  public void addFieldError(String field, String error) {
    fieldErrors.put(field, error);
  }

  public boolean hasFieldErrors() {
    return !fieldErrors.isEmpty();
  }

  public Map<String, String> getFieldErrors() {
    return fieldErrors;
  }
}
