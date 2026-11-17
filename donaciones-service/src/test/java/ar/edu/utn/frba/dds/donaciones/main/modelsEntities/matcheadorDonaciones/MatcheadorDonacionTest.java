package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.AlgoritmoDeSeleccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Matcheador de Donaciones")
public class MatcheadorDonacionTest {

    @Mock private AlgoritmoDeSeleccion algoritmo1;
    @Mock private AlgoritmoDeSeleccion algoritmo2;
    @Mock private Donacion donacionMock;
    @Mock private Necesidad necesidad1;
    @Mock private Necesidad necesidad2;
    @Mock private Necesidad necesidad3;

    private MatcheadorDonacion matcheador;
    private List<Necesidad> todasLasNecesidades;

    @BeforeEach
    void setUp() {
        matcheador = new MatcheadorDonacion(List.of(algoritmo1, algoritmo2));
        todasLasNecesidades = List.of(necesidad1, necesidad2, necesidad3);
    }

    @Nested
    @DisplayName("Cuando los algoritmos tienen resultados en común (Hay Intersección)")
    class ConInterseccion {

        @Test
        @DisplayName("Debe priorizar y devolver únicamente las necesidades coincidentes")
        void devuelveSoloInterseccion() {
            when(algoritmo1.aplicarCriterioDeSelecion(donacionMock, todasLasNecesidades))
                    .thenReturn(List.of(necesidad1, necesidad2));

            when(algoritmo2.aplicarCriterioDeSelecion(donacionMock, todasLasNecesidades))
                    .thenReturn(List.of(necesidad2, necesidad3));

            MatcheoSugerido resultado = matcheador.seleccionarNecesidad(donacionMock, todasLasNecesidades);
            
            List<Necesidad> sugeridas = resultado.getNecesidadesSugeridas();
            assertAll("Verificaciones de intersección",
                    () -> assertEquals(1, sugeridas.size(), "Debería haber solo 1 necesidad en común"),
                    () -> assertTrue(sugeridas.contains(necesidad2), "La necesidad en común debe ser la necesidad2")
            );
        }
    }

    @Nested
    @DisplayName("Cuando los algoritmos difieren totalmente (No Hay Intersección)")
    class SinInterseccion {

        @Test
        @DisplayName("Debe devolver la unión de los resultados sin duplicados")
        void devuelveUnionSinDuplicados() {
            when(algoritmo1.aplicarCriterioDeSelecion(donacionMock, todasLasNecesidades))
                    .thenReturn(List.of(necesidad1));

            when(algoritmo2.aplicarCriterioDeSelecion(donacionMock, todasLasNecesidades))
                    .thenReturn(List.of(necesidad3));

            MatcheoSugerido resultado = matcheador.seleccionarNecesidad(donacionMock, todasLasNecesidades);
            List<Necesidad> sugeridas = resultado.getNecesidadesSugeridas();

            assertAll("Verificaciones de unión",
                    () -> assertEquals(2, sugeridas.size(), "Debería unir los resultados de ambos algoritmos"),
                    () -> assertTrue(sugeridas.containsAll(List.of(necesidad1, necesidad3)), "Debe contener necesidad1 y necesidad3")
            );
        }
    }


}