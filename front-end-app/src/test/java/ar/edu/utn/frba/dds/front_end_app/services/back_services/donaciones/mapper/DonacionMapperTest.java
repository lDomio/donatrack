package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.BienResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionResponseDonante;
import ar.edu.utn.frba.dds.front_end_app.dto.DonacionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.SubcategoriaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.TipoDeEstado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonacionMapperTest {

  private static final SubcategoriaResponse SUBCATEGORIA =
      new SubcategoriaResponse(1L, "Abrigos", 2L, "Ropa", "unidad");

  @ParameterizedTest
  @CsvSource({
      "DEPOSITO,En deposito,,false",
      "ASIGNACION_REALIZADA,Asignada,activo,false",
      "LISTA_PARA_ENTREGAR,Lista para entregar,activo,false",
      "EN_TRASLADO,En transporte,activo,true",
      "ENTREGADA,Entregada,exito,false",
      "ENTREGA_FALLIDA,Entrega fallida,,false",
      "VENCIDO,Vencida,,false"
  })
  void mapeaLosSieteEstados(String estadoRaw, String labelEsperado, String claseEsperada, boolean destacadoEsperado) {
    TipoDeEstado estado = TipoDeEstado.valueOf(estadoRaw);
    DonacionResponseDonante response = new DonacionResponseDonante(
        1L, List.of(), "Una donacion", estado, SUBCATEGORIA);

    DonacionVistaDto vista = DonacionMapper.aVista(response);

    assertEquals(labelEsperado, vista.estado());
    assertEquals(claseEsperada == null ? "" : claseEsperada, vista.claseEstado());
    assertEquals(destacadoEsperado, vista.destacado());
  }

  @Test
  void tituloConBienesUsaCantidadYNombre() {
    DonacionResponseDonante response = new DonacionResponseDonante(
        1L,
        List.of(new BienResponse("camperas de abrigo", 3.0, null)),
        "descripcion general",
        TipoDeEstado.DEPOSITO,
        SUBCATEGORIA);

    assertEquals("3 camperas de abrigo", DonacionMapper.aVista(response).titulo());
  }

  @Test
  void tituloSinBienesUsaDescripcionGeneral() {
    DonacionResponseDonante response = new DonacionResponseDonante(
        1L, List.of(), "Donacion de ropa de invierno", TipoDeEstado.DEPOSITO, SUBCATEGORIA);

    assertEquals("Donacion de ropa de invierno", DonacionMapper.aVista(response).titulo());
  }

  @Test
  void tituloSinBienesNiDescripcionUsaIdComoFallback() {
    DonacionResponseDonante response = new DonacionResponseDonante(
        7L, List.of(), null, TipoDeEstado.DEPOSITO, SUBCATEGORIA);

    assertEquals("Donacion #7", DonacionMapper.aVista(response).titulo());
  }

  @Test
  void subcategoriaNulaCaeAFallback() {
    DonacionResponseDonante response = new DonacionResponseDonante(
        1L, List.of(), "descripcion", TipoDeEstado.DEPOSITO, null);

    DonacionVistaDto vista = DonacionMapper.aVista(response);

    assertEquals("Sin categoria", vista.categoria());
    assertEquals("Sin categoria", vista.subcategoria());
  }

  @Test
  void imagenUrlSiempreNulaPorqueElBackendNoLaProvee() {
    DonacionResponseDonante response = new DonacionResponseDonante(
        1L, List.of(), "descripcion", TipoDeEstado.DEPOSITO, SUBCATEGORIA);

    assertNull(DonacionMapper.aVista(response).imagenUrl());
  }

  @Test
  void estadosDisponiblesTieneLosSietesLabelsEnOrden() {
    assertEquals(7, DonacionMapper.ESTADOS_DISPONIBLES.size());
    assertTrue(DonacionMapper.ESTADOS_DISPONIBLES.contains("En transporte"));
    assertFalse(DonacionMapper.ESTADOS_DISPONIBLES.contains("Esperando asignacion"));
  }
}
