package ar.edu.utn.frba.dds.incentivos.main.dtos;

public record DonanteRankingItemResponse(
    Integer puesto,
    Long idDonante,
    Long idDonanteDonacionesService,
    String nombreUsuario,
    Integer cantidadDonaciones,
    Long misionesCompletadas
) {}
