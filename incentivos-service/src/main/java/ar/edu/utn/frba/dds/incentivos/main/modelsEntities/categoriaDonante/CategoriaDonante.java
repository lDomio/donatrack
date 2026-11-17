package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categoria_donante")
public class CategoriaDonante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "primera_mision_id", referencedColumnName = "id")
    private Mision primeraMision;

    @Column(name = "cantidad_de_misiones_a_completar")
    private Integer cantidadDeMisionesACompletar;

    public CategoriaDonante(String nombre, Mision primeraMision, Integer cantidadDeMisionesACompletar) {
        this.nombre = nombre;
        this.primeraMision = primeraMision;
        this.cantidadDeMisionesACompletar = cantidadDeMisionesACompletar;
    }
}
