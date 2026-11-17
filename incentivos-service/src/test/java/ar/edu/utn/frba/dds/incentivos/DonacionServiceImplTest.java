package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.dtos.ActualizarDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteEventoObserver;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteCategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteMisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.EstadoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionHabilDonador;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService; // Cambiado a interfaz
import ar.edu.utn.frba.dds.incentivos.main.services.impl.DonacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonacionServiceImplTest {

    @Mock
    DonanteService donanteService; // Ahora mockeamos la interfaz, que es lo que inyecta el Service
    @Mock
    DonacionRepository donacionRepository;

    // Reemplazamos el MensajeDifusionRepository por mocks de los generadores
    @Mock
    IGeneradorPaqueteDonanteMisionCompletada generadorMision;
    @Mock
    IGeneradorPaqueteDonanteCategoriaCompletada generadorCategoria;

    @InjectMocks
    DonacionServiceImpl service;

    @Captor
    ArgumentCaptor<Donacion> donacionArgumentCaptor;

    private Donante donante;
    private CrearDonacionRequest req;
    private DonanteEventoObserver observer;

    @BeforeEach
    void setUp() {
        donante = new Donante();
        donante.setId(1L);
        donante.setDonaciones(new ArrayList<>());
        donante.setMisionesCompletadas(new ArrayList<>());
        donante.setInsignias(new ArrayList<>());

        CategoriaDonante catColaborador = new CategoriaDonante("COLABORADOR", null, 1);
        Mision m1 = new Mision("Habil donador", new Insignia("Primer Paso"), new MisionHabilDonador(5.0));
        m1.setCategoria(catColaborador);
        catColaborador.setPrimeraMision(m1);

        donante.setCategoria(catColaborador);
        donante.setMisionActual(m1);
        donante.setProgresoMisionActual(m1.calcularProgreso(donante));

        // Inyectamos manualmente el observer con nuestros mocks generadores
        observer = new DonanteEventoObserver(donante, List.of(generadorCategoria), List.of(generadorMision));
        donante.setObserver(observer);
    }

    @Test
    void agregarDonacion_noCompletaMision_guardarDonacionSinGenerarPaquetes() {
        // Arrange
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 3.0, "Ropa", "Indumentaria", null);
        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));

        // Act
        Donacion resultado = service.agregarDonacion(req);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.getCantidadDeBien());
        assertEquals(0, donante.getMisionesCompletadas().size());

        // Verificamos que NO se intentó generar ningún paquete
        verify(generadorMision, never()).generarPaqueteSiCorresponde(any());
        assertTrue(donante.getPaquetesPendientes().isEmpty());

        verify(donacionRepository, times(1)).save(any());
        verify(donanteService, times(1)).guardarDonante(donante);
    }

    @Test
    void agregarDonacion_completaMision_guardaDonacionYGeneraPaquetes() {
        // Arrange
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 5.0, "Ropa", "Indumentaria", null);
        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));

        // Simulamos que el generador crea un paquete exitosamente
        Paquete paqueteMock = mock(Paquete.class);
        when(generadorMision.generarPaqueteSiCorresponde(any()))
            .thenReturn(Optional.of(paqueteMock));

        // Act
        Donacion resultado = service.agregarDonacion(req);

        // Assert
        assertEquals(1, donante.getMisionesCompletadas().size());
        assertEquals(1, donante.getInsignias().size());
        assertEquals("Primer Paso", donante.getInsignias().get(0).getDescripcion());

        // Verificamos que se llamó al generador y el observer guardó el paquete
        verify(generadorMision, times(1)).generarPaqueteSiCorresponde(argThat(record -> record.donante().equals(donante)));
        assertEquals(1, donante.getPaquetesPendientes().size());
        assertTrue(donante.getPaquetesPendientes().contains(paqueteMock));
    }

    @Test
    void agregarDonacion_generadorRecibeDatosCorrectos() {
        // Arrange
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 5.0, "Ropa", "Indumentaria", null);
        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));

        when(generadorMision.generarPaqueteSiCorresponde(any()))
            .thenReturn(Optional.empty());

        // Act
        service.agregarDonacion(req);

        // Assert
        ArgumentCaptor<Insignia> insigniaCaptor = ArgumentCaptor.forClass(Insignia.class);


        ArgumentCaptor<MisionCompletadaEvento> recordCaptor = ArgumentCaptor.forClass(MisionCompletadaEvento.class);

        verify(generadorMision).generarPaqueteSiCorresponde(recordCaptor.capture());

        MisionCompletadaEvento recordCapturado = recordCaptor.getValue();

        assertEquals(donante, recordCapturado.donante());
        Insignia insignia = recordCapturado.insigniaOtorgada();


        assertEquals("Primer Paso", insignia.getDescripcion());
    }

    @Test
    void agregarDonacion_donanteNoExistente_lanzaExcepcion() {
        // Arrange
        req = new CrearDonacionRequest(null, 999L, LocalDate.now(), 5.0, "Ropa", "Indumentaria", null);
        when(donanteService.findById(999L)).thenReturn(Optional.empty());

        // Act
        assertThrows(RuntimeException.class, () -> service.agregarDonacion(req));

        // Assert
        verifyNoInteractions(donacionRepository, generadorMision, generadorCategoria);
    }

    @Test
    void agregarDonacion_consecutivas_completaMisionYNoSubeDeCategoria() {
        // Arrange
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 3.0, "Ropa", "Indumentaria", null);
        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));

        // Act
        service.agregarDonacion(req);

        // Assert
        assertEquals("COLABORADOR", donante.getCategoria().getNombre());

        // Arrange
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 2.0, "Ropa", "Indumentaria", null);

        // Act
        service.agregarDonacion(req);

        // Assert
        assertNotEquals("SOSTENEDOR", donante.getCategoria().getNombre());
    }

    @Test
    void actualizarDonacion_asignaEntidadBeneficiaria() throws Exception {
        // Arrange
        Donacion donacion = new Donacion();
        donacion.setId(10L);
        donacion.setDonante(donante);
        donacion.setEntidadBeneficiariaId(null);

        lenient().when(donanteService.findById(1L)).thenReturn(Optional.of(donante));
        lenient().when(donacionRepository.findById(10L)).thenReturn(Optional.of(donacion));
        ActualizarDonacionRequest req = new ActualizarDonacionRequest(10L, "Fundación X");

        // Act
        Donacion resultado = service.marcarDonacionComoRecibida(req.idDonacion(), req.razonSocialEntidadBeficiaria());

        // Assert
        assertEquals("Fundación X", resultado.getEntidadBeneficiariaId());
        verify(donacionRepository, times(1)).save(donacion);
    }

    @Test
    void agregarDonacion_completaMision_subeDeCategoriaConPrints() {
        // Arrange
        System.out.println("\n=== INICIO TEST: agregarDonacion_completaMision_subeDeCategoriaConPrints ===");

        CategoriaDonante catColaborador = new CategoriaDonante("COLABORADOR", null, 1);
        Mision m1 = new Mision("Habil donador", new Insignia("Primer Paso"), new MisionHabilDonador(5.0));
        m1.setCategoria(catColaborador);
        catColaborador.setPrimeraMision(m1);

        CategoriaDonante catSostenedor = new CategoriaDonante("SOSTENEDOR", null, 1);
        Mision m2 = new Mision("Habil donador 2", new Insignia("Segundo Paso"), new MisionHabilDonador(10.0));
        m2.setCategoria(catSostenedor);
        catSostenedor.setPrimeraMision(m2);

        m1.setMisionSiguiente(m2);
        m2.setMisionAnterior(m1);

        donante.setCategoria(catColaborador);
        donante.setMisionActual(m1);
        donante.setProgresoMisionActual(m1.calcularProgreso(donante));

        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));
        req = new CrearDonacionRequest(null, 1L, LocalDate.now(), 5.0, "Ropa", "Indumentaria", null);

        // Act
        service.agregarDonacion(req);

        // Assert/Prints
        assertEquals("SOSTENEDOR", donante.getCategoria().getNombre());
        assertEquals(m2, donante.getMisionActual());
        assertEquals(1, donante.getCategoriasCompletadas().size());
        assertEquals("COLABORADOR", donante.getCategoriasCompletadas().get(0).getCategoria().getNombre());
        assertEquals(1, donante.getInsignias().size());
        assertEquals("Primer Paso", donante.getInsignias().get(0).getDescripcion());

        System.out.println("=== FIN TEST: agregarDonacion_completaMision_subeDeCategoriaConPrints ===\n");
    }
}