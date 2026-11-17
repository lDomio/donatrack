package ar.edu.utn.frba.dds.donaciones.main.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
