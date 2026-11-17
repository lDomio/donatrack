package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity @Table(name = "estado_mision")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class EstadoMision {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    abstract ProgresoMision calcularProgreso(Donante donante);
    abstract Boolean esDeRacha();
}
