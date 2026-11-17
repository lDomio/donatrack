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
@DiscriminatorValue("habilDonador")
public class MisionHabilDonador extends EstadoMision {
    @Column(name = "cantidad_de_bienes_en_donacion")
    private Double cantidadDeBienesEnDonacion;

    public MisionHabilDonador( Double cantidadDeBienesEnDonacion) {
        this.cantidadDeBienesEnDonacion = cantidadDeBienesEnDonacion;
    }

    @Override
    public ProgresoMision calcularProgreso(Donante donante) {
        LocalDate fechaInicio = donante.getFechaDelInicioMisionActual();
        List<Donacion> donacionesFiltradas = donante.getDonaciones().stream()
                .filter(d -> !d.getFechaDeCreacion().isBefore(fechaInicio))
                .toList();

        double maxCantidad = donacionesFiltradas.stream()
                .mapToDouble(Donacion::getCantidadDeBien)
                .max()
                .orElse(0.0);

        double progreso = maxCantidad * 100.0 / cantidadDeBienesEnDonacion;
        return new ProgresoMision(progreso, maxCantidad, cantidadDeBienesEnDonacion);
    }
    @Override
    public Boolean esDeRacha(){
        return false;
    }
}
