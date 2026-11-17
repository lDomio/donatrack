package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medio_de_contacto")
public class MedioDeContacto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_contacto")
    private TipoDeContacto tipo;

    @Column(name = "valor")
    private String valor;

    public MedioDeContacto(TipoDeContacto tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
}
