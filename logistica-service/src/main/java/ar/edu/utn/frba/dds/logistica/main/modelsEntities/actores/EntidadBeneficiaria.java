package ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores;

import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Direccion;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity("entidadesBeneficiarias")
public class EntidadBeneficiaria {
    @Id
    private Long id;
    private Long idEntidadDonacionesService;
    private String razonSocial;
    private Direccion direccion;

    @Setter(AccessLevel.NONE)
    private List<MedioDeContacto> contactos = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @Reference(lazy = true, ignoreMissing = true)
    private List<Donacion> donacionesAsignadas = new ArrayList<>();

    public List<MedioDeContacto> getContactos() {
        if (this.contactos == null) {
            this.contactos = new ArrayList<>();
        }
        return this.contactos;
    }

    public List<Donacion> getDonacionesAsignadas() {
        if (this.donacionesAsignadas == null) {
            this.donacionesAsignadas = new ArrayList<>();
        }
        return this.donacionesAsignadas;
    }

    public void agregarContacto(MedioDeContacto contacto) {
        this.getContactos().add(contacto);
    }

    public void agregarDonacionAsignada(Donacion donacion) {
        this.getDonacionesAsignadas().add(donacion);
    }

    public void removerDonacionAsignada(Long idDonacion) {
        if (this.donacionesAsignadas != null) {
            this.donacionesAsignadas.removeIf(d -> d.getId().equals(idDonacion));
        }
    }

}
