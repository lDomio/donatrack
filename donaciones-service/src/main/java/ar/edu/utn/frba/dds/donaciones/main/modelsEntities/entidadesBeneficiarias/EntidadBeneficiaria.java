package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Representante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EntidadesBeneficiarias")
public class EntidadBeneficiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidadBeneficiaria")
    private Long id;
    @Column(name = "razonSocial", nullable = false)
    private String razonSocial;

    @Column(name = "id_usuario_auth")
    private Long idUsuarioAuth;

    @Embedded
    private TipoEntidad tipo;

    @Embedded
    private Direccion direccion;

    @Getter(lombok.AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_titularEntidadBeneficiaria", referencedColumnName = "id_entidadBeneficiaria")
    private List<MedioDeContacto> contactos = new ArrayList<>();

    /**
     * Lado inverso: la FK vive en necesidades.entidad_beneficiaria_id, mapeada por
     * Necesidad.entidadBeneficiaria. Usar mappedBy (y no un @JoinColumn propio) evita
     * que la relacion quede duplicada en dos columnas distintas de la misma tabla.
     * Para dar de alta una necesidad usar siempre agregarNecesidad, que sincroniza
     * las dos puntas.
     */
    @Getter(lombok.AccessLevel.NONE)
    @OneToMany(mappedBy = "entidadBeneficiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Necesidad> necesidades = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_entidadBeneficiariaRepresentada", referencedColumnName = "id_entidadBeneficiaria")
    private List<Representante> representantes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_entidadBeneficiaAsignada", referencedColumnName = "id_entidadBeneficiaria")
    private List<Donacion> donacionesAsignadas = new ArrayList<>();

    public EntidadBeneficiaria(String razonSocial, TipoEntidad tipo, Direccion direccion, List<MedioDeContacto> contactos, List<Necesidad> necesidades, List<Representante> representantes, List<Donacion> donacionesAsignadas) {
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.direccion = direccion;
        this.contactos = contactos;
        this.necesidades = necesidades;
        this.representantes = representantes;
        this.donacionesAsignadas = donacionesAsignadas;
    }

    public EntidadBeneficiaria(String razonSocial, TipoEntidad tipo) {
        this.razonSocial = razonSocial;
        this.tipo = tipo;
    }

    public List<Representante> getRepresentantes() {
        return representantes;
    }

    public void agregarRepresentante(Representante representante) {
        this.getRepresentantes().add(representante);
    }

    public void agregarDonacion(Donacion donacion) {
        this.getDonacionesAsignadas().add(donacion);
    }

    public void agregarContacto(MedioDeContacto contacto) {
        this.contactos.add(contacto);
    }

    public List<MedioDeContacto> getContactos() {
        return this.contactos;
    }

    public void agregarNecesidad(Necesidad necesidad) {
        necesidad.setEntidadBeneficiaria(this);
        this.getNecesidades().add(necesidad);
    }

    public List<Necesidad> getNecesidades() {
        return this.necesidades;
    }

    public List<Donacion> ultimasDonacionesDelTrimestre() {
        if (this.getDonacionesAsignadas() == null) return Collections.emptyList();

        LocalDate fechaLimite = LocalDate.now().minusMonths(3);
        return getDonacionesAsignadas().stream().filter(donacion -> donacion.fechaUltimoEstado().isAfter(fechaLimite)).toList();

    }

    public void removerDonacion(Donacion donacion) {
        this.getDonacionesAsignadas().removeIf(d -> d.getId().equals(donacion.getId()));
    }

}
