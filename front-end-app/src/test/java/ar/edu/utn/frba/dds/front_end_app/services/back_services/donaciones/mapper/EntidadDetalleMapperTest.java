package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaNecesidadDto;
import ar.edu.utn.frba.dds.front_end_app.dto.DireccionDto;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadDetalleVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.InsumoNecesidadDto;
import ar.edu.utn.frba.dds.front_end_app.dto.NecesidadResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntidadDetalleMapperTest {

  private static NecesidadResponse necesidad(String subcategoria, Double requerida, Double donada,
      String tipo, String periodo) {
    return new NecesidadResponse(1L, new SubcategoriaDto(7L, subcategoria, "unid"), tipo, requerida, donada, periodo);
  }

  @Test
  void agrupaExtraordinariasPrimeroYRecurrentesDespues() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(
            necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M"),
            necesidad("Sillas", 30.0, 0.0, null, null)));

    List<CategoriaNecesidadDto> categorias =
        EntidadDetalleMapper.aCategorias(response.necesidades());

    assertEquals(2, categorias.size());
    assertEquals("Necesidades extraordinarias", categorias.get(0).titulo());
    assertEquals("Necesidades recurrentes", categorias.get(1).titulo());
    assertEquals("Sillas", categorias.get(0).insumos().get(0).nombre());
    assertEquals("Arroz", categorias.get(1).insumos().get(0).nombre());
  }

  @Test
  void grupoVacioSeOmite() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M")));

    List<CategoriaNecesidadDto> categorias =
        EntidadDetalleMapper.aCategorias(response.necesidades());

    assertEquals(1, categorias.size());
    assertEquals("Necesidades recurrentes", categorias.get(0).titulo());
  }

  @Test
  void sinNecesidadesDetalleVacio() {
    EntidadBeneficiariaResponse response =
        new EntidadBeneficiariaResponse(1L, "Fundacion", "ONG", List.of());

    EntidadDetalleVistaDto vista = EntidadDetalleMapper.aVista(response);

    assertTrue(vista.categorias().isEmpty());
    assertTrue(vista.entidad().impacto().isEmpty());
    assertFalse(vista.entidad().verificada());
    assertNull(vista.entidad().ubicacion());
    assertEquals("Descripcion pendiente", vista.entidad().descripcion());
  }

  @Test
  void ubicacionCompuestaDesdeDireccion() {
    EntidadBeneficiariaResponse response = new EntidadBeneficiariaResponse(
        1L, "Comedor", "COMEDOR", List.of(),
        new DireccionDto("Av. Siempreviva", 742, null, null, null));

    assertEquals("Av. Siempreviva 742", EntidadDetalleMapper.aVista(response).entidad().ubicacion());
  }

  @Test
  void frecuenciaMensualConP1M() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M")));

    assertEquals("Mensual", categorias.get(0).frecuencia());
  }

  @Test
  void frecuenciaAnualConP1Y() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Fiesta", 40.0, 4.0, "RECURRENTE", "P1Y")));

    assertEquals("Anual", categorias.get(0).frecuencia());
  }

  @Test
  void frecuenciaNulaConMezclaDePeriodos() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M"),
        necesidad("Fiesta", 40.0, 4.0, "RECURRENTE", "P1Y")));

    assertNull(categorias.get(0).frecuencia());
  }

  @Test
  void frecuenciaNulaEnExtraordinarias() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Sillas", 30.0, 0.0, null, null)));

    assertNull(categorias.get(0).frecuencia());
  }

  @Test
  void insumoCompletadoEsVerde() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Fideos", 150.0, 150.0, "RECURRENTE", "P1M")));

    InsumoNecesidadDto insumo = categorias.get(0).insumos().get(0);

    assertTrue(insumo.completado());
    assertEquals("green", insumo.claseColorBarra());
    assertEquals(100, insumo.porcentaje());
  }

  @Test
  void insumoUrgenteEsRojo() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Aceite", 60.0, 12.0, "RECURRENTE", "P1M")));

    InsumoNecesidadDto insumo = categorias.get(0).insumos().get(0);

    assertTrue(insumo.urgente());
    assertFalse(insumo.completado());
    assertEquals("red", insumo.claseColorBarra());
    assertEquals(20, insumo.porcentaje());
  }

  @Test
  void insumoNormalEsTeal() {
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Arroz", 200.0, 150.0, "RECURRENTE", "P1M")));

    InsumoNecesidadDto insumo = categorias.get(0).insumos().get(0);

    assertFalse(insumo.urgente());
    assertFalse(insumo.completado());
    assertEquals("teal", insumo.claseColorBarra());
  }

  @Test
  void insumoConFormaRealDelBack() {
    // Shape verificado contra GET /entidades-beneficiarias (tipoNecesidad como Clase@hash).
    List<CategoriaNecesidadDto> categorias = EntidadDetalleMapper.aCategorias(List.of(
        necesidad("Leche Larga Vida", 20.0, 0.0,
            "ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadRecurrente@46cba520",
            "P1M")));

    assertEquals("Necesidades recurrentes", categorias.get(0).titulo());
    assertEquals("Mensual", categorias.get(0).frecuencia());
  }
}
