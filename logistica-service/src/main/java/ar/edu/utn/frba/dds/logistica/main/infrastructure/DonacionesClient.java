package ar.edu.utn.frba.dds.logistica.main.infrastructure;

public interface DonacionesClient {
    void actualizarEstado(Long donacionId, String nuevoEstado, String motivo, String autor);
}
