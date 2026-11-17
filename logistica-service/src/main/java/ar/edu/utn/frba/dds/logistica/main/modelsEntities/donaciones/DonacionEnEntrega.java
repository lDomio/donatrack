package ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import dev.morphia.annotations.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class DonacionEnEntrega {
  private Long id;
  private String subcategoria;
  private Double cantidad;
  private Double pesoKg;
  private Long donanteId;
}
