package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MatcheoSugerido")
public class MatcheoSugerido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donacion_id")
    private Donacion donacion;

    @ManyToMany
    @JoinTable(
        name="NecesidadEnMatcheo",
        joinColumns = @JoinColumn(name = "matcheo_sugerido_id"),
        inverseJoinColumns = @JoinColumn(name="necesidad_id")
    )
    private List<Necesidad> necesidadesSugeridas = new ArrayList<>();

    @Column(name = "ya_fue_seleccionado_por_el_administrador")
    private Boolean yaFueSeleccionadoPorElAdministrador = false;

    public MatcheoSugerido(Donacion idDonacion, List<Necesidad> necesidadesSugeridas) {
        this.donacion = idDonacion;
        this.necesidadesSugeridas = necesidadesSugeridas;
        this.yaFueSeleccionadoPorElAdministrador = false;
    }
}
