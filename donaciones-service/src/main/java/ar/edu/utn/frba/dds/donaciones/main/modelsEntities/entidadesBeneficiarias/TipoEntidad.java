package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class TipoEntidad {
    @Column(name="tipoEntidadBeneficiaria")
    private String tipoEntidad;
}
