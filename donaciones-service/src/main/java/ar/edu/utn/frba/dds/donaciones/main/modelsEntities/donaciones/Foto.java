package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value object sin identidad: se embebe como columnas en su dueño (bien),
 * no genera tabla propia.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Foto {
  @Column(name = "foto_url", length = 500)
  private String urlFoto;

  @Column(name = "foto_descripcion", length = 255)
  private String descripcion;
}
