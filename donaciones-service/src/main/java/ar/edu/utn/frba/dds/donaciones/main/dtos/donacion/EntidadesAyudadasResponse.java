package ar.edu.utn.frba.dds.donaciones.main.dtos.donacion;

/**
 * Cantidad de entidades beneficiarias distintas ayudadas por un donante
 * en el mes en curso (semantica "creacion asignada": donaciones con
 * fechaDeCreacion dentro del mes y necesidad asignada).
 *
 * @param donanteId id del donante consultado
 * @param anio anio del mes aplicado por el servidor
 * @param mes mes (1-12) aplicado por el servidor
 * @param cantidad entidades distintas contadas
 */
public record EntidadesAyudadasResponse(
        Long donanteId,
        int anio,
        int mes,
        long cantidad
) {
}
