package ar.edu.utn.frba.dds.notificacion;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.canales.WhatsappCanal;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.MedioDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.medioDeContacto.TipoDeContacto;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.PlantillaCanal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class WhatsappCanalTest {
/*
  private WhatsappCanal whatsappCanal;
  private Notificacion notificacion;

  @BeforeEach
  public void setUp() {
    // Usamos el constructor con credenciales nulas para probar el fallo por falta de configuración
    whatsappCanal = new WhatsappCanal(null, null, null);

    MedioDeContacto medio = new MedioDeContacto("+541122334455", TipoDeContacto.WHATSAPP, true);
    Destinatario destinatario = new Destinatario("Test User", Collections.singletonList(medio));

    PlantillaCanal plantilla = new PlantillaCanal(
        "Asunto Test",
        "Cuerpo Test",
        "DonaTrack",
        null, null, null, null,
        LocalDateTime.now(),
        TipoDeContacto.WHATSAPP
    );

    notificacion = new Notificacion(
        destinatario,
        "Mensaje de prueba",
        plantilla,
        new ArrayList<>()
    );
  }

  @Test
  public void testSoportaWhatsapp() {
    Assertions.assertTrue(whatsappCanal.soporta(TipoDeContacto.WHATSAPP));
    Assertions.assertFalse(whatsappCanal.soporta(TipoDeContacto.EMAIL));
  }

  @Test
  public void testEnviarFallaSinCredenciales() {
    boolean result = whatsappCanal.enviar(notificacion, "+541122334455");
    Assertions.assertFalse(result);
  }
  */

}
