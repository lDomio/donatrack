package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.TipoEntidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Ciudad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import ar.edu.utn.frba.dds.donaciones.main.repositories.EntidadBeneficiariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntidadFiltroServiceTest {

    private EntidadBeneficiariaRepository repository;
    private EntidadBeneficiariaImpl service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(EntidadBeneficiariaRepository.class);
        service = new EntidadBeneficiariaImpl(repository, null, null, null);
        when(repository.buscarConFiltros(
                eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(List.of(
                        entidad("Comedor Los Bajitos", "COMEDOR", "Av. Siempreviva", "Rosario"),
                        entidad("Fundacion Vida", "ONG", "Calle Falsa", "CABA"),
                        entidad("Escuela Esperanza", "EDUCACION", "Mitre", "CABA"),
                        entidadSinDireccion("Centro Salud Norte", "SALUD"),
                        entidad("Refugio Amanecer", "REFUGIO", "Rivadavia", null)));
    }

    private static EntidadBeneficiaria entidad(String razonSocial, String tipo, String calle, String ciudad) {
        EntidadBeneficiaria e = new EntidadBeneficiaria();
        e.setRazonSocial(razonSocial);
        TipoEntidad t = new TipoEntidad();
        t.setTipoEntidad(tipo);
        e.setTipo(t);
        Direccion d = new Direccion();
        d.setCalle(calle);
        if (ciudad != null) {
            Ciudad c = new Ciudad();
            ReflectionTestUtils.setField(c, "Nombre", ciudad);
            d.setCiudad(c);
        }
        e.setDireccion(d);
        return e;
    }

    private static EntidadBeneficiaria entidadSinDireccion(String razonSocial, String tipo) {
        EntidadBeneficiaria e = new EntidadBeneficiaria();
        e.setRazonSocial(razonSocial);
        TipoEntidad t = new TipoEntidad();
        t.setTipoEntidad(tipo);
        e.setTipo(t);
        e.setDireccion(null);
        return e;
    }

    private static List<String> razones(List<EntidadBeneficiariaResponse> r) {
        return r.stream().map(EntidadBeneficiariaResponse::razonSocial).toList();
    }

    @Test
    @DisplayName("Sin params delega todo null y mapea las respuestas")
    void testSinParamsDelegaTodoNull() {
        List<EntidadBeneficiariaResponse> resultado =
                service.buscarConFiltros(null, null, null, null);

        verify(repository).buscarConFiltros(eq(null), eq(null), eq(null), eq(null));
        assertEquals(5, resultado.size());
        assertTrue(razones(resultado).contains("Comedor Los Bajitos"));
    }

    @Test
    @DisplayName("Blank equivale a ausente y recorta espacios")
    void testBlankEsAusente() {
        service.buscarConFiltros("  ", "", " ", "Todas");

        verify(repository).buscarConFiltros(eq(null), eq(null), eq(null), eq(null));
    }

    @Test
    @DisplayName("busqueda ignora los filtros finos para evitar AND contradictorios")
    void testBusquedaIgnoraFinos() {
        when(repository.buscarConFiltros(eq("vida"), eq(null), eq(null), eq(null)))
                .thenReturn(List.of(entidad("Fundacion Vida", "ONG", "Calle Falsa", "CABA")));

        List<EntidadBeneficiariaResponse> resultado =
                service.buscarConFiltros("vida", "xyz-sin-match", "xyz-sin-match", null);

        verify(repository).buscarConFiltros(eq("vida"), eq(null), eq(null), eq(null));
        assertEquals(List.of("Fundacion Vida"), razones(resultado));
    }

    @Test
    @DisplayName("Filtros finos se pasan tal cual cuando no hay busqueda")
    void testFinosSinBusqueda() {
        service.buscarConFiltros(null, "escuela", "caba", "Educación");

        verify(repository).buscarConFiltros(eq(null), eq("escuela"), eq("caba"), eq("EDUCACION"));
    }

    @Test
    @DisplayName("Tipo usa la tabla de equivalencias")
    void testTipoEquivalencias() {
        service.buscarConFiltros(null, null, null, "Comedores");
        service.buscarConFiltros(null, null, null, "REFUGIOS");

        verify(repository).buscarConFiltros(eq(null), eq(null), eq(null), eq("COMEDOR"));
        verify(repository).buscarConFiltros(eq(null), eq(null), eq(null), eq("REFUGIO"));
    }

    @Test
    @DisplayName("Comodines LIKE del usuario se escapan para buscar literal")
    void testEscapeLike() {
        service.buscarConFiltros("100%_x", null, null, null);

        verify(repository).buscarConFiltros(eq("100\\%\\_x"), eq(null), eq(null), eq(null));
    }

    @Test
    @DisplayName("Tipo desconocido lanza BusinessException (400) sin consultar")
    void testTipoDesconocido400() {
        EntidadBeneficiariaRepository repo = Mockito.mock(EntidadBeneficiariaRepository.class);
        EntidadBeneficiariaImpl svc = new EntidadBeneficiariaImpl(repo, null, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> svc.buscarConFiltros(null, null, null, "INEXISTENTE_XYZ"));

        assertTrue(ex.getMessage().contains("INEXISTENTE_XYZ"));
        verify(repo, never()).buscarConFiltros(isNull(), isNull(), isNull(), isNull());
    }
}
