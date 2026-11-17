package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Periodo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.SQLJoinTableRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity @Table(name = "Donaciones")
public class Donacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Donacion(Long idDonante, LocalDate fechaDeCreacion, Subcategoria subcategoria, List<Bien> bienes, String descripcionGeneral, Necesidad necesidad, Periodo periodo, List<EstadoDeUnaDonacion> estadosDeDonaciones) {
        this.idDonante = idDonante;
        this.fechaDeCreacion = fechaDeCreacion;
        this.subcategoria = subcategoria;
        this.bienes = bienes;
        this.descripcionGeneral = descripcionGeneral;
        this.necesidad = necesidad;
        this.periodo = periodo;
        this.estadosDeDonaciones = estadosDeDonaciones;
    }

    // Se usa para la lógica de la aplicación, no existe en el DER
    @Column(name="id_donante", nullable = true)
    private Long idDonante;

    @Column(name="fecha_creacion", nullable = false)
    private LocalDate fechaDeCreacion;

    /**
     * Esto se podría quitar, pero para no romper nada ahora, vamos a dejarlo como está
     * El paso correcto sería que cada bien tenga una subcategoría y que sean iguales para todos4
     * No persistirlo en la donación
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="subcategoria_id",
            referencedColumnName = "id"
    )
    private Subcategoria subcategoria;

    @Getter(lombok.AccessLevel.NONE)
    @OneToMany( mappedBy = "donacion", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Bien> bienes = new ArrayList<>();

    @Column(name="descripcion_general")
    private String descripcionGeneral;

    @Column(name="foto_entrega", columnDefinition = "LONGTEXT")
    private String fotoEntrega;

    @Getter
    // No recuerdo si la cardinalidad es correcta
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="necesidad_id")
    private Necesidad necesidad;

    @ManyToOne( optional = true ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="periodo_id")
    private Periodo periodo;

    @Getter(lombok.AccessLevel.NONE)
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn( name = "donacion_id" )
    private List<EstadoDeUnaDonacion> estadosDeDonaciones = new ArrayList<>();

    public void agregarBien(Bien bien) {
        bien.setDonacion(this);
        this.bienes.add(bien);
    }

    public String getNombreBien(){return this.bienes.getFirst().getNombre();}

    public List<Bien> getBienes() {
        return this.bienes;
    }

    public void agregarEstado(EstadoDeUnaDonacion estado) {
        this.estadosDeDonaciones.add(estado);
    }

    public List<EstadoDeUnaDonacion> getEstadosDeDonaciones() {
        return this.estadosDeDonaciones;
    }

    public Double getCantidad(){
        return bienes.stream().mapToDouble(Bien::getCantidad).sum();
    }

    public LocalDate fechaUltimoEstado() {
        return this.getEstadosDeDonaciones().getLast().getFecha();
    }

    public Boolean noEstaVencida(){
        return bienes.stream().anyMatch(bien -> bien.noEstaVencido());
    }

    public TipoDeEstado getUltimoEstado(){
        return this.getEstadosDeDonaciones().getLast().getTipo();
    }

     public LocalDate fechaMasProximaDeVencimiento() {
        return this.getBienes().stream()
        .map(Bien::getFechaVencimiento)          // extraemos las fechas
        .filter(Objects::nonNull)                // eiltramos por si alguno es null
        .min(LocalDate::compareTo)               // buscamos la fecha mas proxima
        .orElse(null);                     //si la lista esta vacia, devolvemos null
    }


    public Boolean puedeCambiarDeEstado(TipoDeEstado nuevoEstado){
        return switch (this.getUltimoEstado()){
            case TipoDeEstado.DEPOSITO -> nuevoEstado == TipoDeEstado.VENCIDO || nuevoEstado == TipoDeEstado.ASIGNACION_REALIZADA;
            case TipoDeEstado.ASIGNACION_REALIZADA -> nuevoEstado == TipoDeEstado.LISTA_PARA_ENTREGAR || nuevoEstado == TipoDeEstado.EN_TRASLADO || nuevoEstado == TipoDeEstado.DEPOSITO;
            case TipoDeEstado.LISTA_PARA_ENTREGAR -> nuevoEstado == TipoDeEstado.EN_TRASLADO || nuevoEstado == TipoDeEstado.DEPOSITO || nuevoEstado == TipoDeEstado.ASIGNACION_REALIZADA;
            case TipoDeEstado.EN_TRASLADO -> nuevoEstado == TipoDeEstado.ENTREGADA || nuevoEstado == TipoDeEstado.ENTREGA_FALLIDA || nuevoEstado == TipoDeEstado.DEPOSITO;
            case TipoDeEstado.ENTREGA_FALLIDA -> nuevoEstado == TipoDeEstado.DEPOSITO || nuevoEstado == TipoDeEstado.VENCIDO;
            case TipoDeEstado.ENTREGADA , TipoDeEstado.VENCIDO -> false;
        };
    }

    public void asignarNecesidad(Necesidad necesidad) {
        this.necesidad = necesidad;
        this.periodo = necesidad.getUltimoPeriodo().orElse(null);
        necesidad.agregarDonacion(this);
    }
    public void desasignar(){
        if (this.necesidad != null){
            this.necesidad.desasignar(this);
            this.necesidad = null;
            this.periodo = null;
        }
    }
}
