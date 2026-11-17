package ar.edu.utn.frba.dds.donaciones.main.controllers;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.donaciones.main.controllers.advice.GlobalExceptionHandler;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.donaciones.main.services.EntidadBeneficiariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EntidadFiltroControllerTest {

    private EntidadBeneficiariaService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(EntidadBeneficiariaService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new EntidadBeneficiariaController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET sin params delega con nulls y responde 200")
    void testSinParams() throws Exception {
        when(service.buscarConFiltros(isNull(), isNull(), isNull(), isNull())).thenReturn(List.of());

        mockMvc.perform(get("/donaciones-service/entidades-beneficiarias"))
                .andExpect(status().isOk());

        verify(service).buscarConFiltros(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("GET con filtros finos pasa los params al servicio")
    void testConFiltros() throws Exception {
        EntidadBeneficiariaResponse r = new EntidadBeneficiariaResponse(
                1L, "Fundacion Vida", "ONG", List.of(), List.of(), List.of(), null,null);
        when(service.buscarConFiltros(isNull(), eq("vida"), eq("caba"), eq("Salud"))).thenReturn(List.of(r));

        mockMvc.perform(get("/donaciones-service/entidades-beneficiarias")
                        .param("nombre", "vida")
                        .param("ubicacion", "caba")
                        .param("tipo", "Salud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].razonSocial").value("Fundacion Vida"));

        verify(service).buscarConFiltros(isNull(), eq("vida"), eq("caba"), eq("Salud"));
    }

    @Test
    @DisplayName("GET con busqueda la reenvia como primer param")
    void testConBusqueda() throws Exception {
        EntidadBeneficiariaResponse r = new EntidadBeneficiariaResponse(
                1L, "Comedor Los Bajitos", "COMEDOR", List.of(), List.of(), List.of(), null, null);
        when(service.buscarConFiltros(eq("bajitos"), isNull(), isNull(), isNull())).thenReturn(List.of(r));

        mockMvc.perform(get("/donaciones-service/entidades-beneficiarias")
                        .param("busqueda", "bajitos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].razonSocial").value("Comedor Los Bajitos"));

        verify(service).buscarConFiltros(eq("bajitos"), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("GET con tipo desconocido responde 400")
    void testTipoDesconocido400() throws Exception {
        when(service.buscarConFiltros(isNull(), isNull(), isNull(), eq("INEXISTENTE_XYZ")))
                .thenThrow(new BusinessException("Tipo de entidad desconocido: 'INEXISTENTE_XYZ'"));

        mockMvc.perform(get("/donaciones-service/entidades-beneficiarias")
                        .param("tipo", "INEXISTENTE_XYZ"))
                .andExpect(status().isBadRequest());
    }
}
