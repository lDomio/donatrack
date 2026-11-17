package ar.edu.utn.frba.dds.logistica.main.infrastructure.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.PollingRoutingResult;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Delivery;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.RequestApiRutas;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.TimeWindow;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimizadorRutasRestAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private OptimizadorRutasRestAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OptimizadorRutasRestAdapter(restTemplate, "http://api.rutas.test", "secret-key-123");
    }

    @Test
    void solicitarPlanificacion_EnviaPeticionPostConHeadersYApiKey() {
        // Arrange
        RequestApiRutas request = new RequestApiRutas(
                "req-1",
                new TimeWindow("2026-06-20T08:00:00Z", "2026-06-20T18:00:00Z"),
                new Warehouse(-34.0, -58.0, "Av. Deposito 123"),
                List.of(new Delivery("101", -34.1, -58.1, "Calle 1", 10.0, 2.0)),
                Collections.emptyList()
        );

        when(restTemplate.exchange(
                eq("http://api.rutas.test/v1/plan-route"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        // Act
        adapter.solicitarPlanificacion(request);

        // Assert
        ArgumentCaptor<HttpEntity<RequestApiRutas>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).exchange(
                eq("http://api.rutas.test/v1/plan-route"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(Void.class)
        );

        HttpEntity<RequestApiRutas> entityEnviada = captor.getValue();
        assertEquals(request, entityEnviada.getBody());
        assertEquals("Bearer secret-key-123", entityEnviada.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(MediaType.APPLICATION_JSON, entityEnviada.getHeaders().getContentType());
    }

    @Test
    void consultarEstado_EnviaPeticionGetYRetornaResultado() {
        // Arrange
        String requestId = "req-123";
        PollingRoutingResult mockResult = new PollingRoutingResult(
                requestId, "COMPLETED", null, null, null, null
        );

        when(restTemplate.exchange(
                eq("http://api.rutas.test/v1/plan-route/" + requestId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(PollingRoutingResult.class)
        )).thenReturn(ResponseEntity.ok(mockResult));

        // Act
        PollingRoutingResult result = adapter.consultarEstado(requestId);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.status());
        assertEquals(requestId, result.request_id());
    }

    @Test
    void comunicarseConApi_ReintentaAnteError5xx() {
        // Arrange
        RequestApiRutas request = new RequestApiRutas(
                "req-1", null, null, Collections.emptyList(), Collections.emptyList()
        );

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        adapter.solicitarPlanificacion(request);

        // Assert: Intentó 3 veces (2 errores 5xx + 1 éxito)
        verify(restTemplate, times(3)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void comunicarseConApi_NoReintentaAnteError4xx() {
        // Arrange
        RequestApiRutas request = new RequestApiRutas(
                "req-1", null, null, Collections.emptyList(), Collections.emptyList()
        );

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> adapter.solicitarPlanificacion(request));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class));
    }
}