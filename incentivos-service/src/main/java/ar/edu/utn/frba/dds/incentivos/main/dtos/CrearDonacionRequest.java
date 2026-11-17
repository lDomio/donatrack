package ar.edu.utn.frba.dds.incentivos.main.dtos;

import java.time.LocalDate;

public record CrearDonacionRequest(Long idDonacion,Long idDonante, LocalDate fechaCreacion, Double CantidadDeBien, String subcategoria, String categoria, Long entidadBeneficiaria) {
}
