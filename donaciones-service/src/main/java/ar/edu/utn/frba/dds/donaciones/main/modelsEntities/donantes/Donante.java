package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.FormularioDeDonacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
@AllArgsConstructor
*/
@NoArgsConstructor
@Entity @Table (name="Donantes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo_persona")
public abstract class Donante {

  @Getter
  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_donante")
  private Long id;

  @Getter
  @Setter
  @Column(name = "id_usuario_auth")
  private Long idUsuarioAuth;

// El valor "donante" hace referencia explícita al nombre del atributo en la clase FormularioDeDonacion
  @OneToOne(mappedBy = "donante", cascade = CascadeType.ALL) //uso mapped by para resolver la bidireccionalidad uno a uno, el FK va a estar en FormularioDeDonacion
  private FormularioDeDonacion bienesADonar;

  @Getter
  @Setter
  @Column(name = "estaActivo")
  private Boolean estaActivo = true;

  /**
  acá hay un problema: el donante deberia tener muchos formularios de donacion, porque deberia poder donar muchos donaciones en cada uno
  Entonces, si la relacion es uno a uno, hay que cambiar como se mapea la entidad en FormularioDeDonacion
    si la relacion es uno a muchos, hay que cambiar aca la logica porque deberia ser una Lista de formularios de donacion
*/
  @Getter
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name="id_titularDonante", referencedColumnName="id_donante")
  private List<MedioDeContacto> contactos = new ArrayList<>();

  public void agregarContacto(MedioDeContacto contacto) {
    if (contacto != null) {
      this.contactos.add(contacto);
    }
  }
}
