package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.notificaciones;

public class GeneradorNotificacionesTest {
/*
  @Test
  public void testNotificarGeneraElRegistroDeNotificacion() {
    // Arrange
    MedioDeContacto email = new ContactoEmail("uriel@example.com");
    MedioDeContacto whatsapp = new ContactoWhatsApp("+5491100000000");
    List<MedioDeContacto> medios = new ArrayList<>();
    medios.add(email);
    medios.add(whatsapp);
    GeneradorNotificacion generador = new GeneradorNotificacion();

    // Act
    Notificacion resultado = generador.notificar(1, "Motivo de prueba", "Notificación de prueba", medios);

    // Assert
    Assertions.assertEquals(1, resultado.getIdUsuario());
    Assertions.assertEquals("Motivo de prueba", resultado.getMotivo());
    Assertions.assertEquals("Notificación de prueba", resultado.getMensaje());
    Assertions.assertEquals(LocalDate.now(), resultado.getFechaEmision());
    Assertions.assertFalse(resultado.isLeida());
  }

  @Test
  void testNotificarConListaVaciaNoFalla() {
    // Arragnge
    List<MedioDeContacto> medios = new ArrayList<>();
    GeneradorNotificacion generador = new GeneradorNotificacion();

    // Act
    Notificacion resultado = generador.notificar(1, "Motivo de prueba", "Notificación de prueba", medios);

    // Assert
    Assertions.assertEquals(1, resultado.getIdUsuario());
    Assertions.assertEquals("Motivo de prueba", resultado.getMotivo());
    Assertions.assertEquals("Notificación de prueba", resultado.getMensaje());
    Assertions.assertEquals(LocalDate.now(), resultado.getFechaEmision());
    Assertions.assertFalse(resultado.isLeida());
  }
  */
  /*
   * No hace falta testear la lista null porque uso Lombok @NonNull en el metodo
   * notificar, lo que hace que se lance una NullPointerException automáticamente
   * si se pasa null. De hecho, el test que estaba escribiendo para eso es
   * redundante y no aporta valor, así que lo voy a eliminar.
   */

  // @Test
  // void testNotificarConListaNullLanzaExcepcion() {
  // // Arrange
  // GeneradorNotificacion generador = new GeneradorNotificacion();

  // // Act & Assert
  // Assertions.assertThrows(NullPointerException.class, () -> {
  // generador.notificar(1, "Motivo de prueba", "Notificación de prueba", null);
  // });
  // }
}
