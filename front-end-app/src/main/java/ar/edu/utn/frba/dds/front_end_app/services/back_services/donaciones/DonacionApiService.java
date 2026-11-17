package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones;

import ar.edu.utn.frba.dds.front_end_app.dto.CasoExitoDto;
import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaResponseSinSubcategoria;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionResponseDonante;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.CambiarEstadoRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.SugerenciaMatchResponse;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.ValidationException;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.DonacionMapper;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class DonacionApiService {

    private static final Logger log = LoggerFactory.getLogger(DonacionApiService.class);
    private final String donacionesServiceUrl;
    private final WebApiCallerService webApiCaller;

    public DonacionApiService(
        @Value("${donaciones.service.url:http://localhost:8080/donaciones-service}") String donacionesServiceUrl,
        WebApiCallerService webApiCaller) {
        this.donacionesServiceUrl = donacionesServiceUrl;
        this.webApiCaller = webApiCaller;
    }

    public List<DonacionResponseEntidad> obtenerTodasLasDonaciones() {
        try {
            String url = donacionesServiceUrl + "/donaciones";
            log.info("Obteniendo todas las donaciones en: {}", url);
            List<DonacionResponseEntidad> res = webApiCaller.getList(url, DonacionResponseEntidad.class);
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.error("Error al obtener todas las donaciones: {}", e.getMessage());
            return List.of();
        }
    }
    public DonacionResponse obtenerDonacion(Long idDonacion){
        try {
            String url = donacionesServiceUrl + "/donaciones/" + idDonacion;
            log.info("Llamando a donaciones-service en: {}", url);

            return webApiCaller.get(url, DonacionResponse.class);
        } catch (Exception e) {
            log.error("Error al obtener donaciones de necesidad de {}: {}", donacionesServiceUrl, e.getMessage());
            return null;
        }
    }

    public List<DonacionResponseEntidad> getDonacionesByNecesidad(Long idNecesidad) {
        try {
            String url = donacionesServiceUrl + "/donaciones?idNecesidad=" + idNecesidad;
            log.info("Llamando a donaciones-service en: {}", url);

            List<DonacionResponseEntidad> res = webApiCaller.getList(url, DonacionResponseEntidad.class);
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.error("Error al obtener donaciones de necesidad de {}: {}", donacionesServiceUrl, e.getMessage());
            return List.of();
        }
    }

    public void marcarComoVencida(Long donacionId) {
        if (donacionId == null) {
            throw new IllegalArgumentException("El id de la donación no puede ser nulo");
        }
        CambiarEstadoRequest request = new CambiarEstadoRequest(
            "Marcada como vencida por el administrador",
            LocalDate.now(),
            "VENCIDO",
            "Administrador",
            null
        );
        String url = donacionesServiceUrl + "/donaciones/" + donacionId + "/estado";
        log.info("Marcando donación {} como vencida en: {}", donacionId, url);
        webApiCaller.patch(url, request, Void.class);
    }

    public void reingresarAlDeposito(Long donacionId) {
        if (donacionId == null) {
            throw new IllegalArgumentException("El id de la donación no puede ser nulo");
        }
        CambiarEstadoRequest request = new CambiarEstadoRequest(
            "Reingresada al depósito por el administrador tras entrega fallida",
            LocalDate.now(),
            "DEPOSITO",
            "Administrador",
            null
        );
        String url = donacionesServiceUrl + "/donaciones/" + donacionId + "/estado";
        log.info("Reingresando donación {} al depósito en: {}", donacionId, url);
        webApiCaller.patch(url, request, Void.class);
    }

    public List<SugerenciaMatchResponse> obtenerSugerenciasDeMatcheo() {
        try {
            String url = donacionesServiceUrl + "/donaciones/matcheador";
            log.info("Obteniendo sugerencias de matcheo en: {}", url);
            List<SugerenciaMatchResponse> res = webApiCaller.getList(url, SugerenciaMatchResponse.class);
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.error("Error al obtener sugerencias de matcheo: {}", e.getMessage());
            return List.of();
        }
    }

    public void ejecutarMatcheo() {
        String url = donacionesServiceUrl + "/donaciones/matcheador/ejecutar";
        log.info("Ejecutando matchmaking a demanda en: {}", url);
        webApiCaller.post(url, Map.of(), Object.class);
    }

    public void seleccionarMatcheo(Long idMatcheo, Long idNecesidadRecibidora) {
        if (idMatcheo == null || idNecesidadRecibidora == null) {
            throw new IllegalArgumentException("Los IDs de matcheo y necesidad no pueden ser nulos");
        }
        String url = donacionesServiceUrl + "/donaciones/matcheador/" + idMatcheo + "/" + idNecesidadRecibidora;
        log.info("Seleccionando necesidad {} para matcheo {} en: {}", idNecesidadRecibidora, idMatcheo, url);
        webApiCaller.post(url, Map.of("nombre", "Administrador"), Void.class);
    }

    public List<SubcategoriaResponse> obtenerSubcategorias() {
        try {
            String url = donacionesServiceUrl + "/subcategorias";
            log.info("Obteniendo todas las subcategorias en: {}", url);
            List<SubcategoriaResponse> res = webApiCaller.getList(url, SubcategoriaResponse.class);
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.error("Error al obtener subcategorias: {}", e.getMessage());
            return List.of();
        }
    }

    public List<CategoriaResponseSinSubcategoria> obtenerCategorias() {
        try {
            String url = donacionesServiceUrl + "/categorias?incluirSubcategorias=false";
            log.info("Obteniendo todas las categorias en: {}", url);
            List<CategoriaResponseSinSubcategoria> res = webApiCaller.getList(url, CategoriaResponseSinSubcategoria.class);
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.error("Error al obtener categorias: {}", e.getMessage());
            return List.of();
        }
    }

    public SubcategoriaResponse crearSubcategoria(SubcategoriaRequest request) {
        String url = donacionesServiceUrl + "/subcategorias";
        log.info("Creando subcategoria {} en: {}", request.nombre(), url);
        return webApiCaller.post(url, request, SubcategoriaResponse.class);
    }

    public void registrarDonacionEnDeposito(ar.edu.utn.frba.dds.front_end_app.dto.donante.CrearDonacionRequest request) {
        if (request == null || request.idDonante() == null) {
            throw new IllegalArgumentException("El donante es obligatorio");
        }
        String url = donacionesServiceUrl + "/donaciones";
        log.info("Registrando donacion en deposito para donante {}: {}", request.idDonante(), url);
        try {
            webApiCaller.post(url, request, Object.class);
        } catch (RuntimeException e) {
            String mensajeNegocio = extraerMensajeDeError400(e);
            if (mensajeNegocio != null) {
                throw new IllegalArgumentException(mensajeNegocio, e);
            }
            throw e;
        }
    }

    /**
     * WebApiCallerService envuelve los errores del backend y pierde el mensaje de negocio.
     * Si la causa es un 400 con el ErrorResponse del donaciones-service, devuelve su "message".
     */
    private String extraerMensajeDeError400(RuntimeException e) {
        if (!(e.getCause() instanceof WebClientResponseException respuesta)
            || respuesta.getStatusCode() != HttpStatus.BAD_REQUEST) {
            return null;
        }
        try {
            tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
            Map<String, Object> cuerpo = mapper.readValue(
                respuesta.getResponseBodyAsString(),
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
            Object mensaje = cuerpo.get("message");
            return mensaje != null && !mensaje.toString().isBlank() ? mensaje.toString() : null;
        } catch (Exception parseError) {
            log.warn("No se pudo leer el mensaje de error del backend: {}", parseError.getMessage());
            return null;
        }
    }

    public void registrarDonacionEnDeposito(Long idDonante, String descripcion, String nombreItem, Double cantidad, String estadoItem) {
        if (idDonante == null) {
            throw new IllegalArgumentException("Debe seleccionar un donante");
        }
        ar.edu.utn.frba.dds.front_end_app.dto.donante.BienRequest bien = new ar.edu.utn.frba.dds.front_end_app.dto.donante.BienRequest(
            nombreItem != null && !nombreItem.isBlank() ? nombreItem : "Bienes donados",
            descripcion,
            cantidad != null && cantidad > 0 ? cantidad : 1.0,
            "Unidad",
            null,
            estadoItem != null && !estadoItem.isBlank() ? estadoItem : "USADO",
            null,
            null
        );
        ar.edu.utn.frba.dds.front_end_app.dto.donante.CrearDonacionRequest request = new ar.edu.utn.frba.dds.front_end_app.dto.donante.CrearDonacionRequest(
            idDonante,
            descripcion,
            List.of(bien)
        );
        registrarDonacionEnDeposito(request);
    }

    public void registrarDonacionEnDeposito(Long idDonante, String descripcion, String bienesJson) {
        if (idDonante == null) {
            throw new IllegalArgumentException("Debe seleccionar un donante");
        }
        List<ar.edu.utn.frba.dds.front_end_app.dto.donante.BienRequest> bienes = new java.util.ArrayList<>();
        if (bienesJson != null && !bienesJson.isBlank()) {
            try {
                tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
                bienes = mapper.readValue(bienesJson, new tools.jackson.core.type.TypeReference<List<ar.edu.utn.frba.dds.front_end_app.dto.donante.BienRequest>>() {});
            } catch (Exception e) {
                log.error("Error al deserializar lista de bienes: {}", e.getMessage());
                throw new IllegalArgumentException("Formato de bienes inválido: " + e.getMessage());
            }
        }
        if (bienes.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar al menos un bien a donar");
        }
        ValidationException validationException = new ValidationException("Errores de validacion");
        bienes.forEach(b ->{
            if(b.nombre() == null){
                validationException.addFieldError("Bien sin nombre: ", "El bien debe tener un nombre.");
            }
            if(b.cantidad() != null){
                if(b.cantidad()<=0){
                    validationException.addFieldError(b.nombre() + ": ", "La cantidad debe ser mayor a 0");
                }
            }
            else{
                validationException.addFieldError(b.nombre() + ": ", "El bien debe tener cantidad");
            }
            if(b.estado() == null || (!b.estado().equals("NUEVO") && !b.estado().equals("USADO"))){
                validationException.addFieldError(b.nombre() + ": ", "El estado del bien debe ser Nuevo o Usado.");
            }
            if(b.fechaVencimiento() != null && b.fechaVencimiento().isBefore(LocalDate.now())){
                validationException.addFieldError(b.nombre() + ": ", "La fecha de vencimiento tiene que ser en el futuro.");
            }
            if(b.subcategoriaId() == null){
                validationException.addFieldError(b.nombre() + ": ", "La subcategoría es obligatoria");
            }

        });
        if(validationException.hasFieldErrors()){
            throw validationException;
        }
        ar.edu.utn.frba.dds.front_end_app.dto.donante.CrearDonacionRequest request = new ar.edu.utn.frba.dds.front_end_app.dto.donante.CrearDonacionRequest(idDonante, descripcion, bienes);
        registrarDonacionEnDeposito(request);
    }

    public void confirmarDonacion(Long donacionId, CambiarEstadoRequest request) {
        try {
            String url = donacionesServiceUrl + "/donaciones/" + donacionId.toString() + "/estado";
            log.info("Llamando a donaciones-service en: {}", url);

            webApiCaller.patch(url, request, Void.class);
        } catch (Exception e) {
            log.error("Error al confirmar donacion: {}", e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }

    public List<CasoExitoDto> obtenerCasosExito() {
        try {
            String url = donacionesServiceUrl + "/donaciones/casos-exito";
            org.springframework.web.reactive.function.client.ExchangeStrategies strategies =
                org.springframework.web.reactive.function.client.ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
                    .build();
            List<CasoExitoDto> res = org.springframework.web.reactive.function.client.WebClient.builder()
                .exchangeStrategies(strategies)
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(CasoExitoDto.class)
                .collectList()
                .block();
            return res != null ? res : List.of();
        } catch (Exception e) {
            log.warn("No se pudieron cargar casos de éxito desde donaciones-service: {}", e.getMessage());
            return List.of();
        }
    }

    public List<DonacionVistaDto> obtenerPorDonante(Long donanteId) {
        try {
            String url = donacionesServiceUrl + "/donaciones/donante/" + donanteId;
            log.info("Llamando a donaciones-service en: {}", url);

            // Llamada delegada al servicio interceptor
            List<DonacionResponseDonante> response = webApiCaller.getList(url, DonacionResponseDonante.class);

            return DonacionMapper.aVista(response != null ? response : List.<DonacionResponseDonante>of());
        }
        catch (Exception e) {
            log.error("Error al obtener donaciones de {} para donante {}: {}", donacionesServiceUrl, donanteId, e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }

    public EntidadesAyudadasResponse contarEntidadesAyudadas(Long donanteId) {
        try {
            String url = donacionesServiceUrl + "/donaciones/donante/" + donanteId + "/entidades-ayudadas-este-mes";
            log.info("Llamando a donaciones-service en: {}", url);

            // Llamada delegada al servicio interceptor
            EntidadesAyudadasResponse response = webApiCaller.get(url, EntidadesAyudadasResponse.class);

            if (response == null) {
                throw new IllegalStateException("Respuesta vacía del conteo de entidades ayudadas");
            }
            return response;
        } catch (BackendNoDisponibleException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener conteo de entidades ayudadas de {} para donante {}: {}", donacionesServiceUrl, donanteId, e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }
}
