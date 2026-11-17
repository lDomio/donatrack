package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaObtenidaRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresMisionCompletada.GeneradorPaqueteDonanteMisionCompletadaN8N;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.services.PaqueteService;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.DifusionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DifusionServiceTest {

  // Ya no usamos MensajeDifusionRepository ni DifusionAdapter,
  // mockeamos las dependencias actuales del Service.
  @Mock
  private PaqueteService mockPaqueteService;

  @Mock
  private GeneradorPaqueteDonanteMisionCompletadaN8N mockGenerador;

  @InjectMocks
  private DifusionServiceImpl difusionService;

  @Test
  void difundirMensajeObtencionInsignia_generaYGuardaPaqueteCorrectamente() {
    // GIVEN
    InsigniaObtenidaRequest request = new InsigniaObtenidaRequest(0L,"Juan", "Oro", "Mision 1");

    // Simulamos un paquete creado por nuestro generador
    Paquete paqueteMock = mock(Paquete.class);

    // Cuando el generador reciba los datos exactos del DTO, debe devolver nuestro paquete mockeado
    when(mockGenerador.generarPaqueteParaService("Juan", "Oro", "Mision 1"))
        .thenReturn(paqueteMock);

    // WHEN
    difusionService.difundirMensajeObtencionInsignia(request);

    // THEN
    // 1. Verificamos que se haya llamado al generador con los parámetros correctos extraídos del Request
    verify(mockGenerador, times(1)).generarPaqueteParaService("Juan", "Oro", "Mision 1");

    // 2. Verificamos que el paquete generado haya sido enviado al PaqueteService para guardarse
    verify(mockPaqueteService, times(1)).guardarPaquete(paqueteMock);
  }

  @Test
  void difundirMensajeObtencionInsignia_fallaElGuardado_lanzaExcepcionHaciaArriba() {
    // GIVEN
    InsigniaObtenidaRequest request = new InsigniaObtenidaRequest(0L,"Juan", "Oro", "Mision 1");
    Paquete paqueteMock = mock(Paquete.class);

    when(mockGenerador.generarPaqueteParaService("Juan", "Oro", "Mision 1"))
        .thenReturn(paqueteMock);

    // Simulamos que al intentar guardar el paquete en base de datos, ocurre un error
    doThrow(new RuntimeException("Error en base de datos al guardar paquete"))
        .when(mockPaqueteService).guardarPaquete(any(Paquete.class));

    // WHEN & THEN
    // Comprobamos que el servicio deja propagar (burbujear) la excepción, en vez de tragársela
    assertThrows(RuntimeException.class, () -> difusionService.difundirMensajeObtencionInsignia(request));

    // Verificamos que el flujo intentó hacerse completo
    verify(mockGenerador, times(1)).generarPaqueteParaService("Juan", "Oro", "Mision 1");
    verify(mockPaqueteService, times(1)).guardarPaquete(paqueteMock);
  }
}