package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

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
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donante_en_ranking")
public class DonanteEnRanking {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "donante_id", referencedColumnName = "id_donante")
  private Donante donante;

  @Column(name = "puesto")
  private Integer puesto;



  public DonanteEnRanking(Donante donante, Integer puesto) {
    this.donante = donante;
    this.puesto = puesto;
  }

}
