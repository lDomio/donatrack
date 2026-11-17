package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mision_completada")
public class MisionCompletada {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insignia_otorgada_id", referencedColumnName = "id")
    private Insignia insigniaOtorgada;

    @Column(name = "fecha_de_complecion")
    private LocalDate fechaDeComplecion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_donante_id", referencedColumnName = "id")
    private CategoriaDonante categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", referencedColumnName = "id")
    private Mision mision;

    public MisionCompletada(Insignia insigniaOtorgada, Mision mision) {
        this.insigniaOtorgada = insigniaOtorgada;
        this.mision = mision;
        this.categoria = mision.getCategoria();
        this.fechaDeComplecion = LocalDate.now();
    }
}
