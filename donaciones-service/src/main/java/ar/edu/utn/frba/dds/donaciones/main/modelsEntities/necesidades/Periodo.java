package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import java.time.LocalDate;
import java.time.Period;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.converters.PeriodConverter;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity @Table(name="Periodos")
public class Periodo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Periodo(Period periodoDeLaNecesidad, LocalDate fechaInicio, Double cantidadDonada,Necesidad necesidad) {
    this.periodoDeLaNecesidad = periodoDeLaNecesidad;
    this.fechaInicio = fechaInicio;
    LocalDate finTentativo = fechaInicio.plus(periodoDeLaNecesidad);

    if (periodoDeLaNecesidad.getDays() == 0) {
      int diaOriginal = necesidad.getInicioRecurrencia().getDayOfMonth();
      int maxDiasMesDestino = finTentativo.lengthOfMonth();
      int diaFinal = Math.min(diaOriginal, maxDiasMesDestino);
      this.fechaFin = finTentativo.withDayOfMonth(diaFinal);
    } else {
      this.fechaFin = finTentativo;
    };
    this.cantidadDonada = cantidadDonada;
    this.id = null;
    this.necesidad = necesidad;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_necesidad")
  private Necesidad necesidad;

  @Convert( converter = PeriodConverter.class)
  @Column(name="periodo")
  private Period periodoDeLaNecesidad;

  @Column(name="fecha_inicio")
  private LocalDate fechaInicio;

  @Column(name="fecha_fin")
  private LocalDate fechaFin;

  @Column(name="cantidad_donada")
  private Double cantidadDonada;

}
