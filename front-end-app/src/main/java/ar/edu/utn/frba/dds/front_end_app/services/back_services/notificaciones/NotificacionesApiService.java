package ar.edu.utn.frba.dds.front_end_app.services.back_services.notificaciones;

import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesApiService.class);
    private final String notificacionesServiceUrl;
    private final WebApiCallerService webApiCaller;

    public NotificacionesApiService(
            @Value("${notificaciones.service.url:http://localhost:8082}") String notificacionesServiceUrl,
            WebApiCallerService webApiCaller) {
        this.notificacionesServiceUrl = notificacionesServiceUrl;
        this.webApiCaller = webApiCaller;
    }
}
