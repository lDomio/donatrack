package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;

import java.util.List;

public interface AlgoritmoDeSeleccion {
    public List<Necesidad> aplicarCriterioDeSelecion(Donacion donacion, List<Necesidad> necesidades);
}
