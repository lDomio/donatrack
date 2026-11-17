package ar.edu.utn.frba.dds.donaciones.main.exceptions;

public class ImportacionException extends RuntimeException {

    public ImportacionException(String mensaje) {
        super(mensaje);
    }

    public ImportacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}