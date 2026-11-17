package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.algoritmosEvaluacion.AlgoritmoDeSeleccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadEnum;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.List;

public class CompatibilidadSemantica implements AlgoritmoDeSeleccion {
    @Override
    public List<Necesidad> aplicarCriterioDeSelecion(Donacion donacion, List<Necesidad> necesidades) {
        if (donacion.getSubcategoria() == null) return java.util.Collections.emptyList();
        return necesidades.stream().filter(n -> n.getSubcategoria() != null && donacion.getSubcategoria().esIgual(n.getSubcategoria()))
                .sorted(Comparator.comparingDouble((Necesidad n)->this.calcularPuntaje(n, donacion)).reversed())
                .limit(10)
                .toList();

    }

    private Double calcularPuntaje(Necesidad necesidad, Donacion donacion){
        Double pesoTextual = 0.40;
        Double pesoVolumen = 0.40;
        Double pesoTipo = 0.20;

        return pesoTextual * this.calcularSimilitudTextual(necesidad, donacion)
                + pesoVolumen * this.calcularCoberturaVolumen(necesidad)
                + pesoTipo * this.calcularTipoNecesidad(necesidad);
    }
    private Double calcularSimilitudTextual(Necesidad necesidad, Donacion donacion){
        return 1.00 - donacion.getBienes().stream()
                .mapToDouble(b -> (double) new LevenshteinDistance().apply(b.getNombre(),necesidad.getDescripcion())
                        /Math.max(b.getNombre().length(), necesidad.getDescripcion().length()))
                .sum()
                /donacion.getCantidad();

    }

    private Double calcularCoberturaVolumen(Necesidad necesidad){
        double R = necesidad.getCantidadDonada()/necesidad.getCantidad();
        if (R <= 1){
            return R;
        }
        else{
            return 1/R;
        }
    }

    private Double calcularTipoNecesidad(Necesidad necesidad){
        if (necesidad.getTipoNecesidad() == TipoNecesidadEnum.RECURRENTE){
            return 0.5;
        }
        else if (necesidad.getTipoNecesidad() == TipoNecesidadEnum.EXTRAORDINARIA){
            return 1.00;
        }
        return 0.00;
    }

}
