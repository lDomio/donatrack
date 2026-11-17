package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones;

import ar.edu.utn.frba.dds.front_end_app.dto.HumanoCreateRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.JuridicoCreateRequest;
import ar.edu.utn.frba.dds.front_end_app.dto.MedioDeContactoCreateDto;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonanteDto;
import ar.edu.utn.frba.dds.front_end_app.dto.donante.RegistroDonanteAdminForm;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonanteApiService {

    private static final Logger log = LoggerFactory.getLogger(DonanteApiService.class);
    private final String donacionesServiceUrl;
    private final WebApiCallerService webApiCaller;

    public DonanteApiService(
        @Value("${donaciones.service.url:http://localhost:8080/donaciones-service}") String donacionesServiceUrl,
        WebApiCallerService webApiCaller) {
        this.donacionesServiceUrl = donacionesServiceUrl;
        this.webApiCaller = webApiCaller;
    }

    public List<DonanteDto> getAll() {
        return getAll(null, null, null);
    }

    public List<DonanteDto> getAll(String buscar, String tipo, String estado) {
        try {
            log.info("Llamando a donaciones-service para buscar donantes con filtros");
            java.net.URI targetUrl = org.springframework.web.util.UriComponentsBuilder.fromUriString(donacionesServiceUrl + "/donantes/todosIncluyendoInactivos")
                    .queryParamIfPresent("buscar", java.util.Optional.ofNullable(buscar).filter(s -> !s.isEmpty()))
                    .queryParamIfPresent("tipo", java.util.Optional.ofNullable(tipo).filter(s -> !s.isEmpty() && !s.equals("todos")))
                    .queryParamIfPresent("estado", java.util.Optional.ofNullable(estado).filter(s -> !s.isEmpty() && !s.equals("todos")))
                    .build()
                    .toUri();

            List<DonanteDto> response = webApiCaller.getList(targetUrl.toString(), DonanteDto.class);

            return response != null ? response : List.of();
        } catch (Exception e) {
            log.error("Error al obtener donantes de {}: {}", donacionesServiceUrl, e.getMessage());
            throw new BackendNoDisponibleException("donaciones-service", e);
        }
    }

    public DonanteDto obtenerPorId(Long id) {
        if (id == null) {
            return null;
        }
        String url = donacionesServiceUrl + "/donantes/" + id;
        log.info("Obteniendo donante por id en: {}", url);
        return webApiCaller.get(url, DonanteDto.class);
    }

    public DonanteDto crearDonante(Object request) {
        String url = donacionesServiceUrl + "/donantes";
        log.info("Creando donante en donaciones-service: {}", url);

        return webApiCaller.post(url, request, DonanteDto.class);
    }

    public DonanteDto registrarDesdeFormulario(RegistroDonanteAdminForm form) {
        return registrarDesdeFormulario(form, null);
    }

    public DonanteDto registrarDesdeFormulario(RegistroDonanteAdminForm form, Long idUsuarioAuth) {
        if (form == null) {
            throw new IllegalArgumentException("El formulario no puede ser nulo");
        }
        if (form.nombreRazonSocial() == null || form.nombreRazonSocial().isBlank()) {
            throw new IllegalArgumentException("El nombre o razón social es obligatorio");
        }
        if (form.numeroDocumento() == null || form.numeroDocumento().isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }

        List<MedioDeContactoCreateDto> contactos = new ArrayList<>();
        if (form.emailDonante() != null && !form.emailDonante().isBlank()) {
            contactos.add(new MedioDeContactoCreateDto("EMAIL", form.emailDonante().trim(), true));
        }
        if (form.telefonoDonante() != null && !form.telefonoDonante().isBlank()) {
            contactos.add(new MedioDeContactoCreateDto("TELEFONO", form.telefonoDonante().trim(), false));
        }

        if ("juridica".equalsIgnoreCase(form.tipoPersona())) {
            JuridicoCreateRequest request = new JuridicoCreateRequest(
                form.nombreRazonSocial().trim(),
                "EMPRESA",
                "General",
                contactos,
                List.of()
            );
            return crearDonante(request);
        } else {
            String[] partes = form.nombreRazonSocial().trim().split("\\s+", 2);
            String nombre = partes[0];
            String apellido = partes.length > 1 ? partes[1] : "";

            HumanoCreateRequest request = new HumanoCreateRequest(
                nombre,
                apellido,
                null,
                null,
                form.numeroDocumento().trim(),
                null,
                null,
                idUsuarioAuth,
                contactos
            );
            return crearDonante(request);
        }
    }

    public DonanteDto obtenerPorIdUsuarioAuth(Long idUsuarioAuth) {
        if (idUsuarioAuth == null) {
            return null;
        }
        try {
            String url = donacionesServiceUrl + "/donantes/usuario/" + idUsuarioAuth;
            log.info("Obteniendo donante por idUsuarioAuth en: {}", url);

            return webApiCaller.get(url, DonanteDto.class);
        } catch (Exception e) {
            log.error("Error al obtener donante por idUsuarioAuth {}: {}", idUsuarioAuth, e.getMessage());
            return null;
        }
    }

    public void darDeBajaDonante(Long id) {
        String url = donacionesServiceUrl + "/donantes/" + id + "/baja";
        log.info("Dando de baja donante en donaciones-service: {}", url);
        webApiCaller.patch(url, Void.class);
    }

    public void darDeAltaDonante(Long id) {
        String url = donacionesServiceUrl + "/donantes/" + id + "/alta";
        log.info("Dando de alta donante en donaciones-service: {}", url);
        webApiCaller.patch(url, Void.class);
    }

    public DonanteDto agregarRepresentante(Long idDonante, String nombre, String apellido, String email, String telefono) {
        String url = donacionesServiceUrl + "/donantes/" + idDonante + "/representantes";
        Map<String, Object> body = construirPayloadRepresentante(nombre, apellido, email, telefono);
        log.info("Agregando representante a donante {} en: {}", idDonante, url);
        return webApiCaller.post(url, body, DonanteDto.class);
    }

    public DonanteDto editarRepresentante(Long idDonante, Long idRepresentante, String nombre, String apellido, String email, String telefono) {
        String url = donacionesServiceUrl + "/donantes/" + idDonante + "/representantes/" + idRepresentante;
        Map<String, Object> body = construirPayloadRepresentante(nombre, apellido, email, telefono);
        log.info("Editando representante {} de donante {} en: {}", idRepresentante, idDonante, url);
        return webApiCaller.put(url, body, DonanteDto.class);
    }

    public DonanteDto eliminarRepresentante(Long idDonante, Long idRepresentante) {
        String url = donacionesServiceUrl + "/donantes/" + idDonante + "/representantes/" + idRepresentante;
        log.info("Eliminando representante {} de donante {} en: {}", idRepresentante, idDonante, url);
        webApiCaller.delete(url);
        return null;
    }

    private Map<String, Object> construirPayloadRepresentante(String nombre, String apellido, String email, String telefono) {
        String nombreCompleto = ((nombre != null ? nombre.trim() : "") + " " + (apellido != null ? apellido.trim() : "")).trim();
        List<MedioDeContactoCreateDto> contactos = new ArrayList<>();
        if (email != null && !email.isBlank()) {
            contactos.add(new MedioDeContactoCreateDto("EMAIL", email.trim(), true));
        }
        if (telefono != null && !telefono.isBlank()) {
            contactos.add(new MedioDeContactoCreateDto("TELEFONO", telefono.trim(), contactos.isEmpty()));
        }
        return Map.of(
            "nombre", nombreCompleto,
            "contactos", contactos
        );
    }

    public List<DonanteDto> importarDonantesCsv(org.springframework.web.multipart.MultipartFile file) {
        String url = donacionesServiceUrl + "/donantes/importar";
        log.info("Importando donantes vía OpenCSV en donaciones-service: {}", url);
        return webApiCaller.postMultipartList(url, "file", file.getResource(), DonanteDto.class);
    }
}
