package ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion;


import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PlantillaCanal {

  @Column(name = "asunto")
  private String asunto;

  @Column(name = "cuerpo")
  private String cuerpo;

  @Column(name = "remitente")
  private String remitente;

  @Column(name = "imagenUrl")
  private String imagenUrl;

  @Column(name = "videoUrl")
  private String videoUrl;

  @Column(name = "audioUrl")
  private String audioUrl;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "notificacion_id", referencedColumnName = "id")
  private List<ArchivoAdjunto> archivosAdjuntos;

  @Column(name = "fechaDeEnvio")
  private LocalDateTime fechaEnvio;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "canal")
  private TipoDeContacto canal;

  @Column(name = "nombrePlantilla")
  private String nombrePlantilla;

  @ElementCollection
  @CollectionTable(
      name = "plantilla_variables",
      joinColumns = @JoinColumn(name = "notificacion_id")
  )
  @MapKeyColumn(name = "clave")
  @Column(name = "valor")
  private Map<String, String> variables = new HashMap<>();

  public Map<String, String> getVariables() {
    if (this.variables == null) {
      this.variables = new HashMap<>();
    }
    return this.variables;
  }

  public String getVariablesAsJson() {
    try {
      return new ObjectMapper().writeValueAsString(this.getVariables());
    } catch (Exception e) {
      return "{}";
    }
  }
}
