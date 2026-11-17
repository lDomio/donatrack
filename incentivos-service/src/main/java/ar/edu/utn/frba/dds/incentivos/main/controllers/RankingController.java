package ar.edu.utn.frba.dds.incentivos.main.controllers;

import ar.edu.utn.frba.dds.incentivos.main.dtos.GenerarRankingRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.RankingResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.RankingDonantes;
import ar.edu.utn.frba.dds.incentivos.main.services.RankingService;
import java.time.YearMonth;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping({"/ranking", "/incentivos/ranking"})
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('VER_RANKING')")
    public RankingResponse obtenerRanking(
            @RequestParam(name = "mes", required = false) Integer mes,
            @RequestParam(name = "anio", required = false) Integer anio) {
        log.info("[Incentivos-Controller] GET /ranking recibido: mes={}, anio={}", mes, anio);
        RankingDonantes ranking = rankingService.obtenerRankingPorPeriodo(mes, anio);
        RankingResponse response;
        if (ranking == null) {
            log.info("[Incentivos-Controller] No se encontro ranking para periodo: mes={}, anio={}", mes, anio);
            response = new RankingResponse(null, mes, anio, null, List.of(), 0);
        } else {
            response = RankingResponse.from(ranking);
        }
        log.info("[Incentivos-Controller] GET /ranking procesado: {} donantes encontrados para periodo={}-{}",
                response.totalDonantes(), response.anio(), response.mes());
        return response;
    }

    @PostMapping("/generar")
    @PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('VER_RANKING')")
    public RankingResponse generarRanking(@RequestBody GenerarRankingRequest request) {
        log.info("[Incentivos-Controller] POST /ranking/generar recibido: mes={}, anio={}",
                request.mes(), request.anio());
        YearMonth periodo = YearMonth.of(request.anio(), request.mes());
        RankingDonantes ranking = rankingService.generarRanking(periodo);
        RankingResponse response = RankingResponse.from(ranking);
        log.info("[Incentivos-Controller] POST /ranking/generar procesado: {} donantes para periodo={}-{}",
                response.totalDonantes(), response.anio(), response.mes());
        return response;
    }
}
