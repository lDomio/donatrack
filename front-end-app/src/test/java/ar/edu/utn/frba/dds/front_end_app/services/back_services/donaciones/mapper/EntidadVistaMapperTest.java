package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.DireccionDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntidadVistaMapperTest {

  private static NecesidadResponse necesidad(String subcategoria, Double requerida, Double donada, String tipo) {
    return necesidad(subcategoria, requerida, donada, tipo, null);
  }

  private static NecesidadResponse necesidad(String subcategoria, Double requerida, Double donada, String tipo,
      String periodo) {
    return new NecesidadResponse(1L, new SubcategoriaDto(7L, subcategoria, "unid"), tipo, requerida, donada, periodo);
  }

  @Test
  void porcentajeCeroEsCriticoPorBajaCobertura() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad("Arroz", 200.0, 0.0, "RECURRENTE", "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(0, vista.metricas().get(0).porcentaje());
    assertTrue(vista.metricas().get(0).critica());
    assertEquals("alerta", vista.claseColorProgreso());
  }

  @Test
  void porcentajeCienSinCritica() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Escuela", "EDUCACION", List.of(necesidad("Cuadernos", 400.0, 400.0, "RECURRENTE", "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(100, vista.metricas().get(0).porcentaje());
    assertFalse(vista.metricas().get(0).critica());
    assertEquals(100, vista.progresoPorcentaje());
  }

  @Test
  void criticaPorBajaCobertura() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad("Aceite", 60.0, 12.0, "RECURRENTE", "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(20, vista.metricas().get(0).porcentaje());
    assertTrue(vista.metricas().get(0).critica());
    assertEquals("alerta", vista.claseColorProgreso());
    assertEquals("alerta", vista.necesidades().get(0).claseColorProgreso());
  }

  @Test
  void criticaPorExtraordinariaAunqueEsteCompleta() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Refugio", "REFUGIO", List.of(necesidad("Sillas", 30.0, 30.0, "EXTRAORDINARIA")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertTrue(vista.metricas().get(0).critica());
    assertEquals("alerta", vista.claseColorProgreso());
  }

  @Test
  void cantidadCeroNoRompeElCalculo() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad("Arroz", 0.0, 0.0, "RECURRENTE", "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(0, vista.metricas().get(0).porcentaje());
    assertFalse(vista.metricas().get(0).critica());
    assertEquals(0, vista.progresoPorcentaje());
  }

  @Test
  void cantidadesNulasMapeanACero() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad("Arroz", null, null, null)));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(0, vista.metricas().get(0).cantidadRequerida());
    assertEquals(0, vista.metricas().get(0).cantidadDonada());
    assertEquals(0, vista.metricas().get(0).porcentaje());
  }

  @Test
  void sinNecesidadesProgresoCeroYResumenVacio() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Fundacion", "ONG", List.of());

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals(0, vista.progresoPorcentaje());
    assertEquals("", vista.claseColorProgreso());
    assertTrue(vista.metricas().isEmpty());
    assertTrue(vista.necesidades().isEmpty());
  }

  @Test
  void criticasVanPrimeroEnElResumen() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(
            necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M"),
            necesidad("Aceite", 60.0, 12.0, "RECURRENTE", "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals("Aceite", vista.necesidades().get(0).nombre());
    assertEquals("Arroz", vista.necesidades().get(1).nombre());
  }

  @Test
  void fallbacksSinDireccion() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of());

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertNull(vista.ubicacion());
    assertEquals("Descripcion pendiente", vista.descripcion());
    assertNull(vista.imagenUrl());
    assertEquals(0, vista.impacto());
  }

  @Test
  void ubicacionConCalleYAltura() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(),
        new DireccionDto("Av. Siempreviva", 742, null, null, null));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals("Av. Siempreviva 742", vista.ubicacion());
  }

  @Test
  void ubicacionConCiudad() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(),
        new DireccionDto("Calle Falsa", 123, "CABA", null, null));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals("Calle Falsa 123, CABA", vista.ubicacion());
  }

  @Test
  void ubicacionSoloCalle() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(),
        new DireccionDto("Calle sin numero", null, null, null, null));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertEquals("Calle sin numero", vista.ubicacion());
  }

  @Test
  void extraordinariaInferidaPorPeriodoNuloAunqueEsteCompleta() {
    // El back serializa tipoNecesidad como Clase@hash; la inferencia usa el periodo (ver plan.md V1).
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Refugio", "REFUGIO", List.of(necesidad(
            "Sillas", 30.0, 30.0,
            "ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadExtraordinaria@4f3b56af",
            null)));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertTrue(vista.metricas().get(0).critica());
    assertEquals("alerta", vista.claseColorProgreso());
  }

  @Test
  void recurrenteCompletaConPeriodoNoEsCritica() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad(
            "Arroz", 200.0, 200.0,
            "ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadRecurrente@1f94888d",
            "P1M")));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertFalse(vista.metricas().get(0).critica());
    assertEquals("", vista.claseColorProgreso());
  }

  @Test
  void direccionVaciaUbicacionNula() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(),
        new DireccionDto(null, null, null, null, null));

    EntidadVistaDto vista = EntidadVistaMapper.aVista(response);

    assertNull(vista.ubicacion());
  }

  @ParameterizedTest
  @CsvSource({
      "COMEDOR,fa-users",
      "EDUCACION,fa-graduation-cap",
      "SALUD,fa-heart-pulse",
      "ONG,fa-hand-holding-heart",
      "REFUGIO,fa-bed",
      "DESCONOCIDO,fa-hand-holding-heart"
  })
  void iconoSegunTipo(String tipo, String iconoEsperado) {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Entidad", tipo, List.of());

    assertEquals(iconoEsperado, EntidadVistaMapper.aVista(response).iconoImpacto());
  }
}
