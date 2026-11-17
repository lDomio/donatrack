package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("donacionesExitosas")
public class MisionDonacionesExitosas extends EstadoMision {
    @Column(name = "donaciones_recibidas")
    private Double donacionesRecibidas;

    public MisionDonacionesExitosas( Double donacionesRecibidas) {
        this.donacionesRecibidas = donacionesRecibidas;
    }

    @Override
    public ProgresoMision calcularProgreso(Donante donante) {
        LocalDate fechaInicio = donante.getFechaDelInicioMisionActual();
        List<Donacion> donacionesFiltradas = donante.getDonaciones().stream()
                .filter(d -> !d.getFechaDeCreacion().isBefore(fechaInicio))
                .toList();

        double recibidas = donacionesFiltradas.stream()
                .filter(d -> Boolean.TRUE.equals(d.getFueRecibida()))
                .count();

        double progreso = recibidas * 100.0 / donacionesRecibidas;
        return new ProgresoMision(progreso, recibidas, donacionesRecibidas);
    }
    @Override
    public Boolean esDeRacha(){
        return false;
    }
}
