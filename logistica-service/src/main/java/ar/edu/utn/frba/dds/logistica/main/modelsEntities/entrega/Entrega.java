package ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.DonacionEnEntrega;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity("entregas")
public class Entrega {
    @Id
    private Long id;
    private String direccionEntidad;
    private String razonSocialEntidadBeneficiaria;
    private Double latitud;
    private Double longitud;
    private Boolean fueRecibidoPorLaEntidad;

    @Setter(AccessLevel.NONE)
    private List<DonacionEnEntrega> donacionesAEntregar = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private List<EstadoEntrega> estadosEntrega = new ArrayList<>();

    public Entrega(Long id, String direccionEntidad, String razonSocialEntidadBeneficiaria, Double latitud,
            Double longitud, Boolean fueRecibidoPorLaEntidad, List<DonacionEnEntrega> donacionesAEntregar,
            List<EstadoEntrega> estadosEntrega) {
        this.id = id;
        this.direccionEntidad = direccionEntidad;
        this.razonSocialEntidadBeneficiaria = razonSocialEntidadBeneficiaria;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fueRecibidoPorLaEntidad = fueRecibidoPorLaEntidad;
        this.donacionesAEntregar = donacionesAEntregar != null ? new ArrayList<>(donacionesAEntregar)
                : new ArrayList<>();
        this.estadosEntrega = estadosEntrega != null ? new ArrayList<>(estadosEntrega) : new ArrayList<>();
    }

    public void agregarDonacion(DonacionEnEntrega donacion) {
        this.donacionesAEntregar.add(donacion);
    }

    public void agregarEstado(EstadoEntrega estado) {
        this.estadosEntrega.add(estado);
    }
}
