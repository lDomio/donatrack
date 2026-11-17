package ar.edu.utn.frba.dds.donaciones.main.infrastructure.impl;

import ar.edu.utn.frba.dds.common.messaging.RestApiCallerService;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class AuthRestAdapter implements AuthClient {

    private final String authServiceUri;
    private final RestApiCallerService restApiCallerService;
    public AuthRestAdapter(
        @Value("${services.autenticacion.url:http://autenticacion-service:8084}") String authServiceUri, RestApiCallerService restApiCallerService) {
        this.authServiceUri = authServiceUri;
        this.restApiCallerService = restApiCallerService;
    }

    @Override
    public Long registrarUsuario(String username, String password, String nombreCompleto, String rol) {
        String[] possibleUris = {
                authServiceUri + "/api/auth/register",
                "http://localhost:8084/api/auth/register"
        };

        String usernameToTry = username;
        for (int intento = 0; intento < 3; intento++) {
            for (String uri : possibleUris) {
                try {
                    Map<String, String> body = Map.of(
                            "username", usernameToTry,
                            "password", password,
                            "nombreCompleto", nombreCompleto != null ? nombreCompleto : usernameToTry,
                            "rol", rol
                    );
                    Map response = restApiCallerService.post(uri, body, Map.class);
                    if (response != null && response.get("id") != null) {
                        Long id = ((Number) response.get("id")).longValue();
                        log.info("[AuthAdapter] Usuario {} creado exitosamente en auth con ID {}", usernameToTry, id);
                        return id;
                    }
                } catch (Exception e) {
                    log.debug("[AuthAdapter] Intento en {} para {} falló: {}", uri, usernameToTry, e.getMessage());
                }
            }
            usernameToTry = username + "_" + (intento + 1);
        }

        log.warn("[AuthAdapter] No se pudo crear usuario auth para {}", username);
        return null;
    }
}
