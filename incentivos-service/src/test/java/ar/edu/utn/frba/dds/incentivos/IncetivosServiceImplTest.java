package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.dtos.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MetricasUsuarioResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.services.DonacionService;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import ar.edu.utn.frba.dds.incentivos.main.services.impl.IncentivoServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncetivosServiceImplTest {

    @Mock
    DonacionService donacionService;
    @Mock
    DonanteService donanteService;
    @Mock
    RankingService rankingService;
    @InjectMocks
    IncentivoServiceImpl service;

    @Test
    void getMetricas_conDonanteSinDonaciones_devuelveMetricasEnCero() {
        Donante donante = new Donante();
        donante.setDonaciones(List.of());
        // Arrange
        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));
        when(rankingService.buscarPosicionDonanteEnRanking(1L)).thenReturn(0);

        // Act
        MetricasUsuarioResponse res = service.getMetricasUsuario(1L, null, null, null);

        // Assert
        assertEquals(0, res.totalHistoricoDonaciones());
        assertEquals(0, res.organizacionesQueAyudo());
        assertTrue(res.donacionesPorPeriodo().isEmpty());
    }

    @Test
    void getMetricas_conDonacionYRanking_retornaMetricasCalculadas() {
        // Arrange
        Donacion d1 = new Donacion();
        d1.setFechaDeCreacion(LocalDate.of(2026, 4, 15));
        d1.setCantidadDeBien(10.0);
        d1.setCategoria("Indumentaria");
        d1.setEntidadBeneficiariaId("Fundación X");
        d1.setFueRecibida(true);

        Donacion d2 = new Donacion();
        d2.setFechaDeCreacion(LocalDate.of(2026, 4, 20));
        d2.setCantidadDeBien(3.0);
        d2.setCategoria("Indumentaria");
        d2.setEntidadBeneficiariaId(null);
        d2.setFueRecibida(true);

        Donante donante = new Donante();
        donante.setDonaciones(List.of(d1, d2));

        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));
        when(rankingService.buscarPosicionDonanteEnRanking(1L)).thenReturn(3);

        // Act
        MetricasUsuarioResponse res = service.getMetricasUsuario(1L, null, null, null);

        // Assert
        assertEquals(2, res.totalHistoricoDonaciones());
        assertEquals(3, res.posicionRanking());
    }

    @Test
    void getEstadoGeneral_exponeCategoriaSiguienteMisionYProgreso() {
        CategoriaDonante actual = new CategoriaDonante("COLABORADOR_INICIAL", null, 2);
        CategoriaDonante siguiente = new CategoriaDonante("COLABORADOR_ACTIVO", null, 2);
        Insignia insignia = new Insignia("Insignia de prueba");

        Mision misionSiguiente = new Mision("Mision 2", insignia, null);
        misionSiguiente.setCategoria(siguiente);
        Mision misionActual = new Mision("Mision 1", insignia, null);
        misionActual.setCategoria(actual);
        misionActual.setMisionSiguiente(misionSiguiente);

        Donante donante = new Donante();
        donante.setCategoria(actual);
        donante.setMisionActual(misionActual);
        donante.setProgresoMisionActual(new ProgresoMision(50.0, 1.0, 2.0));
        donante.setMisionesCompletadas(List.of(new MisionCompletada(insignia, misionActual)));

        when(donanteService.findById(1L)).thenReturn(Optional.of(donante));

        EstadoGeneralResponse res = service.getEstadoGeneral(1L);

        assertEquals("COLABORADOR_INICIAL", res.categoriaActual().nombre());
        assertEquals("COLABORADOR_ACTIVO", res.categoriaSiguiente().nombre());
        assertEquals("Mision 1", res.misionActual().nombre());
        assertEquals("Insignia de prueba", res.misionActual().insignia().descripcion());
        assertEquals(50.0, res.progresoMisionActual().progreso());
        assertEquals(1, res.misionesCompletadasCategoria());
        assertEquals(2, res.misionesRequeridasCategoria());
    }
}
