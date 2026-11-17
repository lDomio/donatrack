package ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos;

import ar.edu.utn.frba.dds.front_end_app.dto.ranking.RankingResponseDto;
import ar.edu.utn.frba.dds.front_end_app.dto.ranking.GenerarRankingRequestDto;
import ar.edu.utn.frba.dds.front_end_app.services.internal.WebApiCallerService;
import ar.edu.utn.frba.dds.front_end_app.dto.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.IncentivosVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.InsigniaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionResponse;
import ar.edu.utn.frba.dds.front_end_app.exceptions.BackendNoDisponibleException;
import ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.mapper.IncentivosMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class IncentivosApiService {

    private static final Logger log = LoggerFactory.getLogger(IncentivosApiService.class);
    private final String incentivosServiceUrl;
    private final WebApiCallerService webApiCaller;

    public IncentivosApiService(
        @Value("${incentivos.service.url:http://localhost:8081}") String incentivosServiceUrl,
        WebApiCallerService webApiCaller) {
        this.incentivosServiceUrl = incentivosServiceUrl;
        this.webApiCaller = webApiCaller;
    }

    public RankingResponseDto obtenerRanking(Integer mes, Integer anio) {
        try {
            log.info("Llamando a incentivos-service para obtener ranking: mes={}, anio={}", mes, anio);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(incentivosServiceUrl + "/ranking");
            if (mes != null) {
                uriBuilder.queryParam("mes", mes);
            }
            if (anio != null) {
                uriBuilder.queryParam("anio", anio);
            }
            String url = uriBuilder.toUriString();
            return webApiCaller.get(url, RankingResponseDto.class);
        } catch (Exception e) {
            log.error("Error al obtener ranking de incentivos-service {}: {}", incentivosServiceUrl, e.getMessage());
            return null;
        }
    }

    public RankingResponseDto generarRanking(Integer mes, Integer anio) {
        try {
            String url = incentivosServiceUrl + "/ranking/generar";
            log.info("Generando ranking en incentivos-service: mes={}, anio={}", mes, anio);
            return webApiCaller.post(
                url,
                new GenerarRankingRequestDto(mes, anio),
                RankingResponseDto.class
            );
        } catch (Exception e) {
            log.error("Error al generar ranking en incentivos-service {}: {}", incentivosServiceUrl, e.getMessage());
            return null;
        }
    }

    /**
     * {@code incentivos-service} solo expone misiones completadas e insignias obtenidas
     * (sin prefijo de path, puerto propio). El resto de la vista (categorias, progreso,
     * catalogo de insignias bloqueadas) lo completa {@link IncentivosMapper} con fallback.
     */
    public IncentivosVistaDto obtenerIncentivos(Long donanteId) {
        try {
            List<MisionResponse> misiones = webApiCaller.getList(
                incentivosServiceUrl + "/incentivos/" + donanteId + "/misiones",
                MisionResponse.class
            );

            List<InsigniaResponse> insignias = webApiCaller.getList(
                incentivosServiceUrl + "/incentivos/" + donanteId + "/insignias",
                InsigniaResponse.class
            );

            EstadoGeneralResponse estadoGeneral = obtenerEstadoGeneral(donanteId);

            return IncentivosMapper.combinar(
                misiones != null ? misiones : List.<MisionResponse>of(),
                insignias != null ? insignias : List.<InsigniaResponse>of(),
                estadoGeneral
            );
        } catch (Exception e) {
            log.error("Error al obtener incentivos de {} para donante {}: {}", incentivosServiceUrl, donanteId, e.getMessage());
            throw new BackendNoDisponibleException("incentivos-service", e);
        }
    }

    public EstadoGeneralResponse obtenerEstadoGeneral(Long donanteId) {
        try {
            String url = incentivosServiceUrl + "/incentivos/" + donanteId + "/estadoGeneral";
            return webApiCaller.get(url, EstadoGeneralResponse.class);
        } catch (Exception e) {
            log.warn("No se pudo obtener el estado general de incentivos para donante {}: {}", donanteId, e.getMessage());
            return null;
        }
    }
}