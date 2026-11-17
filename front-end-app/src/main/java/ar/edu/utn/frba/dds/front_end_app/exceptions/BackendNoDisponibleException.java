package ar.edu.utn.frba.dds.front_end_app.exceptions;

public class BackendNoDisponibleException extends RuntimeException {
  public BackendNoDisponibleException(String servicio, Throwable causa) {
    super("No se pudo contactar a " + servicio, causa);
  }
}
