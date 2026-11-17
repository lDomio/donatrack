package ar.edu.utn.frba.dds.donaciones.main.dtos.incentivos;

import java.time.LocalDate;

public record CrearDonacionRequest(Long idDonacion, Long idDonante, LocalDate fechaCreacion,
                                   Double CantidadDeBien, String subcategoria, String categoria,
                                   Long entidadBeneficiaria) {
}
