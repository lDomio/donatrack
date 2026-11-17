package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante;

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
@Entity @Table(name = "categoria_completada")
public class CategoriaCompletada {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_donante_id", referencedColumnName = "id")
    private CategoriaDonante categoria;

    @Column(name = "fecha_de_complecion")
    private LocalDate fechaDeComplecion;

    public CategoriaCompletada(CategoriaDonante categoria) {
        this.categoria = categoria;
        this.fechaDeComplecion = LocalDate.now();
    }
}
