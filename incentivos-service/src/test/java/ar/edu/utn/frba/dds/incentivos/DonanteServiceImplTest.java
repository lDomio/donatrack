package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonanteRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.DonanteResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteObserverFactory;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.MisionService;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.DonanteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonanteServiceImplTest {

    @Mock
    private DonanteRepository donanteRepository;

    @Mock
    private DonanteObserverFactory observerFactory;

    @Mock
    private MisionService misionService;

    @InjectMocks
    private DonanteServiceImpl donanteService;

    private Donante donanteGuardado;

    @BeforeEach
    void setUp() {
        donanteGuardado = new Donante();
        donanteGuardado.setId(10L); // PK local en incentivos
        donanteGuardado.setIdDonanteDonacionesService(100L); // ID de origen en donaciones-service
        donanteGuardado.setNombreUsuario("testUser");
    }

    @Test
    @DisplayName("agregarDonante guarda el id_donante_donaciones_service y deja id para autogenerar")
    void agregarDonanteGuardaIdDeOrigen() {
        CrearDonanteRequest req = new CrearDonanteRequest(100L, 100L,null, List.of());

        when(donanteRepository.findByIdDonanteDonacionesService(100L)).thenReturn(Optional.empty());
        when(donanteRepository.findById(100L)).thenReturn(Optional.empty());
        when(misionService.obtenerPrimeraMision()).thenReturn(Optional.empty());

        DonanteResponse response = donanteService.agregarDonante(req);

        ArgumentCaptor<Donante> captor = ArgumentCaptor.forClass(Donante.class);
        verify(donanteRepository, times(1)).save(captor.capture());

        Donante guardado = captor.getValue();
        assertNull(guardado.getId(), "El ID local debe ser nulo para que la base de datos lo autogenere");
        assertEquals(100L, guardado.getIdDonanteDonacionesService(), "El ID de donaciones debe persistirse");
        assertEquals(100L, response.idDonanteDonacionesService());
    }

    @Test
    @DisplayName("agregarDonante es idempotente si ya existe por idDonanteDonacionesService")
    void agregarDonanteIdempotenteSiExiste() {
        CrearDonanteRequest req = new CrearDonanteRequest(100L, 100L,null, List.of());

        when(donanteRepository.findByIdDonanteDonacionesService(100L)).thenReturn(Optional.of(donanteGuardado));

        DonanteResponse response = donanteService.agregarDonante(req);

        assertNotNull(response);
        assertEquals(10L, response.idUsuario());
        assertEquals(100L, response.idDonanteDonacionesService());
        assertEquals(100L, donanteGuardado.getIdUsuarioAuth());
        verify(donanteRepository, times(1)).save(donanteGuardado);
    }

    @Test
    @DisplayName("findById busca por PK local y si no existe busca por idDonanteDonacionesService")
    void findByIdFallbackAIdOrigen() {
        // Cuando busca por el ID de origen (100L) que no coincide con la PK (10L):
        when(donanteRepository.findById(100L)).thenReturn(Optional.empty());
        when(donanteRepository.findByIdDonanteDonacionesService(100L)).thenReturn(Optional.of(donanteGuardado));

        Optional<Donante> resultado = donanteService.findById(100L);

        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getId());
        assertEquals(100L, resultado.get().getIdDonanteDonacionesService());
    }

    @Test
    @DisplayName("findByIdDonanteDonacionesService busca directamente por ID de origen")
    void findByIdDonanteDonacionesServiceDirecto() {
        when(donanteRepository.findByIdDonanteDonacionesService(100L)).thenReturn(Optional.of(donanteGuardado));

        Optional<Donante> resultado = donanteService.findByIdDonanteDonacionesService(100L);

        assertTrue(resultado.isPresent());
        assertEquals(100L, resultado.get().getIdDonanteDonacionesService());
    }
}
