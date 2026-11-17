package ar.edu.utn.frba.dds.autenticacion.modelEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permisos")
public class Permisos {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_permiso")
  private Long id;

  @Column(name = "nombre_permiso", unique = true, nullable = false)
  private String permiso;

  public Permisos(String permiso) {
    this.permiso = permiso;
  }
}
