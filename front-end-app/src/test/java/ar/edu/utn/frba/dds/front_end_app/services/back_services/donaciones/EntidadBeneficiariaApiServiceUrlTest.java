package ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EntidadBeneficiariaApiServiceUrlTest {

    private static final String BASE = "http://base/donaciones-service";

    @Test
    @DisplayName("Sin filtros la URL no lleva query params")
    void testSinFiltros() {
        assertEquals(URI.create("http://base/donaciones-service/entidades-beneficiarias"),
                EntidadBeneficiariaApiService.construirUrl(BASE, null, null));
        assertEquals(URI.create("http://base/donaciones-service/entidades-beneficiarias"),
                EntidadBeneficiariaApiService.construirUrl(BASE, "  ", ""));
    }

    @Test
    @DisplayName("Con filtros la URL lleva busqueda y tipo")
    void testConFiltros() {
        assertEquals(URI.create("http://base/donaciones-service/entidades-beneficiarias"
                        + "?busqueda=vida&tipo=Salud"),
                EntidadBeneficiariaApiService.construirUrl(BASE, "vida", "Salud"));
    }

    @Test
    @DisplayName("Valores con espacios y acentos se encodean una sola vez")
    void testEncoding() {
        URI uri = EntidadBeneficiariaApiService.construirUrl(
                BASE, "comedor los bajitos", "Educación");
        assertEquals(URI.create("http://base/donaciones-service/entidades-beneficiarias"
                        + "?busqueda=comedor%20los%20bajitos&tipo=Educaci%C3%B3n"),
                uri);
        assertFalse(uri.toString().contains("%25"));
    }

    @Test
    @DisplayName("El tipo Educación conserva el acento codificado una sola vez")
    void testTipoConAcento() {
        URI uri = EntidadBeneficiariaApiService.construirUrl(BASE, null, "Educación");
        assertEquals("http://base/donaciones-service/entidades-beneficiarias?tipo=Educaci%C3%B3n",
                uri.toString());
        assertFalse(uri.toString().contains("%25"));
    }

    @Test
    @DisplayName("La búsqueda Colón conserva el acento codificado una sola vez")
    void testBusquedaConAcento() {
        URI uri = EntidadBeneficiariaApiService.construirUrl(BASE, "Colón", null);
        assertEquals("http://base/donaciones-service/entidades-beneficiarias?busqueda=Col%C3%B3n",
                uri.toString());
        assertFalse(uri.toString().contains("%25"));
    }

    @Test
    @DisplayName("Búsqueda y tipo con acentos conviven en la misma URL")
    void testBusquedaYTipoConAcento() {
        URI uri = EntidadBeneficiariaApiService.construirUrl(BASE, "Colón", "Educación");
        assertEquals("http://base/donaciones-service/entidades-beneficiarias"
                        + "?busqueda=Col%C3%B3n&tipo=Educaci%C3%B3n",
                uri.toString());
        assertFalse(uri.toString().contains("%25"));
    }

    @Test
    @DisplayName("Los caracteres especiales de búsqueda se escapan una sola vez")
    void testCaracterEspecial() {
        URI uri = EntidadBeneficiariaApiService.construirUrl(BASE, "A&B", null);
        assertEquals("http://base/donaciones-service/entidades-beneficiarias?busqueda=A%26B",
                uri.toString());
        assertFalse(uri.toString().contains("%2526"));
    }
}
