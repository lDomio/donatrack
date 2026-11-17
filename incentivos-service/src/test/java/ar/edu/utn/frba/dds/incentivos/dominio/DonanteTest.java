package ar.edu.utn.frba.dds.incentivos.dominio;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteEventoObserver;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.CategoriaCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonanteTest {

  private Donante donante;

  @Mock
  private DonanteEventoObserver observerMock;

  @Mock
  private Mision misionActualMock;

  @Mock
  private CategoriaDonante categoriaMockA;

  @Mock
  private CategoriaDonante categoriaMockB;

  @BeforeEach
  void setUp() {
    donante = new Donante();
    donante.setObserver(observerMock);
    donante.setMisionActual(misionActualMock);
  }

  @Nested
  @DisplayName("Tests de Progreso y Completitud de Misiones (Casos Borde)")
  class CompletarMisionesTests {

    @Test
    @DisplayName("Borde Inferior: Progreso al 99.9% no debe completar la misión")
    void noCompletaMisionConProgresoAlCasi100() {
      // Arrange
      ProgresoMision progresoIncompleto = new ProgresoMision(99.9, 99.9, 100.0);
      when(misionActualMock.calcularProgreso(donante)).thenReturn(progresoIncompleto);

      // Act
      donante.completarMisionSiCompletadaYGenerarNotificaciones();

      // Assert
      verify(misionActualMock, never()).completar(any());
      assertEquals(99.9, donante.getProgresoMisionActual().getProgreso());
    }

    @Test
    @DisplayName("Borde Exacto: Progreso al 100.0% DEBE completar la misión")
    void completaMisionConProgresoExactamenteAl100() {
      // Arrange
      ProgresoMision progresoCompleto = new ProgresoMision(100.0, 100.0, 100.0);
      when(misionActualMock.calcularProgreso(donante)).thenReturn(progresoCompleto);

      // Act
      donante.completarMisionSiCompletadaYGenerarNotificaciones();

      // Assert
      verify(misionActualMock, times(1)).completar(donante);
    }

    @Test
    @DisplayName("Borde Superior: Progreso mayor al 100% (ej. 150%) DEBE completar la misión")
    void completaMisionConProgresoMayorAl100() {
      // Arrange
      ProgresoMision progresoExcedido = new ProgresoMision(150.0, 150.0, 100.0);
      when(misionActualMock.calcularProgreso(donante)).thenReturn(progresoExcedido);

      // Act
      donante.completarMisionSiCompletadaYGenerarNotificaciones();

      // Assert
      verify(misionActualMock, times(1)).completar(donante);
    }

    @Test
    @DisplayName("Null Safety: Si no tiene misión actual, no arroja excepción")
    void noFallaSiMisionActualEsNullAlVerificarProgreso() {
      // Arrange
      donante.setMisionActual(null);

      // Act & Assert
      assertDoesNotThrow(() -> donante.completarMisionSiCompletadaYGenerarNotificaciones());
    }
  }

  @Nested
  @DisplayName("Tests de Notificaciones y Transiciones de Categoría")
  class NotificacionesYCambioDeCategoriaTests {

    @Mock
    private Mision nuevaMisionMock;

    @BeforeEach
    void setupMisionAnterior() {
      lenient().when(misionActualMock.getCategoria()).thenReturn(categoriaMockA);
      lenient().when(misionActualMock.getNombre()).thenReturn("Misión 1");
    }

    @Test
    @DisplayName("Transición: Misma categoría. Notifica Misión, pero NO Notifica Categoría")
    void cambiarMisionMismaCategoriaNoNotificaCategoria() {
      // Arrange
      when(nuevaMisionMock.getCategoria()).thenReturn(categoriaMockA); // Misma categoría

      // Act
      donante.cambiarMisionActual(nuevaMisionMock);

      // Assert
      // 1. Debe haber guardado la misión completada
      assertEquals(1, donante.getMisionesCompletadas().size());

      // 2. Debe notificar misión completada
      verify(observerMock, times(1)).notificarMisionCompletada(any());

      // 3. NO debe notificar categoría completada porque siguen siendo iguales
      verify(observerMock, never()).notificarCategoriaCompletada(any());
    }

    @Test
    @DisplayName("Transición: Distinta categoría. Notifica Misión y Notifica Categoría")
    void cambiarMisionDistintaCategoriaNotificaAmbas() {
      // Arrange
      when(nuevaMisionMock.getCategoria()).thenReturn(categoriaMockB); // Nueva categoría
      when(misionActualMock.getCategoria()).thenReturn(categoriaMockA);

      // Act
      donante.cambiarMisionActual(nuevaMisionMock);

      // Assert
      verify(observerMock, times(1)).notificarMisionCompletada(any());

      // Verificamos que se llamó al observer con la categoría anterior y la nueva
      ArgumentCaptor<CategoriaCompletadaEvento> captor = ArgumentCaptor.forClass(CategoriaCompletadaEvento.class);
      verify(observerMock, times(1)).notificarCategoriaCompletada(captor.capture());
      assertEquals(captor.getValue().categoriaActual(), categoriaMockB);
      assertEquals(categoriaMockA, captor.getValue().categoriaAnterior().getCategoria());
      assertEquals(categoriaMockB, donante.getCategoria());
    }

    @Test
    @DisplayName("Transición a NULL: Termina el track de misiones, notifica categoría final")
    void cambiarMisionANullNotificaUltimaCategoria() {
      // Arrange
      donante.setCategoria(categoriaMockA); // Seteamos para simular estado previo real

      // Act
      donante.cambiarMisionActual(null);

      // Assert
      ArgumentCaptor<MisionCompletadaEvento> captorEvento = ArgumentCaptor.forClass(MisionCompletadaEvento.class);

      verify(observerMock, times(1)).notificarMisionCompletada(captorEvento.capture());
      assertNull(captorEvento.getValue().misionActual());

      // Al pasar a null, debe dar por completada la categoría anterior
      ArgumentCaptor<CategoriaCompletadaEvento> captorCategoria = ArgumentCaptor.forClass(CategoriaCompletadaEvento.class);
      verify(observerMock, times(1)).notificarCategoriaCompletada(captorCategoria.capture());
      assertEquals(captorCategoria.getValue().categoriaActual(), categoriaMockA);
      assertEquals(categoriaMockA, captorCategoria.getValue().categoriaAnterior().getCategoria());
    }

    @Test
    @DisplayName("Null Safety: Funciona correctamente si el Observer es null")
    void noLanzaExcepcionSiObserverEsNull() {
      // Arrange
      donante.setObserver(null);
      when(nuevaMisionMock.getCategoria()).thenReturn(categoriaMockB);

      // Act & Assert
      assertDoesNotThrow(() -> donante.cambiarMisionActual(nuevaMisionMock));
      assertEquals(1, donante.getCategoriasCompletadas().size(), "Debe guardar la categoría aunque no notifique");
    }
  }

  @Nested
  @DisplayName("Tests de Consultas y Estadísticas sobre Donaciones")
  class EstadisticasDonacionesTests {

    @Mock private Donacion donacion1;
    @Mock private Donacion donacion2;
    @Mock private Donacion donacion3;

    @Test
    @DisplayName("Total de Entidades: Filtra 'no recibidas' y agrupa duplicados (Set)")
    void getTotalEntidadesBeneficiariasQueRecibieron() {
      // Arrange
      // Entidad 1 recibe 2 donaciones (Debería contar como 1 sola entidad)
      when(donacion1.getFueRecibida()).thenReturn(true);
      when(donacion1.getEntidadBeneficiariaId()).thenReturn("1");

      when(donacion2.getFueRecibida()).thenReturn(true);
      when(donacion2.getEntidadBeneficiariaId()).thenReturn("1");

      // Entidad 2 NO recibe la donación (Debería filtrarse y no contar)
      when(donacion3.getFueRecibida()).thenReturn(false);

      // Evitamos StrictStubbingException con leniency si mockito chequea uso
      lenient().when(donacion3.getEntidadBeneficiariaId()).thenReturn("2");

      donante.registrarDonacion(donacion1);
      donante.registrarDonacion(donacion2);
      donante.registrarDonacion(donacion3);

      // Act
      Integer totalEntidades = donante.getTotalEntidadesBeneficiariasQueRecibieron();

      // Assert
      assertEquals(1, totalEntidades, "Solo debe contar a la Entidad 1 (sin duplicar) y excluir a la 2");
    }

    @Test
    @DisplayName("Fechas: Ordena correctamente aunque se inserten desordenadas")
    void obtenerPrimeraYUltimaDonacionOrdenCorrecto() {
      // Arrange
      when(donacion1.getFechaDeCreacion()).thenReturn(LocalDate.of(2023, 10, 15)); // Medio
      when(donacion2.getFechaDeCreacion()).thenReturn(LocalDate.of(2023, 1, 1));   // Primera
      when(donacion3.getFechaDeCreacion()).thenReturn(LocalDate.of(2023, 12, 31));  // Última

      // Insertamos desordenado a propósito
      donante.registrarDonacion(donacion1);
      donante.registrarDonacion(donacion3);
      donante.registrarDonacion(donacion2);

      // Act
      Optional<Donacion> primera = donante.obtenerPrimeraDonacion();
      Optional<Donacion> ultima = donante.obtenerUltimaDonacion();

      // Assert
      assertTrue(primera.isPresent());
      assertTrue(ultima.isPresent());
      assertEquals(donacion2, primera.get());
      assertEquals(donacion3, ultima.get());
    }

    @Test
    @DisplayName("Fechas Borde: Devuelve Optional.empty si no hay donaciones")
    void primeraYUltimaRetornanEmptySiNoHayDonaciones() {
      assertTrue(donante.obtenerPrimeraDonacion().isEmpty());
      assertTrue(donante.obtenerUltimaDonacion().isEmpty());
    }

    @Test
    @DisplayName("Mes Exacto: Valida matching estricto de Año y Mes (donoEnMes)")
    void donoEnMesValidaAnoYMesExactos() {
      // Arrange
      when(donacion1.getFechaDeCreacion()).thenReturn(LocalDate.of(2024, 2, 28));
      when(donacion2.getFechaDeCreacion()).thenReturn(LocalDate.of(2023, 2, 15)); // Mismo mes, distinto año

      donante.registrarDonacion(donacion1);
      donante.registrarDonacion(donacion2);

      // Act & Assert
      assertTrue(donante.donoEnMes(YearMonth.of(2024, 2)));
      assertTrue(donante.donoEnMes(YearMonth.of(2023, 2)));

      // Falso: Distinto mes
      assertFalse(donante.donoEnMes(YearMonth.of(2024, 3)));
      // Falso: Mismo mes de un año donde no donó
      assertFalse(donante.donoEnMes(YearMonth.of(2022, 2)));
    }
  }
}