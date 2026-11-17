package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.MedioDeContacto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "destinatario")
public class Destinatario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_destinatario")
  private Long id;

  @Column(name = "id_destinatario_origen")
  private Long idDestinatarioOrigen;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_destinatario")
  private TipoDestinatario tipoDestinatario;

  @Column(name = "nombreCompleto")
  private String nombreCompleto;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "destinatario_id", referencedColumnName = "id_destinatario")
  private List<MedioDeContacto> mediosDeContactos = new ArrayList<>();

  public Destinatario(String nombreCompleto, List<MedioDeContacto> mediosDeContactos) {
    this.nombreCompleto = nombreCompleto;
    this.mediosDeContactos = mediosDeContactos != null ? mediosDeContactos : new ArrayList<>();
  }

  public Destinatario(Long idDestinatarioOrigen, TipoDestinatario tipoDestinatario, String nombreCompleto, List<MedioDeContacto> mediosDeContactos) {
    this.idDestinatarioOrigen = idDestinatarioOrigen;
    this.tipoDestinatario = tipoDestinatario;
    this.nombreCompleto = nombreCompleto;
    this.mediosDeContactos = mediosDeContactos != null ? mediosDeContactos : new ArrayList<>();
  }

  public List<MedioDeContacto> obtenerContactosPreferidos(){
    if (this.mediosDeContactos == null) return new ArrayList<>();
    return this.mediosDeContactos.stream().filter(MedioDeContacto::esPreferido).collect(Collectors.toList());
  }
}
