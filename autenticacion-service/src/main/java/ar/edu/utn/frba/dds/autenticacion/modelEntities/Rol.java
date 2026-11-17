package ar.edu.utn.frba.dds.autenticacion.modelEntities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_rol")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_rol", unique = true, nullable = false)
  private TipoRol tipoRol;

  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "roles_permisos",
      joinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id_rol"),
      inverseJoinColumns = @JoinColumn(name = "permiso_id", referencedColumnName = "id_permiso")
  )
  private List<Permisos> permisos = new ArrayList<>();

  public Rol(TipoRol tipoRol, List<Permisos> permisos) {
    this.tipoRol = tipoRol;
    this.permisos = permisos != null ? permisos : new ArrayList<>();
  }
}
