package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

import ar.edu.utn.frba.dds.front_end_app.dto.donante.DonacionResponseEntidad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EntidadBeneficiariaDTO(
    Long id,
    String razonSocial,
    String tipo,
    List<MedioDeContactoDTO> contactos,
    List<RepresentanteDTO> representantes,
    List<NecesidadDTO> necesidades,
    List<DonacionResponseEntidad> donacionesAsignadas,
    Object direccion
) {
}
