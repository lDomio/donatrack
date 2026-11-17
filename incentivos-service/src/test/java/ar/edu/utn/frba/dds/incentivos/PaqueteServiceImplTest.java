package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.EnrutadorDePaquetes;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.IPaqueteAdapter;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteN8N.PaqueteN8N;
import ar.edu.utn.frba.dds.incentivos.main.repositories.PaqueteRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.PaqueteServiceImpl;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceImplTest {

  @Mock
  private PaqueteRepository repository;

  @Test
  void siElAdapterFallaNoMarcaElPaqueteComoEnviadoYGuardaElIntento() {
    PaqueteN8N paquete = spy(new PaqueteN8N(Map.of("nombreInsignia", "Novato")));
    when(repository.findByFueEnviadoFalse()).thenReturn(List.of(paquete));

    IPaqueteAdapter<PaqueteN8N> adapterConFalla = new IPaqueteAdapter<>() {
      @Override
      public void enviar(PaqueteN8N paquete) {
        paquete.avisarEnvioFallido();
        throw new RuntimeException("n8n no disponible");
      }

      @Override
      public Class<PaqueteN8N> tipoDePaqueteSoportado() {
        return PaqueteN8N.class;
      }
    };
    PaqueteServiceImpl service = new PaqueteServiceImpl(repository, new EnrutadorDePaquetes(List.of(adapterConFalla)));

    service.enviarPaquetes();

    verify(paquete, never()).marcarComoEnviado();
    verify(repository).save(paquete);
    assertEquals(1, paquete.getCantidadDeIntentos());
  }

  @Test
  void siElAdapterFuncionaMarcaElPaqueteComoEnviado() {
    PaqueteN8N paquete = spy(new PaqueteN8N(Map.of("nombreInsignia", "Novato")));
    when(repository.findByFueEnviadoFalse()).thenReturn(List.of(paquete));

    IPaqueteAdapter<PaqueteN8N> adapterOk = new IPaqueteAdapter<>() {
      @Override
      public void enviar(PaqueteN8N paquete) {
      }

      @Override
      public Class<PaqueteN8N> tipoDePaqueteSoportado() {
        return PaqueteN8N.class;
      }
    };
    PaqueteServiceImpl service = new PaqueteServiceImpl(repository, new EnrutadorDePaquetes(List.of(adapterOk)));

    service.enviarPaquetes();

    verify(paquete).marcarComoEnviado();
    verify(repository).save(paquete);
  }
}
