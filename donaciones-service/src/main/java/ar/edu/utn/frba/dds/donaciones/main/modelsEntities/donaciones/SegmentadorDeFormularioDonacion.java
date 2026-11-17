package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SegmentadorDeFormularioDonacion {

  public List<Donacion> segmentar(List<Bien> bienesADonar){
    // Agrupo los bienes por subcategoría, estado (nuevo/usado) y fecha de vencimiento.
    Map<String, List<Bien>> bienesAgrupados = bienesADonar.stream()
        .collect(Collectors.groupingBy(bien -> {
            String subcat = (bien.getSubcategoria() != null) ? bien.getSubcategoria().getNombre() : "SIN_SUBCATEGORIA";
            String estado = (bien.getEstado() != null) ? bien.getEstado().name() : "SIN_ESTADO";
            String vencimiento = (bien.getFechaVencimiento() != null) ? bien.getFechaVencimiento().toString() : "SIN_VENCIMIENTO";
            return subcat + "_" + estado + "_" + vencimiento;
        }));

    // Mapeo los grupos de bienes en donaciones independientes
    return bienesAgrupados.values().stream()
        .map(bienes -> {
          Donacion donacion = new Donacion();
          donacion.setFechaDeCreacion(LocalDate.now());
          
          // La subcategoría de la donación es la del primer bien del grupo
          if (bienes.getFirst().getSubcategoria() != null) {
              donacion.setSubcategoria(bienes.getFirst().getSubcategoria());
          }
          
          bienes.forEach(donacion::agregarBien);

          donacion.agregarEstado(new EstadoDeUnaDonacion(
              TipoDeEstado.DEPOSITO,
              LocalDate.now(),
              "Segmentación automática por subcategoría, estado y vencimiento",
              "SEGMENTADOR DE DONACIONES"
          ));
          return donacion;
        }).collect(Collectors.toList());
  }

}
