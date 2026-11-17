package ar.edu.utn.frba.dds.front_end_app.services;

import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonanteDto;
import ar.edu.utn.frba.dds.front_end_app.dto.*;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.auth.AuthApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonanteApiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccesoServiceImpl {

    private final DonanteApiService donanteApiService;
    private final AuthApiService authApiService;

    public AccesoServiceImpl(DonanteApiService donanteApiService, AuthApiService authApiService) {
        this.donanteApiService = donanteApiService;
        this.authApiService = authApiService;
    }

    public List<DonanteDto> getAll() {
        return donanteApiService.getAll();
    }

    public void registrarDonante(RegistroDonanteForm form) {
        validarDatosDonante(form);

        String nombreCompleto = form.nombre() + " " + form.apellido();
        UsuarioResponseDTO usuarioCreado = authApiService.registrar(
            form.username(),
            form.password(),
            form.confirmPassword(),
            nombreCompleto,
            "DONANTE"
        );

        List<MedioDeContactoCreateDto> contactos = armarMediosDeContacto(form);

        HumanoCreateRequest donanteReq = new HumanoCreateRequest(
            form.nombre(),
            form.apellido(),
            form.fechaDeNacimiento(),
            form.genero(),
            form.numeroDeDocumento(),
            form.calle(),
            form.altura(),
            usuarioCreado != null ? usuarioCreado.id() : null,
            contactos
        );
        donanteApiService.crearDonante(donanteReq);
    }

    private void validarDatosDonante(RegistroDonanteForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Los datos del formulario son requeridos.");
        }
        if (form.nombre() == null || form.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (form.apellido() == null || form.apellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (form.numeroDeDocumento() == null || form.numeroDeDocumento().isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
    }

    private List<MedioDeContactoCreateDto> armarMediosDeContacto(RegistroDonanteForm form) {
        List<MedioDeContactoCreateDto> contactos = new ArrayList<>();
        if (form.contactoTipo() != null && form.contactoValor() != null) {
            for (int i = 0; i < form.contactoTipo().size(); i++) {
                String tipo = form.contactoTipo().get(i);
                String valor = (i < form.contactoValor().size()) ? form.contactoValor().get(i) : "";
                if (valor != null && !valor.isBlank()) {
                    boolean esPref = form.contactoPreferido() != null && form.contactoPreferido().contains(i);
                    contactos.add(new MedioDeContactoCreateDto(tipo, valor, esPref));
                }
            }
        }
        if (!contactos.isEmpty() && contactos.stream().noneMatch(c -> Boolean.TRUE.equals(c.esPreferido()))) {
            MedioDeContactoCreateDto primero = contactos.get(0);
            contactos.set(0, new MedioDeContactoCreateDto(primero.tipo(), primero.valor(), true));
        }
        return contactos;
    }
}
