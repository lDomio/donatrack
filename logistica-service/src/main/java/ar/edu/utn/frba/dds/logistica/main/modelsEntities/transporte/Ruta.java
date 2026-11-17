package ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte;

import java.util.Comparator;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity("rutas")
public class Ruta {
    @Id
    private Long id;
    private Double distanciaARecorrer;
    private LocalDateTime fechaDeComienzoDeViaje;
    private LocalDateTime fechaFinDeViaje;

    @Setter(AccessLevel.NONE)
    private List<Parada> paradas = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private List<EstadoDeLaRuta> estadosDeLaRuta = new ArrayList<>();

    public Ruta(Long id, Double distanciaARecorrer, LocalDateTime fechaDeComienzoDeViaje, LocalDateTime fechaFinDeViaje,
            List<Parada> paradas, List<EstadoDeLaRuta> estadosDeLaRuta) {
        this.id = id;
        this.distanciaARecorrer = distanciaARecorrer;
        this.fechaDeComienzoDeViaje = fechaDeComienzoDeViaje;
        this.fechaFinDeViaje = fechaFinDeViaje;
        this.paradas = paradas != null ? new ArrayList<>(paradas) : new ArrayList<>();
        this.estadosDeLaRuta = estadosDeLaRuta != null ? new ArrayList<>(estadosDeLaRuta) : new ArrayList<>();
    }

    public void agregarParada(Parada parada) {
        this.paradas.add(parada);
    }

    public void agregarEstado(EstadoDeLaRuta estado) {
        this.estadosDeLaRuta.add(estado);
    }

    public TipoDeEstadoRuta getUltimoEstado(){
        return estadosDeLaRuta.stream().sorted(Comparator.comparing(EstadoDeLaRuta::getFecha)).toList().get(estadosDeLaRuta.size() -1).getTipoDeEstadoRuta();
    }
    public Boolean esActiva(){
        return getUltimoEstado().toString().equals(TipoDeEstadoRuta.RUTA_ASIGNADA.toString()) || getUltimoEstado().toString().equals(TipoDeEstadoRuta.VIAJE_COMENZADO.toString());
    }
}
