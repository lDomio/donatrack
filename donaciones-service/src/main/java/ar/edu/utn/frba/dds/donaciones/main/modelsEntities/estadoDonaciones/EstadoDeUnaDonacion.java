package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity @Table(name="EstadosDeDonaciones")
public class EstadoDeUnaDonacion {
  public EstadoDeUnaDonacion(TipoDeEstado tipo, LocalDate fecha, String descripcion, String autorDelCambioDeEstado) {
    this.tipo = tipo;
    this.fecha = fecha;
    this.descripcion = descripcion;
    this.autorDelCambioDeEstado = autorDelCambioDeEstado;
  }
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated( value = EnumType.STRING )
  @Column(name = "tipo_estado")
  private TipoDeEstado tipo;

  @Column( name = "fecha")
  private LocalDate fecha;

  @Column( name = "descripcion" )
  private String descripcion;

  @Column( name = "autor_del_cambio" )
  private String autorDelCambioDeEstado;

}
