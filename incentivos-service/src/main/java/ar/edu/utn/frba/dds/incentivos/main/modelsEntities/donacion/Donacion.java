package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion;


import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Persistable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "donacion")
public class Donacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donacion")
    private Long id;

    @Column(name = "id_donacion_donaciones_service")
    private Long idDonacionDonacionesService;

    @Column(name = "fecha_de_creacion")
    private LocalDate fechaDeCreacion;

    @Column(name = "cantidad_de_bien")
    private Double cantidadDeBien;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "fecha_de_recepcion")
    private LocalDate fechaDeRecepcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", referencedColumnName = "id_donante")
    private Donante donante;


    @Column(name = "fue_recibida")
    private Boolean fueRecibida;

    @Column(name = "entidad_beneficiaria_id")
    private String entidadBeneficiariaId;


    public Donacion(LocalDate fechaDeCreacion, Double cantidadDeBien, String categoria, Long idDonacionDonacionesService, Donante donante, String entidadBeneficiariaId) {
        this.fechaDeCreacion = fechaDeCreacion;
        this.cantidadDeBien = cantidadDeBien;
        this.categoria = categoria;
        this.fechaDeRecepcion = null;
        this.idDonacionDonacionesService = idDonacionDonacionesService;
        this.donante = donante;
        this.fueRecibida = false;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
    }

    public void marcarComoRecibida(String entidadQUeLaRecibioId){
        this.fechaDeRecepcion = LocalDate.now();
        this.fueRecibida = true;
        this.entidadBeneficiariaId = entidadQUeLaRecibioId;
    }
}
