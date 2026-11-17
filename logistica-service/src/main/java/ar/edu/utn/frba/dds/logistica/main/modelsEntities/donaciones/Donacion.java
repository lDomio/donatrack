package ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity("donaciones")
public class Donacion {
    @Id
    private Long id;
    private Long donanteId;
    private String subcategoria;
    private Double cantidad;
    private LocalDate fechaVencimiento;
    private Double pesoKg;
    private Double volumen;
    private Long entidadId;
    private TipoDeEstadoDonacion estado = TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE;

    public Boolean puedeCambiarDeEstado(TipoDeEstadoDonacion nuevoEstado) {
        return switch (this.estado) {
            case PENDIENTE_DE_PESAJE -> nuevoEstado == TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR;
            case LISTA_PARA_PLANIFICAR -> nuevoEstado == TipoDeEstadoDonacion.EN_PLANIFICACION
                    || nuevoEstado == TipoDeEstadoDonacion.ASIGNADA_A_RUTA;
            case EN_PLANIFICACION -> nuevoEstado == TipoDeEstadoDonacion.ASIGNADA_A_RUTA
                    || nuevoEstado == TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR;
            case ASIGNADA_A_RUTA -> nuevoEstado == TipoDeEstadoDonacion.ENTREGADA
                    || nuevoEstado == TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR;
            case ENTREGADA -> false;
        };
    }

    public boolean cambiarEstado(TipoDeEstadoDonacion nuevoEstado) {
        if (!puedeCambiarDeEstado(nuevoEstado)) {
            return false;
        }
        this.estado = nuevoEstado;
        return true;
    }

    public boolean estaListaParaPlanificar() {
        return this.estado == TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR;
    }

    public boolean requierePesaje() {
        return this.estado == TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE;
    }

    public void registrarPesaje(Double pesoKg, Double volumen) {
        this.pesoKg = pesoKg;
        this.volumen = volumen;
        this.cambiarEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
    }

    public boolean noEstaVencida() {
        return fechaVencimiento == null || fechaVencimiento.isAfter(LocalDate.now());
    }
}
