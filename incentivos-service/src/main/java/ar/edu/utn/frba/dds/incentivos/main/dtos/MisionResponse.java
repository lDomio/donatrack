package ar.edu.utn.frba.dds.incentivos.main.dtos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;

import java.time.LocalDate;

public record MisionResponse(
        InsigniaResponse insignia,
        LocalDate fecha
) {
    public static MisionResponse from(MisionCompletada mision) {
        return new MisionResponse(
                mision.getInsigniaOtorgada() != null ? InsigniaResponse.from(mision.getInsigniaOtorgada()) : null,
                mision.getFechaDeComplecion()
        );
    }
}
