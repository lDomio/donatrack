package ar.edu.utn.frba.dds.notificacion;

import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.Destinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.destinatario.TipoDestinatario;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.notificacion.Notificacion;
import ar.edu.utn.frba.dds.notificacion.main.repositories.DestinatarioRepository;
import ar.edu.utn.frba.dds.notificacion.main.repositories.NotificacionRepository;
import ar.edu.utn.frba.dds.notificacion.main.services.dtos.MedioDeContactoDto;
import ar.edu.utn.frba.dds.notificacion.main.services.NotificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import ar.edu.utn.frba.dds.notificacion.main.modelsEntities.Notificador.Notificador;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinatarioServiceTest {
/*
    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private DestinatarioRepository destinatarioRepository;

    @Mock
    private Notificador notificador;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private List<MedioDeContactoDto> contactos;

    @BeforeEach
    void setUp() {
        contactos = List.of(new MedioDeContactoDto("juan@test.com", "EMAIL"));
        org.springframework.test.util.ReflectionTestUtils.setField(notificacionService, "notificadores", List.of(notificador));
    }

    @Test
    @DisplayName("notificarInactividad asigna idDestinatarioOrigen y tipoDestinatario PERSONA_FISICA")
    void notificarInactividadCreaDestinatarioConTipo() {
        when(destinatarioRepository.findByIdDestinatarioOrigenAndTipoDestinatario(1L, TipoDestinatario.PERSONA_FISICA))
                .thenReturn(Optional.empty());

        notificacionService.notificarInactividad(1L, TipoDestinatario.PERSONA_FISICA, "Juan", contactos);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, atLeastOnce()).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertNotNull(guardada.getDestinatario());
        assertNull(guardada.getDestinatario().getId(), "La PK local debe generarse por la base de datos");
        assertEquals(1L, guardada.getDestinatario().getIdDestinatarioOrigen());
        assertEquals(TipoDestinatario.PERSONA_FISICA, guardada.getDestinatario().getTipoDestinatario());
    }

    @Test
    @DisplayName("notificarAsignacionEntidad asigna tipoDestinatario ENTIDAD")
    void notificarAsignacionEntidadCreaTipoEntidad() {
        when(destinatarioRepository.findByIdDestinatarioOrigenAndTipoDestinatario(5L, TipoDestinatario.ENTIDAD))
                .thenReturn(Optional.empty());

        notificacionService.notificarAsignacionEntidad(5L, "Comedor Los Bajitos", contactos, "Arroz 50kg");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, atLeastOnce()).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertEquals(5L, guardada.getDestinatario().getIdDestinatarioOrigen());
        assertEquals(TipoDestinatario.ENTIDAD, guardada.getDestinatario().getTipoDestinatario());
    }

    @Test
    @DisplayName("notificarAsignacionDonante soporta PERSONA_JURIDICA")
    void notificarAsignacionDonantePersonaJuridica() {
        when(destinatarioRepository.findByIdDestinatarioOrigenAndTipoDestinatario(10L, TipoDestinatario.PERSONA_JURIDICA))
                .thenReturn(Optional.empty());

        notificacionService.notificarAsignacionDonante(10L, TipoDestinatario.PERSONA_JURIDICA, "Empresa S.A.", contactos, "Muebles", "Comedor");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, atLeastOnce()).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertEquals(10L, guardada.getDestinatario().getIdDestinatarioOrigen());
        assertEquals(TipoDestinatario.PERSONA_JURIDICA, guardada.getDestinatario().getTipoDestinatario());
    }

    @Test
    @DisplayName("Reutiliza destinatario existente si ya fue persistido")
    void reutilizaDestinatarioExistente() {
        Destinatario existente = new Destinatario(1L, TipoDestinatario.PERSONA_FISICA, "Juan", null);
        existente.setId(99L);

        when(destinatarioRepository.findByIdDestinatarioOrigenAndTipoDestinatario(1L, TipoDestinatario.PERSONA_FISICA))
                .thenReturn(Optional.of(existente));

        notificacionService.notificarInactividad(1L, TipoDestinatario.PERSONA_FISICA, "Juan Actualizado", contactos);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, atLeastOnce()).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertSame(existente, guardada.getDestinatario());
        assertEquals(99L, guardada.getDestinatario().getId());
    }
*/
}
