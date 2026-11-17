package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.AlgoritmoDeSeleccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;

import java.util.Comparator;
import java.util.List;

public class PrioridadSubAtendidos implements AlgoritmoDeSeleccion {
    @Override
    public List<Necesidad> aplicarCriterioDeSelecion(Donacion donacion, List<Necesidad> necesidades) {
        if (donacion.getSubcategoria() == null) return java.util.Collections.emptyList();
        return necesidades.stream().filter(n -> n.getSubcategoria() != null && donacion.getSubcategoria().esIgual(n.getSubcategoria()))
                .sorted(Comparator.comparingDouble(this::calcularPuntaje).reversed())
                .limit(10)
                .toList();
    }

    private Double calcularPuntaje(Necesidad necesidad){
        double factorDeSuevizado = 0.1;
        return 1/(factorDeSuevizado * necesidad.getDonacionesUltimoTrimestreDeEntidad().size());
    }
}
