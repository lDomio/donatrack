package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.config.ClockConfig;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DonacionConteoServiceTest {

    private DonacionRepository donacionRepository;
    private DonanteService donanteService;
    private DonacionServiceImpl donacionService;
    private Clock clockFijo;

    @BeforeEach
    void setUp() {
        donacionRepository = Mockito.mock(DonacionRepository.class);
        donanteService = Mockito.mock(DonanteService.class);
        //15 de octubre de 2026 en la zona del negocio
        clockFijo = Clock.fixed(
                Instant.parse("2026-10-15T12:00:00Z"),
                ClockConfig.ZONA);
        donacionService = new DonacionServiceImpl(
                donacionRepository, donanteService, null, null, null, null, null, null, clockFijo);
    }

    @Test
    @DisplayName("Cuenta entidades distintas del mes con rango primero/ultimo dia")
    void testContarEntidadesDistintasDelMes() {
        when(donanteService.obtenerDonante(5L)).thenReturn(new Humana());
        when(donacionRepository.contarEntidadesDistintasAyudadas(
                eq(5L), eq(LocalDate.of(2026, 10, 1)), eq(LocalDate.of(2026, 10, 31))))
                .thenReturn(2L);

        EntidadesAyudadasResponse response = donacionService.contarEntidadesAyudadasEsteMes(5L);

        assertEquals(5L, response.donanteId());
        assertEquals(2026, response.anio());
        assertEquals(10, response.mes());
        assertEquals(2L, response.cantidad());
        verify(donacionRepository).contarEntidadesDistintasAyudadas(
                eq(5L), eq(LocalDate.of(2026, 10, 1)), eq(LocalDate.of(2026, 10, 31)));
    }

    @Test
    @DisplayName("Sin ayudas este mes responde cantidad cero")
    void testSinAyudasRespondeCero() {
        when(donanteService.obtenerDonante(5L)).thenReturn(new Humana());
        when(donacionRepository.contarEntidadesDistintasAyudadas(eq(5L), any(), any()))
                .thenReturn(0L);

        EntidadesAyudadasResponse response = donacionService.contarEntidadesAyudadasEsteMes(5L);

        assertEquals(0L, response.cantidad());
        assertEquals(2026, response.anio());
        assertEquals(10, response.mes());
    }

    @Test
    @DisplayName("Donante inexistente propaga 404 sin consultar el conteo")
    void testDonanteInexistenteNoCuenta() {
        when(donanteService.obtenerDonante(999L))
                .thenThrow(new ResourceNotFoundException("No se encontró el donante con ID: 999"));

        assertThrows(ResourceNotFoundException.class,
                () -> donacionService.contarEntidadesAyudadasEsteMes(999L));
        verify(donacionRepository, never()).contarEntidadesDistintasAyudadas(any(), any(), any());
    }
}
