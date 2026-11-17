package ar.edu.utn.frba.dds.front_end_app.services.back_services.auth;

import ar.edu.utn.frba.dds.front_end_app.dto.RegistroAuthRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.UsuarioResponseDTO;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthApiService {

    private static final Logger log = LoggerFactory.getLogger(AuthApiService.class);
    private final String authServiceUrl;
    private final WebApiCallerService webApiCaller;

    public AuthApiService(
            @Value("${auth.service.url:http://localhost:8084/api}") String authServiceUrl,
            WebApiCallerService webApiCaller) {
        this.authServiceUrl = authServiceUrl;
        this.webApiCaller = webApiCaller;
    }

    public UsuarioResponseDTO registrar(String username, String password, String confirmPassword, String nombreCompleto, String rol) {
        validarCredenciales(username, password, confirmPassword);
        RegistroAuthRequest request = new RegistroAuthRequest(username, password, nombreCompleto, rol);
        return registrarUsuario(request);
    }

    public UsuarioResponseDTO registrarUsuario(RegistroAuthRequest request) {
        String url = authServiceUrl + "/auth/register";
        log.info("Registrando usuario en auth-service: {}", url);

        return webApiCaller.post(url, request, UsuarioResponseDTO.class);
    }

    private void validarCredenciales(String username, String password, String confirmPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas ingresadas no coinciden.");
        }
    }
}
