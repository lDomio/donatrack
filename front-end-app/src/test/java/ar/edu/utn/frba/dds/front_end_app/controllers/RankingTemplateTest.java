package ar.edu.utn.frba.dds.front_end_app.controllers;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el contrato de la plantilla de ranking sin levantar el contexto web.
 * La renderización final se cubre en la verificación E2E con el navegador.
 */
class RankingTemplateTest {

  @Test
  @DisplayName("La plantilla muestra misiones, el botón de generación y las alertas")
  void plantillaTieneMisionesBotonYAlertas() throws Exception {
    String template = new ClassPathResource("templates/personaAdministradora/ranking.html")
        .getContentAsString(StandardCharsets.UTF_8);

    assertTrue(template.contains("Misiones"));
    assertTrue(template.contains("misionesCompletadas"));
    assertTrue(template.contains("Generar ranking"));
    assertTrue(template.contains("/personaAdministradora/ranking/generar"));
    assertTrue(template.contains("formularioGenerarRanking"));
    assertTrue(template.contains("alertaFormulario"));
    assertTrue(template.contains("periodoGenerar"));
  }
}
