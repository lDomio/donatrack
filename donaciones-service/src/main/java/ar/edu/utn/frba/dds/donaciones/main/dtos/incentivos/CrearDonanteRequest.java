package ar.edu.utn.frba.dds.donaciones.main.dtos.incentivos;

import java.util.List;

public record CrearDonanteRequest(
        Long id,
        Long idUsuarioAuth,
        String nombreUsuario,
        List<MedioDeContactoRequest> contactos
) {
}

