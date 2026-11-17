package ar.edu.utn.frba.dds.incentivos.main.dtos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;

public record InsigniaResponse(
        String descripcion
) {
    public static InsigniaResponse from(Insignia insignia) {
        return new InsigniaResponse(insignia.getDescripcion());
    }
}
