package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Bloque 3 del DER - donaciones y trazabilidad.
 * Cada bien cuelga de la donacion que resulto de segmentar el formulario
 * (donacion 1--N bien). La Foto va embebida (foto_url / foto_descripcion).
 */
@Entity
@Table(
    name = "bien",
    indexes = {
        @Index(name = "idx_bien_donacion", columnList = "donacion_id"),
        @Index(name = "idx_bien_subcategoria", columnList = "subcategoria_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bien {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Lado duenio de la relacion donacion 1--N bien.
   * El lado inverso, cuando se mapee Donacion, tiene que ser
   * {@code @OneToMany(mappedBy = "donacion", cascade = ALL, orphanRemoval = true)}
   * para no duplicar la columna donacion_id.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "donacion_id", referencedColumnName = "id")
  private Donacion donacion;

  @Column(name = "nombre", nullable = false, length = 150)
  private String nombre;

  @Column(name = "descripcion", length = 500)
  private String descripcion;

  @Embedded
  private Foto foto;

  @Column(name = "cantidad", columnDefinition = "DECIMAL(12,2)")
  private Double cantidad;

  /**
   * La subcategoria canonica del DER vive en donacion; se persiste tambien
   * aca porque el bien la conoce antes de existir la donacion (el
   * SegmentadorDeFormularioDonacion agrupa por subcategoria + estado + vencimiento).
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subcategoria_id", referencedColumnName = "id")
  private Subcategoria subcategoria;

  /** Copia de subcategoria.unidad al momento de la carga (queda historica). */
  @Column(name = "unidad_de_medida", length = 30)
  private String unidadDeMedida;

  @Column(name = "fecha_vencimiento")
  private LocalDate fechaVencimiento;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = 20)
  private EstadoDeBienNoPerecedero estado;

  public Bien(Donacion donacion, String nombre, String descripcion, Foto foto, Double cantidad, Subcategoria subcategoria, String unidadDeMedida, LocalDate fechaVencimiento, EstadoDeBienNoPerecedero estado) {
    this.donacion = donacion;
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.foto = foto;
    this.cantidad = cantidad;
    this.subcategoria = subcategoria;
    this.unidadDeMedida = unidadDeMedida;
    this.fechaVencimiento = fechaVencimiento;
    this.estado = estado;
  }


  public Boolean noEstaVencido(){

    if(this.fechaVencimiento == null){
      return true;
    }

    return this.fechaVencimiento.isAfter(LocalDate.now());
  }
}
