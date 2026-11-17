package ar.edu.utn.frba.dds.autenticacion.dto;

import ar.edu.utn.frba.dds.autenticacion.modelEntities.Permisos;
import ar.edu.utn.frba.dds.autenticacion.modelEntities.TipoRol;
import lombok.Builder;

import java.util.List;

@Builder
public record UserRolesPermissionsDTO(
    String username,
    TipoRol rol,
    List<Permisos> permisos
) {}
