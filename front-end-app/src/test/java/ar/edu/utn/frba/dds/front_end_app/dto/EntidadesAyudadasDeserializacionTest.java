package ar.edu.utn.frba.dds.front_end_app.dto;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntidadesAyudadasDeserializacionTest {

    @Test
    @DisplayName("El JSON del backend mapea al espejo del front")
    void testMapeaJsonBackend() throws Exception {
        String json = "{\"donanteId\":5,\"anio\":2026,\"mes\":10,\"cantidad\":2}";

        EntidadesAyudadasResponse response =
                new ObjectMapper().readValue(json, EntidadesAyudadasResponse.class);

        assertEquals(5L, response.donanteId());
        assertEquals(2026, response.anio());
        assertEquals(10, response.mes());
        assertEquals(2L, response.cantidad());
    }
}
