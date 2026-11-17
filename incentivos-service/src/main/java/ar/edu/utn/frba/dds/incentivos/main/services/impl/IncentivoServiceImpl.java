package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.incentivos.main.dtos.CategoriaDonanteDto;
import ar.edu.utn.frba.dds.incentivos.main.dtos.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MisionDto;
import ar.edu.utn.frba.dds.incentivos.main.dtos.ProgresoMisionDto;
import ar.edu.utn.frba.dds.incentivos.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MetricasUsuarioResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionRacha;
import ar.edu.utn.frba.dds.incentivos.main.services.DonacionService;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.IncentivoService;
import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Service
public class IncentivoServiceImpl implements IncentivoService {
    private final DonacionService donacionService;
    private final DonanteService donanteService;
    private final RankingService rankingService;

    public IncentivoServiceImpl(DonacionService donacionService, DonanteService donanteService, RankingService rankingService) {
        this.donacionService = donacionService;
        this.donanteService = donanteService;
        this.rankingService = rankingService;
    }

    @Override
    public MetricasUsuarioResponse getMetricasUsuario(Long id, YearMonth fechaLimite, YearMonth mesInicio, YearMonth mesFin) {
        Donante donante = this.donanteService.findById(id).orElseThrow(()->new BusinessException("No existe ese donante"));

        Map<String, Number> donacionesPorPeriodo = this.donacionesPorPeriodo(donante, fechaLimite);

        Map<String, Number> comparacionDeMeses = mesInicio != null && mesFin != null ? this.compararMeses(donante, mesInicio, mesFin) : null;

        Number numeroTotalDeOrganizacionesQueAyudo = donante.getTotalEntidadesBeneficiariasQueRecibieron();

        Number posicionEnRankingDeDonantes = this.rankingService.buscarPosicionDonanteEnRanking(id);

        return new MetricasUsuarioResponse(donante.getCantidadDonaciones(), posicionEnRankingDeDonantes, donacionesPorPeriodo, comparacionDeMeses, numeroTotalDeOrganizacionesQueAyudo);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoGeneralResponse getEstadoGeneral(Long id) {
        Donante donante = this.donanteService.findById(id)
                .orElseThrow(() -> new BusinessException("No existe ese donante"));

        CategoriaDonante categoriaActual = donante.getCategoria();
        CategoriaDonante categoriaSiguiente = buscarCategoriaSiguiente(donante, categoriaActual);

        Mision misionActual = donante.getMisionActual();
        ProgresoMision progreso = donante.getProgresoMisionActual();
        if (progreso == null && misionActual != null) {
            progreso = misionActual.calcularProgreso(donante);
        }

        int completadas = (int) (donante.getMisionesCompletadas() != null
                ? donante.getMisionesCompletadas().stream()
                    .filter(mc -> mismaCategoria(mc.getCategoria(), categoriaActual))
                    .count()
                : 0);
        int requeridas = categoriaActual != null && categoriaActual.getCantidadDeMisionesACompletar() != null
                ? categoriaActual.getCantidadDeMisionesACompletar()
                : 0;

        return new EstadoGeneralResponse(
                mapCategoria(categoriaActual),
                mapCategoria(categoriaSiguiente),
                mapMision(misionActual),
                mapProgreso(progreso),
                completadas,
                requeridas
        );
    }

    private CategoriaDonante buscarCategoriaSiguiente(Donante donante, CategoriaDonante categoriaActual) {
        Mision mision = donante.getMisionActual();
        while (mision != null) {
            if (mision.getCategoria() != null && !mismaCategoria(mision.getCategoria(), categoriaActual)) {
                return mision.getCategoria();
            }
            mision = mision.getMisionSiguiente();
        }
        return null;
    }

    private boolean mismaCategoria(CategoriaDonante a, CategoriaDonante b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getId() != null && b.getId() != null) {
            return Objects.equals(a.getId(), b.getId());
        }
        return Objects.equals(a.getNombre(), b.getNombre());
    }

    private CategoriaDonanteDto mapCategoria(CategoriaDonante categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDonanteDto(categoria.getNombre(), categoria.getCantidadDeMisionesACompletar());
    }

    private MisionDto mapMision(Mision mision) {
        if (mision == null) {
            return null;
        }
        return new MisionDto(
                mision.getNombre(),
                mision.getInsignia() != null ? InsigniaResponse.from(mision.getInsignia()) : null
        );
    }

    private ProgresoMisionDto mapProgreso(ProgresoMision progreso) {
        if (progreso == null) {
            return null;
        }
        return new ProgresoMisionDto(
                progreso.getProgreso(),
                progreso.getCantidadActual(),
                progreso.getCantidadObjetivo()
        );
    }

    @Override
    @Transactional
    public void reiniciarMisionesMensuales() {
        List<Donante> donantes = this.donanteService.findAll();
        YearMonth mesActual = YearMonth.now().minusMonths(1);
        for (Donante donante : donantes) {
           Mision misionActual = donante.getMisionActual();
           if (misionActual != null && misionActual.esDeRacha()) {
               if (!donante.donoEnMes(mesActual)) {
                   donante.reiniciarMision();
                   this.donanteService.guardarDonante(donante);
               }
           }
        }
    }

    @Override
    public List<MisionCompletada> getMisionesCompletadasPorDonante(Long id) {
        Donante donante = this.donanteService.findById(id).orElseThrow();
        return donante.getMisionesCompletadas();
    }

    @Override
    public List<Insignia> getInsigniasDonante(Long id) {
        return this.donanteService.findById(id).orElseThrow().getInsignias();
    }

    @Override
    public Map<String, Number> donacionesPorPeriodo(Donante donante, YearMonth fechaLimite) {
        if (donante.getDonaciones() == null || donante.getDonaciones().isEmpty()) return Map.of();
        YearMonth inicio = YearMonth.from(donante.obtenerPrimeraDonacion().get().getFechaDeCreacion());
        YearMonth fin = this.menorYearMonth(YearMonth.from(donante.obtenerUltimaDonacion().get().getFechaDeCreacion()), fechaLimite);
        Map<String, Number> resultado = new TreeMap<>();
        YearMonth actual = inicio;
        while(actual.isBefore(fin) || actual.equals(fin)) {
            int count = donante.getDonacionesEnPeriodo(actual);
            resultado.put(actual.toString(), count);
            actual = actual.plusMonths(1);
        }
        return resultado;
    }
    private YearMonth menorYearMonth(YearMonth fecha1, YearMonth fecha2){
        if(fecha1 != null && fecha2 == null){
            return fecha1;
        }
        if(fecha1 == null && fecha2 != null){
            return fecha2;
        }
        if(fecha1 ==null && fecha2 == null){
            return null;
        }
        return fecha1.isAfter(fecha2) ? fecha2 : fecha1;
    }
    @Override
    public Map<String, Number> compararMeses(Donante donante, YearMonth mesInicio, YearMonth mesFin) {
        if (donante.getDonaciones().isEmpty()) return Map.of();
        int donacionesMesInicio = donante.getDonacionesEnPeriodo(mesInicio);
        int donacionesMesFin = donante.getDonacionesEnPeriodo(mesFin);
        Map<String, Number> resultado = new HashMap<>();
        resultado.put(mesInicio.toString(), donacionesMesInicio);
        resultado.put(mesFin.toString(), donacionesMesFin);
        return resultado;
    }
}
