package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.matcheadorDonaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.AlgoritmoDeSeleccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MatcheadorDonacion {
    private final List<AlgoritmoDeSeleccion> algoritmosDeSeleccion;

    public MatcheadorDonacion(List<AlgoritmoDeSeleccion> estrategiasEvaluacion) {
        this.algoritmosDeSeleccion = estrategiasEvaluacion;
    }
    public MatcheoSugerido seleccionarNecesidad(Donacion donacion, List<Necesidad> necesidades) {

        List<List<Necesidad>> resultadosDeSeleccion = algoritmosDeSeleccion.stream()
                .map(algoritmoDeSeleccion -> algoritmoDeSeleccion.aplicarCriterioDeSelecion(donacion,necesidades))
                .toList();

        List<Necesidad> interseccionDeLosResultados = this.calcularInterseccion(resultadosDeSeleccion);

        List<Necesidad> seleccionFinal;

        if(!interseccionDeLosResultados.isEmpty()){
            seleccionFinal = interseccionDeLosResultados;
        }else{
            seleccionFinal = resultadosDeSeleccion.stream()
                    .flatMap(List::stream)
                    .distinct()
                    .toList();
        }

        return new MatcheoSugerido(donacion, seleccionFinal);
    }
    private List<Necesidad> calcularInterseccion(List<List<Necesidad>> resultadosDeSeleccion) {
        if (resultadosDeSeleccion.isEmpty()) return new ArrayList<>();

        List<Necesidad> interseccion = new ArrayList<>(resultadosDeSeleccion.getFirst());
        for(int i = 1; i < resultadosDeSeleccion.size(); i++){
            interseccion.retainAll(resultadosDeSeleccion.get(i));
        }

        return interseccion;
    }
}
