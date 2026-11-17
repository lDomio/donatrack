package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "archivoAdjunto")
public class ArchivoAdjunto {
  public ArchivoAdjunto(String nombreArchivo, String tipoArchivo, Double tamanioArchivo) {
    this.nombreArchivo = nombreArchivo;
    this.tipoArchivo = tipoArchivo;
    this.tamanioArchivo = tamanioArchivo;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombreArchivo")
  private String nombreArchivo;

  @Column(name = "tipoArchivo")
  private String tipoArchivo;

  @Column(name = "tamanioArchivo")
  private Double tamanioArchivo;
}
