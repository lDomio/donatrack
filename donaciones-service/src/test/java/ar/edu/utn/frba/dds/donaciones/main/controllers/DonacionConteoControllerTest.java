package ar.edu.utn.frba.dds.donaciones.main.controllers;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.controllers.advice.GlobalExceptionHandler;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.services.DonacionService;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DonacionConteoControllerTest {

    private DonacionService donacionService;
    private EntidadBeneficiariaService entidadBeneficiariaService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        donacionService = Mockito.mock(DonacionService.class);
        entidadBeneficiariaService = Mockito.mock(EntidadBeneficiariaService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new DonacionController(donacionService,entidadBeneficiariaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET conteo responde 200 con el objeto con contexto")
    void testConteoRespondeObjetoConContexto() throws Exception {
        when(donacionService.contarEntidadesAyudadasEsteMes(5L))
                .thenReturn(new EntidadesAyudadasResponse(5L, 2026, 10, 2L));

        mockMvc.perform(get("/donaciones-service/donaciones/donante/5/entidades-ayudadas-este-mes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donanteId").value(5))
                .andExpect(jsonPath("$.anio").value(2026))
                .andExpect(jsonPath("$.mes").value(10))
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    @DisplayName("GET conteo sin ayudas responde cantidad cero")
    void testConteoCero() throws Exception {
        when(donacionService.contarEntidadesAyudadasEsteMes(5L))
                .thenReturn(new EntidadesAyudadasResponse(5L, 2026, 10, 0L));

        mockMvc.perform(get("/donaciones-service/donaciones/donante/5/entidades-ayudadas-este-mes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(0));
    }

    @Test
    @DisplayName("GET conteo de donante inexistente responde 404")
    void testConteoDonanteInexistente404() throws Exception {
        when(donacionService.contarEntidadesAyudadasEsteMes(999L))
                .thenThrow(new ResourceNotFoundException("No se encontró el donante con ID: 999"));

        mockMvc.perform(get("/donaciones-service/donaciones/donante/999/entidades-ayudadas-este-mes"))
                .andExpect(status().isNotFound());
    }
}
