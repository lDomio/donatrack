package ar.edu.utn.frba.dds.front_end_app.dto.entidad_beneficiaria;

import java.util.List;

public record RegistroEntidadBeneficiariaForm(
    String razonSocial,
    String tipoEntidad,
    String calle,
    Integer altura,
    String telefono,
    String nombreRepresentante,
    String emailRepresentante,
    String username,
    String password,
    String confirmPassword
) {

  public EntidadBeneficiariaRequest toRequest(Long idUsuarioAuth) {
    return new EntidadBeneficiariaRequest(
        razonSocial,
        tipoEntidad,
        calle,
        altura,
        List.of(new MedioDeContactoDTO(TipoDeContacto.TELEFONO, telefono)),
        List.of(new RepresentanteDTO(
            nombreRepresentante,
            new MedioDeContactoDTO(TipoDeContacto.EMAIL, emailRepresentante))),
        idUsuarioAuth
    );
  }
}
