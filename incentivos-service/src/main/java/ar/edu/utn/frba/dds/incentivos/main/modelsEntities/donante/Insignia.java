package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "insignia")
public class Insignia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion")
    String descripcion;

    public Insignia(String descripcion) {
        this.descripcion = descripcion;
    }
}
