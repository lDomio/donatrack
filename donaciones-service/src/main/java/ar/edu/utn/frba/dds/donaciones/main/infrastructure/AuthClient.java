package ar.edu.utn.frba.dds.donaciones.main.infrastructure;

public interface AuthClient {
    Long registrarUsuario(String username, String password, String nombreCompleto, String rol);
}
