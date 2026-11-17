package ar.edu.utn.frba.dds.incentivos.main.dtos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;

import java.time.LocalDate;
import java.util.List;

public record DonanteResponse(
    Long idUsuario,
    Long idDonanteDonacionesService,
    List<DonacionDto> donaciones,
    CategoriaDonanteDto categoria,
    List<InsigniaResponse> insignias,
    List<MisionResponse> misionesCompletadas,
    MisionDto misionActual,
    List<CategoriaCompletadaDto> categoriasCompletadas,
    ProgresoMisionDto progresoMisionActual,
    List<MedioDeContactoDto> medioContacto,
    LocalDate fechaInicioMisionActual,
    Integer cantidadDeEntidadesQueAyudo
) {}
