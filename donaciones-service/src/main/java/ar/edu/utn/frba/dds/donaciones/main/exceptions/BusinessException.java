package ar.edu.utn.frba.dds.donaciones.main.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
