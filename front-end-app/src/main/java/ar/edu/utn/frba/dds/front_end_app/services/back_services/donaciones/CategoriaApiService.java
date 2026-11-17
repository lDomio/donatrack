package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones;

import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CategoriaApiService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaApiService.class);
    private final String donacionesServiceUrl;
    private final WebApiCallerService webApiCaller;

    public CategoriaApiService(
            @Value("${donaciones.service.url:http://localhost:8080/donaciones-service}") String donacionesServiceUrl,
            WebApiCallerService webApiCaller) {
        this.donacionesServiceUrl = donacionesServiceUrl;
        this.webApiCaller = webApiCaller;
    }
}
