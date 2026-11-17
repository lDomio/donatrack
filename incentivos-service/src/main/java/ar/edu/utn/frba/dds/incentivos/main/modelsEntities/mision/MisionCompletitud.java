package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;

import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("completitud")
public class MisionCompletitud extends EstadoMision {
    @Column(name = "catidad_categorias_distintas")
    private Double cantidadCategoriasDistintas;

    public MisionCompletitud(Double cantidadCategoriasDistintas) {
        this.cantidadCategoriasDistintas = cantidadCategoriasDistintas;
    }

    @Override
    public ProgresoMision calcularProgreso(Donante donante) {
        //Primero lo que hago es filtrar las donaciones que sean para la mision, osea, aquellas donaciones que se hagan despues de la fecha en la cual se cambio la mision
        LocalDate fechaInicio = donante.getFechaDelInicioMisionActual();
        List<Donacion> donacionesFiltradas = donante.getDonaciones().stream()
                .filter(d -> !d.getFechaDeCreacion().isBefore(fechaInicio))
                .toList();

        //definno un set de categorias, para no tener repetidas y asi poder calcular el progreso
        double distinctCategories = new HashSet<>(donacionesFiltradas.stream()
                .map(Donacion::getCategoria)
                .toList()).size();

        double progreso = distinctCategories * 100.0 / cantidadCategoriasDistintas;
        return new ProgresoMision(progreso, distinctCategories, cantidadCategoriasDistintas);
    }
    @Override
    public Boolean esDeRacha(){
        return false;
    }
}
