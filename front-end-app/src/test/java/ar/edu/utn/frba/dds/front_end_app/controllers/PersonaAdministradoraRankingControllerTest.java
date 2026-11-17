package ar.edu.utn.frba.dds.front_end_app.controllers;

import ar.edu.utn.frba.dds.front_end_app.dto.ranking.RankingResponseDto;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.auth.AuthApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonacionApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonanteApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.EntidadBeneficiariaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.IncentivosApiService;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.logistica.LogisticaApiService;
import ar.edu.utn.frba.dds.front_end_app.services.internal.AlmacenamientoFotosService;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PersonaAdministradoraRankingControllerTest {

  private IncentivosApiService incentivosApiService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    DonanteApiService donanteApiService = Mockito.mock(DonanteApiService.class);
    LogisticaApiService logisticaApiService = Mockito.mock(LogisticaApiService.class);
    incentivosApiService = Mockito.mock(IncentivosApiService.class);
    EntidadBeneficiariaApiService entidadBeneficiariaApiService = Mockito.mock(EntidadBeneficiariaApiService.class);
    AuthApiService authApiService = Mockito.mock(AuthApiService.class);
    DonacionApiService donacionApiService = Mockito.mock(DonacionApiService.class);
    AlmacenamientoFotosService almacenamientoFotosService = Mockito.mock(AlmacenamientoFotosService.class);

    PersonaAdministradoraController controller = new PersonaAdministradoraController(
        donanteApiService,
        logisticaApiService,
        incentivosApiService,
        entidadBeneficiariaApiService,
        authApiService,
        donacionApiService,
        almacenamientoFotosService
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Generar ranking redirige al período con mensaje de éxito")
  void generarRankingRedirigeConExito() throws Exception {
    RankingResponseDto response = new RankingResponseDto(
        1L, 10, 2026, LocalDate.of(2026, 10, 1), List.of(), 0);
    when(incentivosApiService.generarRanking(10, 2026)).thenReturn(response);

    mockMvc.perform(post("/personaAdministradora/ranking/generar")
            .param("periodo", "2026-10"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/personaAdministradora/ranking?periodo=2026-10"))
        .andExpect(flash().attribute("exito", "Ranking generado correctamente para 2026-10."));

    verify(incentivosApiService).generarRanking(10, 2026);
  }

  @Test
  @DisplayName("Un período inválido redirige sin llamar al servicio")
  void periodoInvalidoRedirigeConError() throws Exception {
    mockMvc.perform(post("/personaAdministradora/ranking/generar")
            .param("periodo", "2026-13"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/personaAdministradora/ranking"))
        .andExpect(flash().attribute("error", "El período seleccionado no es válido."));

    verifyNoInteractions(incentivosApiService);
  }

  @Test
  @DisplayName("El endpoint de generación exige permiso VER_RANKING")
  void tienePreAuthorizeDeRanking() throws NoSuchMethodException {
    Method method = PersonaAdministradoraController.class.getMethod(
        "generarRanking", String.class, RedirectAttributes.class);
    PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

    assertNotNull(preAuthorize);
    assertTrue(preAuthorize.value().contains("VER_RANKING"));
  }
}
