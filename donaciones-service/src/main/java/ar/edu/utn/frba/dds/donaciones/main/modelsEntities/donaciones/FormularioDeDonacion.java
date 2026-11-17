package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Bloque 3 del DER - header liviano de la carga original.
 * donante 1--N formulario_de_donacion  y  formulario_de_donacion 1--N donacion
 * (esta ultima FK vive en donacion.formulario_id, la mapea Donacion).
 */
@Entity
@Table(
    name = "formulario_de_donacion",
    indexes = @Index(name = "idx_formulario_donante", columnList = "id_donante", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormularioDeDonacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public FormularioDeDonacion(Donante donante, LocalDate fechaDeCreacion, String descripcionGeneral, List<Bien> bienes) {
    this.donante = donante;
    this.fechaDeCreacion = fechaDeCreacion;
    this.descripcionGeneral = descripcionGeneral;
    this.bienes = bienes;
  }

  @OneToOne
  @JoinColumn(name = "id_donante", referencedColumnName = "id_donante")
  private Donante donante;

  /**
   * Lado duenio de donante 1--N formulario_de_donacion.
   * Del lado de Donante, el campo bienesADonar tiene que quedar como
   * {@code @OneToOne(mappedBy = "donante")} (o pasar a lista) para no duplicar
   * la columna donante_id.
   *
   * EDIT 07/sept: al parecer, el donante tiene solo 1 formularioDeDonacion, no una lista de formularios
   * por lo que la relaciones 1 a 1. edito el atributo abajo para que corresponda con esta logica y dejo comentado lo anterior
   */
  //@ManyToOne(fetch = FetchType.LAZY)
  //@JoinColumn(name = "donante_id", referencedColumnName = "id")
  //private Donante donante;



  @Column(name = "fecha_creacion")
  private LocalDate fechaDeCreacion;

  @Column(name = "descripcion_general", length = 500)
  private String descripcionGeneral;

  /**
   * Carga cruda previa a la segmentacion: no se persiste. Los bienes quedan
   * en la base colgando de la donacion que genera el
   * SegmentadorDeFormularioDonacion (bien.donacion_id), como define el DER.
   */
  @Transient
  @Getter(lombok.AccessLevel.NONE)
  private List<Bien> bienes = new ArrayList<>();

  public void agregarBien(Bien bien) {
    this.bienes.add(bien);
  }

  public List<Bien> getBienes() {
    return this.bienes;
  }
}
