package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.converters.PeriodConverter;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.converters.TipoNecesidadConverter;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import jakarta.persistence.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "Necesidades")
public class Necesidad {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    public Necesidad(Double cantidad, Double cantidadDonadaTotal, LocalDate fechaCreacion, Subcategoria subcategoria, String descripcion, TipoNecesidad tipoDeNecesidad, String UnidadDeMedida, Period periodoCorrespondiente, LocalDate inicioRecurrencia, EntidadBeneficiaria entidadBeneficiaria, List<Periodo> historialPeriodos, Double cantidadDonada) {
        this.cantidad = cantidad;
        this.cantidadDonadaTotal = cantidadDonadaTotal;
        this.fechaCreacion = fechaCreacion;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.tipoDeNecesidad = tipoDeNecesidad;
        this.UnidadDeMedida = UnidadDeMedida;
        this.periodoCorrespondiente = periodoCorrespondiente;
        this.inicioRecurrencia = inicioRecurrencia;
        this.entidadBeneficiaria = entidadBeneficiaria;
        this.historialPeriodos = historialPeriodos;
        this.cantidadDonada = cantidadDonada;
    }

    @Column(name = "cantidad")
    private Double cantidad;

    @Column(name = "cantidad_donada_total")
    private Double cantidadDonadaTotal = 0.0;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "subcategoria_id" )
    private Subcategoria subcategoria;

    @Column( name = "descripcion" )
    private String descripcion;

    @Convert( converter = TipoNecesidadConverter.class )
    @Column( name = "tipo_necesidad" )
    private TipoNecesidad tipoDeNecesidad;

    @Column( name = "unidad_medida")
    private String UnidadDeMedida;

    @Convert( converter = PeriodConverter.class )
    @Column( name = "periodo_correspondiente" )
    private Period periodoCorrespondiente;

    @Column( name = "inicio_recurrencia" )
    private LocalDate inicioRecurrencia;

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn( name = "entidad_beneficiaria_id")
    private EntidadBeneficiaria entidadBeneficiaria;

    @OneToMany(
        mappedBy = "necesidad",
        cascade = CascadeType.ALL
    )
    private List<Periodo> historialPeriodos = new ArrayList<>();

    @OneToMany(mappedBy = "necesidad",cascade = CascadeType.ALL)
    private List<Donacion> donacionesAsignadas = new ArrayList<>();

    // Se pasó de Periodo a esta entidad
    @Column(name="cantidad_donada", columnDefinition = "FLOAT")
    private Double cantidadDonada;


    public List<Donacion> getDonacionesUltimoTrimestreDeEntidad(){
        return entidadBeneficiaria.ultimasDonacionesDelTrimestre();
    }
    private void aumentarCantidad(Double cantidad){
        this.getTipoDeNecesidad().aumentarCantidad(this, cantidad, this.historialPeriodos);
    }

    public Double getCantidadDonada(){
        return this.tipoDeNecesidad.getCantidadDonada(this, this.historialPeriodos);
    }
    public TipoNecesidadEnum getTipoNecesidad(){
        return this.tipoDeNecesidad.getTipoNecesidad();
    }

    public Optional<Periodo> getUltimoPeriodo(){
        Periodo periodo= this.tipoDeNecesidad.getUltimoPeriodo(this, this.historialPeriodos);
        if (periodo == null){
            return Optional.empty();
        }
        return Optional.of(periodo);
    }
    public void agregarDonacion(Donacion donacion){
        getDonacionesAsignadas().add(donacion);
        this.getEntidadBeneficiaria().agregarDonacion(donacion);
        this.aumentarCantidad(donacion.getCantidad());
    }
    public void desasignar(Donacion donacion){
        donacionesAsignadas.removeIf(d -> d.getId().equals(donacion.getId()));
        this.entidadBeneficiaria.removerDonacion(donacion);
        this.tipoDeNecesidad.desasignar(donacion);
    }

    public List<Periodo> getHistorialPeriodos() {
        this.tipoDeNecesidad.actualizarPeriodos(this, this.historialPeriodos);
        return this.historialPeriodos;
    }
}