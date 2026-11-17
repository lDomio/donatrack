package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NecesidadTest {

  private Necesidad necesidad;

  @Mock
  private EntidadBeneficiaria entidadBeneficiariaMock;

  @Mock
  private Donacion donacionMock;

  @BeforeEach
  void setUp() {
    necesidad = new Necesidad();
    necesidad.setEntidadBeneficiaria(entidadBeneficiariaMock);
  }

  @Nested
  @DisplayName("Tests para Necesidad EXTRAORDINARIA")
  class NecesidadExtraordinariaTests {

    @BeforeEach
    void setUpExtraordinaria() {
      necesidad.setTipoDeNecesidad(new TipoNecesidadExtraordinaria());
      necesidad.setCantidadDonadaTotal(0.0); // Estado inicial
    }

    @Test
    @DisplayName("Debe devolver el tipo de necesidad correcto")
    void getTipoNecesidad() {
      assertEquals(TipoNecesidadEnum.EXTRAORDINARIA, necesidad.getTipoNecesidad());
    }

    @Test
    @DisplayName("Al aumentar cantidad, se suma directamente al total de la necesidad")
    void aumentarCantidad() {
      necesidad.agregarDonacion(crearDonacionMockeada(50.5));
      necesidad.agregarDonacion(crearDonacionMockeada(10.0));

      assertEquals(60.5, necesidad.getCantidadDonada());
    }

    @Test
    @DisplayName("El último periodo de una necesidad extraordinaria debe ser empty")
    void getUltimoPeriodoEsNull() {
      assertTrue(necesidad.getUltimoPeriodo().isEmpty());
    }

    @Test
    @DisplayName("Al agregar una donación, avisa a la entidad y aumenta la cantidad")
    void agregarDonacion() {
      when(donacionMock.getCantidad()).thenReturn(100.0);

      necesidad.agregarDonacion(donacionMock);

      verify(entidadBeneficiariaMock, times(1)).agregarDonacion(donacionMock);
      assertEquals(100.0, necesidad.getCantidadDonada());
    }

    @Test
    @DisplayName("Al desasignar una donación, avisa a la entidad y resta la cantidad total")
    void desasignarDonacionExtraordinaria() {
      // 1. Configuramos el estado inicial (simulamos que ya había 150 donados)
      necesidad.setCantidadDonadaTotal(150.0);

      // 2. Configuramos el mock de la donación que vamos a desasignar
      when(donacionMock.getCantidad()).thenReturn(50.0);
      when(donacionMock.getNecesidad()).thenReturn(necesidad);

      // 3. Ejecutamos la acción
      necesidad.desasignar(donacionMock);

      // 4. Verificaciones
      verify(entidadBeneficiariaMock, times(1)).removerDonacion(donacionMock);
      assertEquals(100.0, necesidad.getCantidadDonadaTotal(), "Se debió restar 50 al total de 150");
    }
  }

  @Nested
  @DisplayName("Tests para Necesidad RECURRENTE")
  class NecesidadRecurrenteTests {

    @BeforeEach
    void setUpRecurrente() {
      necesidad.setTipoDeNecesidad(new TipoNecesidadRecurrente());
      necesidad.setPeriodoCorrespondiente(Period.ofDays(10)); // Periodos de 10 días
    }

    @Test
    @DisplayName("Debe devolver el tipo de necesidad correcto")
    void getTipoNecesidad() {
      assertEquals(TipoNecesidadEnum.RECURRENTE, necesidad.getTipoNecesidad());
    }

    @Test
    @DisplayName("Si no tiene historial, genera el primer periodo al consultarlo")
    void generacionDePrimerPeriodo() {
      LocalDate inicio = LocalDate.now();
      necesidad.setInicioRecurrencia(inicio);

      Optional<Periodo> ultimoPeriodoOpt = necesidad.getUltimoPeriodo();

      assertTrue(ultimoPeriodoOpt.isPresent());
      Periodo ultimoPeriodGet = ultimoPeriodoOpt.get();
      assertNull(ultimoPeriodGet.getId(), "El ID debería ser null antes de persistir");
      assertEquals(inicio, ultimoPeriodGet.getFechaInicio());
      assertEquals(inicio.plusDays(10), ultimoPeriodGet.getFechaFin());
      assertEquals(0.0, ultimoPeriodGet.getCantidadDonada());
    }

    @Test
    @DisplayName("Genera múltiples periodos pasados hasta alcanzar la fecha actual")
    void generacionDeMultiplesPeriodos() {
      LocalDate inicioHistorico = LocalDate.now().minusDays(25);
      necesidad.setInicioRecurrencia(inicioHistorico);

      Optional<Periodo> ultimoPeriodo = necesidad.getUltimoPeriodo();

      assertEquals(3, necesidad.getHistorialPeriodos().size(), "Debería haber generado 3 periodos históricos");
      assertTrue(ultimoPeriodo.isPresent());
      assertEquals(LocalDate.now().plusDays(5), ultimoPeriodo.get().getFechaFin());
    }

    @Test
    @DisplayName("Al agregar una donación, avisa a la entidad y aumenta el periodo activo")
    void agregarDonacionAfectaAlPeriodo() {
      necesidad.setInicioRecurrencia(LocalDate.now());
      when(donacionMock.getCantidad()).thenReturn(75.0);

      necesidad.agregarDonacion(donacionMock);

      verify(entidadBeneficiariaMock, times(1)).agregarDonacion(donacionMock);
      assertTrue(necesidad.getUltimoPeriodo().isPresent());
      assertEquals(75.0, necesidad.getUltimoPeriodo().get().getCantidadDonada());
    }

    @Test
    @DisplayName("Al desasignar, avisa a la entidad y resta la cantidad del periodo específico de la donación")
    void desasignarDonacionRecurrente() {
      necesidad.setInicioRecurrencia(LocalDate.now());

      // 1. Forzamos la creación del periodo actual llamando a getUltimoPeriodo
      assertTrue(necesidad.getUltimoPeriodo().isPresent());
      Periodo periodoActual = necesidad.getUltimoPeriodo().get();
      periodoActual.setCantidadDonada(200.0); // Simulamos que ese periodo ya tiene 200 donados

      // 2. Configuramos el mock de la donación para que pertenezca a ese periodo
      when(donacionMock.getCantidad()).thenReturn(50.0);
      when(donacionMock.getPeriodo()).thenReturn(periodoActual);

      // 3. Ejecutamos la desasignación
      necesidad.desasignar(donacionMock);

      // 4. Verificaciones
      verify(entidadBeneficiariaMock, times(1)).removerDonacion(donacionMock);
      assertEquals(150.0, periodoActual.getCantidadDonada(), "Se debió restar 50 al periodo que tenía 200");
    }
  }

  @Nested
  @DisplayName("Tests de Integración (Donacion -> Necesidad)")
  class DonacionANecesidadTests {

    @Test
    @DisplayName("Donacion.desasignar() limpia las referencias y delega a la necesidad")
    void donacionLimpiaReferenciasAlDesasignar() {
      // ¡CORRECCIÓN ACÁ! Le asignamos un mock del tipo de necesidad para evitar el NullPointerException.
      // Usamos un mock porque en este test específico no nos importa la lógica de suma/resta,
      // solo queremos verificar que Donacion delegue correctamente a Necesidad.
      necesidad.setTipoDeNecesidad(mock(TipoNecesidad.class));

      // Este test prueba la lógica desde la clase Donacion
      Donacion donacion = new Donacion();
      donacion.setPeriodo(new Periodo(Period.ofDays(10), LocalDate.now(), 0.0, necesidad));

      // Espiamos la necesidad (usamos un spy en vez de mock para poder verificar llamadas sin perder el estado real)
      Necesidad necesidadSpy = spy(necesidad);
      donacion.setNecesidad(necesidadSpy);

      // Ejecutamos
      donacion.desasignar();

      // Verificamos que se llamó al método de la necesidad
      verify(necesidadSpy, times(1)).desasignar(donacion);

      // Verificamos que se limpiaron las referencias en la donación
      assertNull(donacion.getNecesidad(), "La necesidad en la donación debe quedar en null");
      assertNull(donacion.getPeriodo(), "El periodo en la donación debe quedar en null");
    }

  }

  // --- Métodos de utilidad para tests ---

  private Donacion crearDonacionMockeada(Double cantidad) {
    Donacion mock = mock(Donacion.class);
    when(mock.getCantidad()).thenReturn(cantidad);
    return mock;
  }
}
