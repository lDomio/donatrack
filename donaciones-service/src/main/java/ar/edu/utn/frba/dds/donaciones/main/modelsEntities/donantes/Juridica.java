package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes;

import jakarta.persistence.*;
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
@DiscriminatorValue("JURIDICA")
public class Juridica extends Donante {

    @Column(name = "razonSocial")
    private String razonSocial;

    @Embedded
    private TipoPersonaJuridica tipoJuridica;

    @Column(name = "rubro")
    private String rubro;

    @Column(name = "CUIT")
    private String nroCuit;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_donantePersonaJuridicaRepresentado", referencedColumnName = "id_donante")
    private List<Representante> representantes = new ArrayList<>();

    public void agregarRepresentante(Representante representante) {
        this.representantes.add(representante);
    }

    public void removerRepresentante(Long idRepresentante) {
        this.representantes.removeIf(r -> r.getId_representante() != null && r.getId_representante().equals(idRepresentante));
    }
}
