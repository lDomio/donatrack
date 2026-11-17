package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaResponseSinSubcategoria;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadDTO;
import ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadVistaDto;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.NotFoundException;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadDetalleMapper;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper.EntidadVistaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class EntidadBeneficiariaApiService {

    private static final Logger log = LoggerFactory.getLogger(EntidadBeneficiariaApiService.class);
    private final String donacionesServiceUrl;
    private final WebApiCallerService webApiCallerService;

    public EntidadBeneficiariaApiService(
        @Value("${donaciones.service.url:http://localhost:8080/donaciones-service}") String donacionesServiceUrl,
        WebApiCallerService webApiCallerService) {
        this.donacionesServiceUrl = donacionesServiceUrl;
        this.webApiCallerService = webApiCallerService;
    }

    public EntidadBeneficiariaDTO crear(EntidadBeneficiariaRequest request) {
        String url = donacionesServiceUrl + "/entidades-beneficiarias";
        log.info("Llamando a donaciones-service en: {}", url);
        return webApiCallerService.post(url, request, EntidadBeneficiariaDTO.class);
    }

    public EntidadBeneficiariaDTO getById(Long id) {
        return webApiCallerService.get(donacionesServiceUrl + "/entidades-beneficiarias/" + id.toString(), EntidadBeneficiariaDTO.class);
    }

    public List<NecesidadDTO> getNecesidadesByIdEntidad(Long idEntidad) {
        return webApiCallerService.getList(donacionesServiceUrl + "/entidades-beneficiarias/" + idEntidad.toString() + "/necesidades", NecesidadDTO.class);
    }

    public List<CategoriaResponseSinSubcategoria> getCategorias() {
        return webApiCallerService.getList(donacionesServiceUrl + "/categorias?incluirSubcategorias=false", CategoriaResponseSinSubcategoria.class);
    }

    public NecesidadDTO getNecesidadById(Long idEntidad) {
        return webApiCallerService.get(donacionesServiceUrl + "/entidades-beneficiarias/necesidades/" + idEntidad.toString(), NecesidadDTO.class);
    }

    public List<SubcategoriaResponse> getSubcategorias(Long categoriaId) {
        return webApiCallerService.getList(donacionesServiceUrl + "/subcategorias?categoriaId=" + categoriaId.toString(), SubcategoriaResponse.class);
    }

    public void crearNecesidad(Long entidadId, NecesidadRequest necesidad) {
        webApiCallerService.post(donacionesServiceUrl + "/entidades-beneficiarias/" + entidadId.toString() + "/necesidades", necesidad, Void.class);
    }

    public List<DonacionResponseEntidad> getDonacionesByIdEntidad(Long idEntidad) {
        return webApiCallerService.getList(donacionesServiceUrl + "/entidades-beneficiarias/" + idEntidad.toString() + "/donaciones", DonacionResponseEntidad.class);
    }

    public EntidadBeneficiariaDTO obtenerPorIdUsuarioAuth(Long idUsuarioAuth) {
        if (idUsuarioAuth == null) {
            return null;
        }
        return webApiCallerService.get(donacionesServiceUrl + "/entidades-beneficiarias/usuario/" + idUsuarioAuth, EntidadBeneficiariaDTO.class);
    }

    public List<EntidadVistaDto> obtenerTodas() {
        return obtenerConFiltros(null, null);
    }

    /**
     * Lista con filtros opcionales. Valores blank se omiten (no filtran).
     */
    public List<EntidadVistaDto> obtenerConFiltros(String busqueda, String tipo) {
        try {
            URI uri = construirUrl(donacionesServiceUrl, busqueda, tipo);
            log.info("Llamando a donaciones-service en: {}", uri);

            // Llamada delegada al servicio interceptor
            List<EntidadBeneficiariaResponse> response = webApiCallerService.getList(uri, EntidadBeneficiariaResponse.class);

            return EntidadVistaMapper.aVista(response != null ? response : List.of());
        } catch (Exception e) {
            log.error("Error al obtener entidades beneficiarias de {}: {}", donacionesServiceUrl, e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }

    /** Construye la URL con query params; blank = ausente (no filtra). */
    static URI construirUrl(String base, String busqueda, String tipo) {
        UriComponentsBuilder builder =
            UriComponentsBuilder.fromUriString(base + "/entidades-beneficiarias");
        if (busqueda != null && !busqueda.isBlank()) {
            builder.queryParam("busqueda", busqueda);
        }
        if (tipo != null && !tipo.isBlank()) {
            builder.queryParam("tipo", tipo);
        }
        return builder.build().encode().toUri();
    }

    public EntidadDetalleVistaDto obtenerPorId(Long id) {
        try {
            String url = donacionesServiceUrl + "/entidades-beneficiarias/" + id;
            log.info("Llamando a donaciones-service en: {}", url);

            // Llamada delegada al servicio interceptor
            EntidadBeneficiariaResponse response = webApiCallerService.get(url, EntidadBeneficiariaResponse.class);

            if (response == null) {
                throw new BackendNoDisponibleException("donaciones-service",
                    new IllegalStateException("respuesta vacia de " + url));
            }
            return EntidadDetalleMapper.aVista(response);
        } catch (NotFoundException e) {
            // El webApiCaller lanza NotFoundException genérica ante un 404. La re-lanzamos con el mensaje custom.
            throw new NotFoundException("entidad beneficiaria", String.valueOf(id));
        } catch (Exception e) {
            log.error("Error al obtener entidad beneficiaria {} de {}: {}", id, donacionesServiceUrl, e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }
}