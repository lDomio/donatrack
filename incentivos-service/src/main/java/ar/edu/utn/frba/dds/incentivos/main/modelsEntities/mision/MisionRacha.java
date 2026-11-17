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
import java.time.YearMonth;
import java.util.List;
import java.util.Comparator;

import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("racha")
public class MisionRacha extends EstadoMision {
    @Column(name = "meses_consecutivos")
    private Double mesesConsecutivos;

    public MisionRacha(Double mesesConsecutivos) {
        this.mesesConsecutivos = mesesConsecutivos;
    }

    @Override
    public ProgresoMision calcularProgreso(Donante donante) {
        LocalDate fechaInicio = donante.getFechaDelInicioMisionActual();
        //Filtro las donaciones por fecha y las ordeno de la mas vieja a las mas nueva
        List<Donacion> donacionesFiltradas = donante.getDonaciones().stream()
                .filter(d -> !d.getFechaDeCreacion().isBefore(fechaInicio))
                .sorted(Comparator.comparing(Donacion::getFechaDeCreacion))
                .toList();

        if (donacionesFiltradas.isEmpty()) {
            return new ProgresoMision(0.0, 0.0, mesesConsecutivos);
        }

        int maxRacha = 1;
        int rachaActual = 1;

        YearMonth ultimoMes = YearMonth.from(donacionesFiltradas.getFirst().getFechaDeCreacion());

        for (int i = 1; i < donacionesFiltradas.size(); i++) {
            YearMonth mesActual = YearMonth.from(donacionesFiltradas.get(i).getFechaDeCreacion());

            if (ultimoMes.plusMonths(1).equals(mesActual)) {
                rachaActual++;
                ultimoMes = mesActual;
            } else if (mesActual.isAfter(ultimoMes.plusMonths(1))) {
                rachaActual = 1;
                ultimoMes = mesActual;
            }

            if (rachaActual > maxRacha) {
                maxRacha = rachaActual;
            }
        }

        double progreso = (double) maxRacha * 100.0 / mesesConsecutivos;
        return new ProgresoMision(progreso, (double) maxRacha, mesesConsecutivos);
    }
    @Override
    public Boolean esDeRacha(){
        return true;
    }
}
