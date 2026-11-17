package ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaDonanteResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.IncentivosVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.InsigniaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.InsigniaVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionEstadoResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.ProgresoMisionResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncentivosMapperTest {

  @Test
  void combinarSinDatosDelBackendDejaSoloElFallbackIlustrativo() {
    IncentivosVistaDto vista = IncentivosMapper.combinar(List.of(), List.of());

    assertEquals(0, vista.insigniasObtenidas());
    assertEquals(4, vista.insigniasTotales());
    assertEquals(4, vista.misiones().size());
    assertTrue(vista.misiones().stream().allMatch(m -> !"COMPLETADA".equals(m.estado())));
    assertEquals(4, vista.insignias().size());
    assertTrue(vista.insignias().stream().noneMatch(InsigniaVistaDto::obtenida));
  }

  @Test
  void combinarConMisionCompletadaRealLaAgregaAlPrincipioConEstadoCompletada() {
    MisionResponse mision = new MisionResponse(new InsigniaResponse("Donaste mucha ropa"), LocalDate.of(2026, 3, 1));

    IncentivosVistaDto vista = IncentivosMapper.combinar(List.of(mision), List.of());

    assertEquals(5, vista.misiones().size());
    MisionVistaDto real = vista.misiones().get(0);
    assertEquals("COMPLETADA", real.estado());
    assertEquals(100, real.porcentaje());
    assertEquals("Donaste mucha ropa", real.insigniaOtorgada());
    assertTrue(real.descripcion().contains("2026-03-01"));
  }

  @Test
  void combinarConInsigniaRealCuentaComoObtenidaYSumaAlTotal() {
    InsigniaResponse insignia = new InsigniaResponse("Donaste mucha ropa");

    IncentivosVistaDto vista = IncentivosMapper.combinar(List.of(), List.of(insignia));

    assertEquals(1, vista.insigniasObtenidas());
    assertEquals(5, vista.insigniasTotales());
    InsigniaVistaDto real = vista.insignias().get(0);
    assertTrue(real.obtenida());
    assertEquals("Donaste mucha ropa", real.nombre());
  }

  @Test
  void misionSinInsigniaAsociadaNoRompeElMapeo() {
    MisionResponse mision = new MisionResponse(null, null);

    IncentivosVistaDto vista = IncentivosMapper.combinar(List.of(mision), List.of());

    MisionVistaDto real = vista.misiones().get(0);
    assertEquals("COMPLETADA", real.estado());
    assertEquals(null, real.insigniaOtorgada());
  }

  @Test
  void soloFallbackReproduceElContenidoIlustrativoOriginal() {
    IncentivosVistaDto vista = IncentivosMapper.soloFallback();

    assertEquals(5, vista.misiones().size());
    assertEquals(5, vista.insignias().size());
    assertEquals(1, vista.insigniasObtenidas());
    assertEquals(5, vista.insigniasTotales());
    assertEquals("Colaborador", vista.categoriaActual());
  }

  @Test
  void combinarConEstadoGeneralUsaCategoriaProgresoYMisionActualReales() {
    EstadoGeneralResponse estado = new EstadoGeneralResponse(
        new CategoriaDonanteResponse("COLABORADOR_ACTIVO", 2),
        new CategoriaDonanteResponse("COLABORADOR_LEYENDA", 3),
        new MisionEstadoResponse("Mision 2", new InsigniaResponse("Insignia de prueba")),
        new ProgresoMisionResponse(50.0, 1.0, 2.0),
        1,
        2
    );

    IncentivosVistaDto vista = IncentivosMapper.combinar(List.of(), List.of(), estado);

    assertEquals("Sostenedor", vista.categoriaActual());
    assertEquals("Transformador", vista.categoriaSiguiente());
    assertEquals(1, vista.misionesFaltantesCategoria());
    assertEquals(3, vista.categorias().size());
    assertTrue(vista.categorias().stream().anyMatch(c -> c.nombre().equals("Sostenedor") && c.actual()));

    MisionVistaDto enProgreso = vista.misiones().stream()
        .filter(m -> "EN_PROGRESO".equals(m.estado()))
        .findFirst()
        .orElseThrow();
    assertEquals("Mision 2", enProgreso.titulo());
    assertEquals(50, enProgreso.porcentaje());
    assertEquals(1, enProgreso.cantidadActual());
    assertEquals(2, enProgreso.cantidadObjetivo());
    assertEquals("Insignia de prueba", enProgreso.insigniaOtorgada());
  }
}
