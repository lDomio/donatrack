package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.dtos.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MetricasUsuarioResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface IncentivoService {
    MetricasUsuarioResponse getMetricasUsuario(Long id, YearMonth fechaLimite, YearMonth mesInicio, YearMonth mesFin);
    EstadoGeneralResponse getEstadoGeneral(Long id);
    void reiniciarMisionesMensuales();
    List<MisionCompletada> getMisionesCompletadasPorDonante(Long id);
    List<Insignia> getInsigniasDonante(Long id);
    Map<String, Number> donacionesPorPeriodo(Donante donante, YearMonth fechaLimite);
    Map<String, Number> compararMeses(Donante donante, YearMonth mesInicio, YearMonth mesFin);
}
