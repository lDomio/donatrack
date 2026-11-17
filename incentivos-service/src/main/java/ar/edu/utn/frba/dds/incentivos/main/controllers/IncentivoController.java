package ar.edu.utn.frba.dds.incentivos.main.controllers;

import ar.edu.utn.frba.dds.common.security.Usuario;
import ar.edu.utn.frba.dds.incentivos.main.dtos.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.InsigniaResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MetricasUsuarioResponse;
import ar.edu.utn.frba.dds.incentivos.main.dtos.MisionResponse;
import ar.edu.utn.frba.dds.incentivos.main.exceptions.UnauthorizedException;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.IncentivoService;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.Tuple;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/incentivos")
public class IncentivoController {
    private final IncentivoService incentivoService;
    private final DonanteService donanteService;

    public IncentivoController(IncentivoService incentivoService, DonanteService donanteService) {
        this.incentivoService = incentivoService;
        this.donanteService = donanteService;
    }

    @GetMapping("/{id}/metricas")
    public MetricasUsuarioResponse getMetricasDeActividad(
            @PathVariable Long id,
            // Fecha límite para realizar las métricas mensuales
            @RequestParam(name = "fechaLimite", required = false) String fechaLimite,
            // Comparar entre dos meses
            @RequestParam(name = "mesInicio", required = false) String mesInicio,
            @RequestParam(name = "mesFin", required = false) String mesFin
            // Comparar entre dos meses
            ) throws InterruptedException {
        YearMonth fechaParseada = fechaLimite != null ? YearMonth.parse(fechaLimite) : null;
        YearMonth mesInicioParseado = mesInicio != null ? YearMonth.parse(mesInicio) : null;
        YearMonth mesFinParseado = mesFin != null ? YearMonth.parse(mesFin) : null;
        return this.incentivoService.getMetricasUsuario(id, fechaParseada, mesInicioParseado, mesFinParseado);
    }

    @GetMapping("{id}/estadoGeneral")
    @PreAuthorize("hasRole('DONANTE') and hasAuthority('VER_MIS_INCENTIVOS')")
    public EstadoGeneralResponse getEstadoGeneralSistema(@PathVariable Long id, Authentication authentication) {
        if (!donanteService.validarUsuarioDeDonante(id, getIdUsuarioAuth(authentication))) {
            throw new UnauthorizedException("Un donante puede ver solo sus incentivos");
        }
        return incentivoService.getEstadoGeneral(id);
    }

    @GetMapping("/{id}/misiones")
    @PreAuthorize("hasRole('DONANTE') and hasAuthority('VER_MIS_INCENTIVOS')")
    public List<MisionResponse> getMisionesCompletadasParaDonante(@PathVariable Long id, Authentication authentication) {
        if (!donanteService.validarUsuarioDeDonante(id, getIdUsuarioAuth(authentication))){
            throw new UnauthorizedException("Un donante puede ver solo sus incentivos");
        }

        List<MisionCompletada> misiones = this.incentivoService.getMisionesCompletadasPorDonante(id);
        return misiones.stream().map(MisionResponse::from).toList();
    }

    @GetMapping("/{id}/insignias")
    @PreAuthorize("hasRole('DONANTE') and hasAuthority('VER_MIS_INCENTIVOS')")
    public List<InsigniaResponse> getInsigniasDonante(@PathVariable Long id, Authentication authentication) {
        if (!donanteService.validarUsuarioDeDonante(id, getIdUsuarioAuth(authentication))){
            throw new UnauthorizedException("Un donante puede ver solo sus incentivos");
        }
        
        List<Insignia> insignias = this.incentivoService.getInsigniasDonante(id);
        return insignias.stream().map(InsigniaResponse::from).toList();
    }

    private Long getIdUsuarioAuth(Authentication authentication){
        return ((Usuario) authentication.getPrincipal()).idUsuarioAuth();
    }

}
