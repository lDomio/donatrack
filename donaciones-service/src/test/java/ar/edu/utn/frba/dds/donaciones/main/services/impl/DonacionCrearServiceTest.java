package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.donaciones.main.config.ClockConfig;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.BienRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DonacionCrearServiceTest {

    private static final Long ID_DONANTE = 5L;

    private DonacionRepository donacionRepository;
    private DonanteService donanteService;
    private IncentivosClient incentivosClient;
    private DonacionServiceImpl donacionService;

    @BeforeEach
    void setUp() {
        donacionRepository = Mockito.mock(DonacionRepository.class);
        donanteService = Mockito.mock(DonanteService.class);
        incentivosClient = Mockito.mock(IncentivosClient.class);
        //15 de octubre de 2026 en la zona del negocio
        Clock clockFijo = Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ClockConfig.ZONA);

        when(donanteService.obtenerDonante(ID_DONANTE)).thenReturn(new Humana());
        when(donacionRepository.save(any(Donacion.class))).thenAnswer(inv -> inv.getArgument(0));

        donacionService = new DonacionServiceImpl(
                donacionRepository, donanteService, null, null, null, incentivosClient, null, null, clockFijo);
    }

    @Test
    @DisplayName("Rechaza un bien con fecha de vencimiento anterior a hoy y no persiste nada")
    void testRechazaBienVencido() {
        LocalDate ayer = LocalDate.of(2026, 10, 14);
        CrearDonacionRequest request = new CrearDonacionRequest(ID_DONANTE, "Lote",
                List.of(bien("Leche vencida", ayer)));

        BusinessException ex = assertThrows(BusinessException.class, () -> donacionService.crearDonacion(request));

        System.out.println("Mensaje: " + ex.getMessage());
        assertTrue(ex.getMessage().contains("Leche vencida"));
        assertTrue(ex.getMessage().contains("2026-10-14"));
        verify(donacionRepository, never()).save(any());
        verifyNoInteractions(incentivosClient);
    }

    @Test
    @DisplayName("Un solo bien vencido dentro de un lote aborta todo el lote")
    void testUnBienVencidoAbortaElLote() {
        CrearDonacionRequest request = new CrearDonacionRequest(ID_DONANTE, "Lote variado", List.of(
                bien("Sillas", null),
                bien("Fideos", LocalDate.of(2026, 12, 31)),
                bien("Latas vencidas", LocalDate.of(2026, 1, 1))));

        assertThrows(BusinessException.class, () -> donacionService.crearDonacion(request));

        verify(donacionRepository, never()).save(any());
        verifyNoInteractions(incentivosClient);
    }

    @Test
    @DisplayName("Un bien que vence hoy es válido y la donación queda en DEPOSITO")
    void testBienQueVenceHoyEsValido() {
        LocalDate hoy = LocalDate.of(2026, 10, 15);
        CrearDonacionRequest request = new CrearDonacionRequest(ID_DONANTE, "Lote",
                List.of(bien("Yogur", hoy)));

        List<DonacionResponse> resultado = donacionService.crearDonacion(request);

        assertEquals(1, resultado.size());
        assertEquals(TipoDeEstado.DEPOSITO, resultado.get(0).estado());
    }

    private BienRequest bien(String nombre, LocalDate vencimiento) {
        return new BienRequest(nombre, null, 1.0, "UNIDADES", null, "NUEVO", vencimiento, null);
    }
}
