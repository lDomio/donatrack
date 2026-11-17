package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Representantes")
public class Representante {

  @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  @Column(name = "id_representante")
  private Long id_representante;
  @Column(name = "nombre", nullable = false)
  private String nombre;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name="id_titularRepresentante", referencedColumnName="id_representante")
  private List<MedioDeContacto> contactos = new ArrayList<>();

  public Representante(String nombre, MedioDeContacto contacto) {
    this.nombre = nombre;
    if (contacto != null) {
      this.contactos.add(contacto);
    }
  }

  public Representante(String nombre, List<MedioDeContacto> contactos) {
    this.nombre = nombre;
    if (contactos != null) {
      contactos.stream().filter(Objects::nonNull).forEach(this.contactos::add);
    }
  }

  public MedioDeContacto getContacto() {
    return this.contactos.isEmpty() ? null : this.contactos.get(0);
  }
}
