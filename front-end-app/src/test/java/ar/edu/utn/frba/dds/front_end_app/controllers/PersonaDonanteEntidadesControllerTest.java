package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.EntidadesAyudadasResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EntidadVistaDto;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.services.IdentidadService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonacionApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.EntidadBeneficiariaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.IncentivosApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class PersonaDonanteEntidadesControllerTest {

    private IdentidadService identidadService;
    private DonacionApiService donacionApiService;
    private EntidadBeneficiariaApiService entidadApiService;
    private MockMvc mockMvc;

    private static EntidadVistaDto vista(Long id, String razonSocial) {
        return new EntidadVistaDto(id, razonSocial, "ONG", null, "Descripcion pendiente",
                null, List.of(), 0, "", 0, "fa-hand-holding-heart", List.of());
    }

    @BeforeEach
    void setUp() {
        identidadService = Mockito.mock(IdentidadService.class);
        donacionApiService = Mockito.mock(DonacionApiService.class);
        IncentivosApiService incentivosApiService = Mockito.mock(IncentivosApiService.class);
        entidadApiService = Mockito.mock(EntidadBeneficiariaApiService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PersonaDonanteController(
                        identidadService, donacionApiService, incentivosApiService, entidadApiService))
                .setViewResolvers((viewName, locale) -> (model, request, response) -> {
                })
                .build();
    }

    @Test
    @DisplayName("Logueado con filtros: reenvia params, muestra conteo y preserva form")
    void testLogueadoConFiltros() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(identidadService.esInvitado(session)).thenReturn(false);
        when(identidadService.donanteIdActual(session)).thenReturn(5L);
        when(entidadApiService.obtenerConFiltros(eq("vida"), eq("Salud")))
                .thenReturn(List.of(vista(1L, "Fundacion Vida")));
        when(donacionApiService.contarEntidadesAyudadas(5L))
                .thenReturn(new EntidadesAyudadasResponse(5L, 2026, 10, 2L));

        mockMvc.perform(get("/personaDonante/entidadesBeneficiarias")
                        .session(session)
                        .param("busqueda", "vida")
                        .param("tipo", "Salud"))
                .andExpect(status().isOk())
                .andExpect(view().name("personaDonante/entidadesBeneficiarias"))
                .andExpect(model().attribute("busquedaActiva", "vida"))
                .andExpect(model().attribute("tipoEntidadActivo", "Salud"))
                .andExpect(model().attribute("cantidadEntidadesAyudadas", 2L))
                .andExpect(model().attribute("mostrarConteo", true))
                .andExpect(model().attribute("errorConteo", false))
                .andExpect(model().attribute("errorCarga", false));

        verify(entidadApiService).obtenerConFiltros(eq("vida"), eq("Salud"));
        verify(donacionApiService).contarEntidadesAyudadas(5L);
    }

    @Test
    @DisplayName("Sin filtros: reenvia nulls y tipo activo es Todas")
    void testSinFiltros() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(identidadService.esInvitado(session)).thenReturn(false);
        when(identidadService.donanteIdActual(session)).thenReturn(5L);
        when(entidadApiService.obtenerConFiltros(eq(null), eq(null))).thenReturn(List.of());
        when(donacionApiService.contarEntidadesAyudadas(5L))
                .thenReturn(new EntidadesAyudadasResponse(5L, 2026, 10, 0L));

        mockMvc.perform(get("/personaDonante/entidadesBeneficiarias").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("busquedaActiva", ""))
                .andExpect(model().attribute("tipoEntidadActivo", "Todas"))
                .andExpect(model().attribute("cantidadEntidadesAyudadas", 0L))
                .andExpect(model().attribute("mostrarConteo", true));

        verify(entidadApiService).obtenerConFiltros(eq(null), eq(null));
    }

    @Test
    @DisplayName("Invitado: sin tarjeta de conteo y sin llamar al conteo")
    void testInvitadoSinTarjeta() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(identidadService.esInvitado(session)).thenReturn(true);
        when(identidadService.donanteIdActual(session)).thenReturn(null);
        when(entidadApiService.obtenerConFiltros(eq(null), eq(null)))
                .thenReturn(List.of(vista(1L, "Comedor")));

        mockMvc.perform(get("/personaDonante/entidadesBeneficiarias").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("mostrarConteo", false))
                .andExpect(model().attribute("errorConteo", false));

        verify(donacionApiService, never()).contarEntidadesAyudadas(any());
    }

    @Test
    @DisplayName("Backend caido: lista vacia con reintento y pagina sin cifra")
    void testBackendCaido() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(identidadService.esInvitado(session)).thenReturn(false);
        when(identidadService.donanteIdActual(session)).thenReturn(5L);
        when(entidadApiService.obtenerConFiltros(any(), any()))
                .thenThrow(new BackendNoDisponibleException("donaciones-service", new RuntimeException("down")));
        when(donacionApiService.contarEntidadesAyudadas(5L))
                .thenThrow(new BackendNoDisponibleException("donaciones-service", new RuntimeException("down")));

        mockMvc.perform(get("/personaDonante/entidadesBeneficiarias").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("personaDonante/entidadesBeneficiarias"))
                .andExpect(model().attribute("errorCarga", true))
                .andExpect(model().attribute("errorConteo", true))
                .andExpect(model().attribute("mostrarConteo", false));
    }
}
