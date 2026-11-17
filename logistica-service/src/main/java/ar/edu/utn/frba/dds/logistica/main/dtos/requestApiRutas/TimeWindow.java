package ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimeWindow(
        String start,
        String end
) {
}

