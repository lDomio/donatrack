package ar.edu.utn.frba.dds.logistica.main.dtos.transporte;

import ar.edu.utn.frba.dds.logistica.main.dtos.entrega.EntregaResponse;
import java.time.LocalDateTime;

public record ParadaResponse(
    Long id,
    Integer numeroDeParada,
    LocalDateTime fechaEntrega,
    String nombreEntidad,
    EntregaResponse entrega
) {}
