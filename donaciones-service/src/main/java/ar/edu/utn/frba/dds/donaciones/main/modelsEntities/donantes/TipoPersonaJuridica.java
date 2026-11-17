package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class TipoPersonaJuridica {
    @Column(name="tipoPersonaJuridica")
    private String tipo;
}
