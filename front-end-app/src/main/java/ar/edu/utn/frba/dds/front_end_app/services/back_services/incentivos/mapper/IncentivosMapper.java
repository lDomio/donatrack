package ar.edu.utn.frba.dds.front_end_app.services.back_services.incentivos.mapper;

import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaDonanteDto;
import ar.edu.utn.frba.dds.front_end_app.dto.CategoriaDonanteResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.EstadoGeneralResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.IncentivosVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.InsigniaResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.InsigniaVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionEstadoResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionResponse;
import ar.edu.utn.frba.dds.front_end_app.dto.MisionVistaDto;
import ar.edu.utn.frba.dds.front_end_app.dto.ProgresoMisionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Combina las misiones completadas e insignias reales del backend con el catalogo
 * ilustrativo de lo que el backend todavia no provee (categorias bloqueadas, etc.).
 *
 * <p>Si {@code estadoGeneral} viene informado, la categoria actual, la siguiente,
 * el progreso y la mision en curso se toman del backend; el resto del catalogo
 * sigue siendo el fallback visual.</p>
 */
public final class IncentivosMapper {

  // Fallback: no hay endpoint de categoria/progreso de categoria en incentivos-service.
  private static final List<CategoriaDonanteDto> CATEGORIAS_FALLBACK = List.of(
      new CategoriaDonanteDto("Colaborador", "fa-seedling", "verde", false, true),
      new CategoriaDonanteDto("Sostenedor", "fa-hand-holding-heart", "teal", false, false),
      new CategoriaDonanteDto("Transformador", "fa-crown", "dorada", false, false));
  private static final String CATEGORIA_ACTUAL_FALLBACK = "Colaborador";
  private static final String CATEGORIA_SIGUIENTE_FALLBACK = "Sostenedor";
  private static final int MISIONES_FALTANTES_FALLBACK = 1;
  private static final List<String> FILTROS_FALLBACK =
      List.of("Todas", "Colaborador", "Sostenedor", "Transformador");

  private static final Map<String, String> NOMBRES_CATEGORIA = Map.of(
      "COLABORADOR_INICIAL", "Colaborador",
      "COLABORADOR_ACTIVO", "Sostenedor",
      "COLABORADOR_LEYENDA", "Transformador");

  // Fallback: no hay catalogo de misiones en progreso/bloqueadas, solo completadas.
  private static final List<MisionVistaDto> MISIONES_EN_PROGRESO_FALLBACK = List.of(
      new MisionVistaDto(2L, "Racha solidaria",
          "Realizá al menos una donación durante 4 meses consecutivos.",
          "Colaborador", null, 2, 4, 50, "EN_PROGRESO", "Donante constante",
          "Si no realizás ninguna donación este mes, perdés el progreso acumulado."),
      new MisionVistaDto(3L, "Completitud",
          "Doná artículos de 4 categorías distintas.",
          "Sostenedor", null, 0, 4, 0, "BLOQUEADA", "Donante versátil", null),
      new MisionVistaDto(4L, "Apoyo extraordinario",
          "Contribuí a más de 40 necesidades marcadas como extraordinarias en distintas entidades.",
          "Sostenedor", null, 0, 40, 0, "BLOQUEADA", "Donador extraordinario", null),
      new MisionVistaDto(5L, "Donaciones exitosas",
          "Lográ que 10 entidades beneficiarias reciban exitosamente el total de sus necesidades.",
          "Transformador", null, 0, 10, 0, "BLOQUEADA", "Saciador de necesidades", null));

  // Fallback completo para cuando incentivos-service no responde (misma mision #1 del mock original).
  private static final MisionVistaDto MISION_COMPLETADA_FALLBACK = new MisionVistaDto(
      1L, "Arreglar escuela primaria",
      "Contribuí con materiales para la refacción del ala norte de la institución.",
      "Colaborador", null, 50, 50, 100, "COMPLETADA", "Donador de muebles", null);

  // Fallback: no hay catalogo de insignias no obtenidas (solo se puede consultar las que el donante ya tiene).
  private static final List<InsigniaVistaDto> INSIGNIAS_BLOQUEADAS_FALLBACK = List.of(
      new InsigniaVistaDto(2L, "Donante constante", "Realizaste donaciones durante 4 meses consecutivos.", "fa-fire", "naranja", false, false),
      new InsigniaVistaDto(3L, "Donante versátil", "Donaste artículos de 4 categorías distintas.", "fa-layer-group", "azul", false, false),
      new InsigniaVistaDto(4L, "Donador extraordinario", "Contribuiste a más de 40 necesidades extraordinarias.", "fa-star", "teal", false, false),
      new InsigniaVistaDto(5L, "Saciador de necesidades", "Tus donaciones saciaron completamente las necesidades de más de 10 entidades beneficiarias.", "fa-trophy", "dorada", false, false));

  // Fallback completo para cuando incentivos-service no responde (misma insignia #1 del mock original).
  private static final InsigniaVistaDto INSIGNIA_OBTENIDA_FALLBACK = new InsigniaVistaDto(
      1L, "Donador de muebles", "Donaste más de 50 muebles.", "fa-couch", "dorada", true, true);

  private IncentivosMapper() {
  }

  /**
   * Combina misiones completadas e insignias reales del backend con el contenido
   * ilustrativo para lo que el backend todavia no provee (categorias, progreso, catalogo).
   */
  public static IncentivosVistaDto combinar(List<MisionResponse> misionesCompletadas, List<InsigniaResponse> insigniasObtenidas) {
    return combinar(misionesCompletadas, insigniasObtenidas, null);
  }

  /**
   * Igual que {@link #combinar(List, List)}, pero usa el estado real del donante
   * (categoria, mision actual y progreso) cuando esta disponible.
   */
  public static IncentivosVistaDto combinar(
      List<MisionResponse> misionesCompletadas,
      List<InsigniaResponse> insigniasObtenidas,
      EstadoGeneralResponse estadoGeneral) {

    List<MisionVistaDto> misionesReales = aMisionesVista(misionesCompletadas);
    List<InsigniaVistaDto> insigniasReales = aInsigniasVista(insigniasObtenidas);

    String categoriaActual = estadoGeneral != null
        ? nombreVisible(estadoGeneral.categoriaActual())
        : CATEGORIA_ACTUAL_FALLBACK;
    String categoriaSiguiente = estadoGeneral != null
        ? nombreVisible(estadoGeneral.categoriaSiguiente())
        : CATEGORIA_SIGUIENTE_FALLBACK;
    List<CategoriaDonanteDto> categorias = estadoGeneral != null
        ? categoriasConEstado(categoriaActual)
        : CATEGORIAS_FALLBACK;
    int misionesFaltantes = estadoGeneral != null
        ? misionesFaltantes(estadoGeneral)
        : MISIONES_FALTANTES_FALLBACK;

    List<MisionVistaDto> misiones = new ArrayList<>(misionesReales);
    if (estadoGeneral != null && estadoGeneral.misionActual() != null) {
      misiones.add(aMisionActual(estadoGeneral, categoriaActual));
    }
    List<MisionVistaDto> fallback = estadoGeneral != null
        ? MISIONES_EN_PROGRESO_FALLBACK.subList(1, MISIONES_EN_PROGRESO_FALLBACK.size())
        : MISIONES_EN_PROGRESO_FALLBACK;
    misiones.addAll(fallback);

    List<InsigniaVistaDto> insignias = Stream.concat(insigniasReales.stream(), INSIGNIAS_BLOQUEADAS_FALLBACK.stream())
        .collect(Collectors.toList());
    List<String> filtros = estadoGeneral != null
        ? Stream.concat(Stream.of("Todas"), categorias.stream().map(CategoriaDonanteDto::nombre))
            .distinct()
            .collect(Collectors.toList())
        : FILTROS_FALLBACK;

    return new IncentivosVistaDto(
        categorias, categoriaActual, categoriaSiguiente, misionesFaltantes,
        filtros, misiones, insignias,
        insigniasReales.size(), insigniasReales.size() + INSIGNIAS_BLOQUEADAS_FALLBACK.size());
  }

  /** Contenido 100% ilustrativo para cuando incentivos-service no responde (sin error de template). */
  public static IncentivosVistaDto soloFallback() {
    List<MisionVistaDto> misiones = Stream.concat(Stream.of(MISION_COMPLETADA_FALLBACK), MISIONES_EN_PROGRESO_FALLBACK.stream())
        .collect(Collectors.toList());
    List<InsigniaVistaDto> insignias = Stream.concat(Stream.of(INSIGNIA_OBTENIDA_FALLBACK), INSIGNIAS_BLOQUEADAS_FALLBACK.stream())
        .collect(Collectors.toList());

    return new IncentivosVistaDto(
        CATEGORIAS_FALLBACK, CATEGORIA_ACTUAL_FALLBACK, CATEGORIA_SIGUIENTE_FALLBACK, MISIONES_FALTANTES_FALLBACK,
        FILTROS_FALLBACK, misiones, insignias, 1, insignias.size());
  }

  private static String nombreVisible(CategoriaDonanteResponse categoria) {
    if (categoria == null || categoria.nombre() == null || categoria.nombre().isBlank()) {
      return null;
    }
    return NOMBRES_CATEGORIA.getOrDefault(categoria.nombre(), categoria.nombre());
  }

  private static List<CategoriaDonanteDto> categoriasConEstado(String categoriaActual) {
    if (categoriaActual == null) {
      return CATEGORIAS_FALLBACK;
    }

    int indiceActual = -1;
    for (int i = 0; i < CATEGORIAS_FALLBACK.size(); i++) {
      if (CATEGORIAS_FALLBACK.get(i).nombre().equals(categoriaActual)) {
        indiceActual = i;
        break;
      }
    }

    if (indiceActual < 0) {
      List<CategoriaDonanteDto> resultado = new ArrayList<>();
      resultado.add(new CategoriaDonanteDto(categoriaActual, "fa-award", "teal", false, true));
      for (CategoriaDonanteDto categoria : CATEGORIAS_FALLBACK) {
        if (!categoria.nombre().equals(categoriaActual)) {
          resultado.add(new CategoriaDonanteDto(categoria.nombre(), categoria.icono(), categoria.colorClase(), false, false));
        }
      }
      return resultado;
    }

    List<CategoriaDonanteDto> resultado = new ArrayList<>();
    for (int i = 0; i < CATEGORIAS_FALLBACK.size(); i++) {
      CategoriaDonanteDto categoria = CATEGORIAS_FALLBACK.get(i);
      boolean esActual = i == indiceActual;
      boolean alcanzada = i < indiceActual;
      resultado.add(new CategoriaDonanteDto(
          categoria.nombre(), categoria.icono(), categoria.colorClase(), alcanzada, esActual));
    }
    return resultado;
  }

  private static int misionesFaltantes(EstadoGeneralResponse estadoGeneral) {
    int requeridas = estadoGeneral.misionesRequeridasCategoria() != null
        ? estadoGeneral.misionesRequeridasCategoria() : 0;
    int completadas = estadoGeneral.misionesCompletadasCategoria() != null
        ? estadoGeneral.misionesCompletadasCategoria() : 0;
    return Math.max(0, requeridas - completadas);
  }

  private static MisionVistaDto aMisionActual(EstadoGeneralResponse estadoGeneral, String categoriaActual) {
    MisionEstadoResponse mision = estadoGeneral.misionActual();
    ProgresoMisionResponse progreso = estadoGeneral.progresoMisionActual();

    int porcentaje = progreso != null && progreso.progreso() != null
        ? clamp((int) Math.round(progreso.progreso())) : 0;
    int cantidadActual = progreso != null && progreso.cantidadActual() != null
        ? (int) Math.round(progreso.cantidadActual()) : 0;
    int cantidadObjetivo = progreso != null && progreso.cantidadObjetivo() != null
        ? (int) Math.round(progreso.cantidadObjetivo()) : 0;
    String insignia = mision.insignia() != null ? mision.insignia().descripcion() : null;
    String estado = porcentaje >= 100 ? "COMPLETADA" : "EN_PROGRESO";

    return new MisionVistaDto(
        null,
        mision.nombre(),
        "Completá esta misión para obtener la insignia.",
        categoriaActual,
        null,
        cantidadActual,
        cantidadObjetivo,
        porcentaje,
        estado,
        insignia,
        null);
  }

  private static int clamp(int valor) {
    return Math.max(0, Math.min(100, valor));
  }

  private static List<MisionVistaDto> aMisionesVista(List<MisionResponse> responses) {
    return responses.stream().map(IncentivosMapper::aMisionVista).collect(Collectors.toList());
  }

  private static MisionVistaDto aMisionVista(MisionResponse response) {
    InsigniaResponse insignia = response.insignia();
    String insigniaOtorgada = insignia != null && insignia.descripcion() != null ? insignia.descripcion() : null;
    String descripcion = response.fecha() != null ? "Completada el " + response.fecha() : "Mision completada";

    // El backend no da titulo/categoria/cantidad propios de la mision: solo que ya esta completada.
    return new MisionVistaDto(
        null, "Mision completada", descripcion, "", null,
        1, 1, 100, "COMPLETADA", insigniaOtorgada, null);
  }

  private static List<InsigniaVistaDto> aInsigniasVista(List<InsigniaResponse> responses) {
    return responses.stream().map(IncentivosMapper::aInsigniaVista).collect(Collectors.toList());
  }

  private static InsigniaVistaDto aInsigniaVista(InsigniaResponse response) {
    String texto = response.descripcion() != null ? response.descripcion() : "Insignia obtenida";
    // El backend no da nombre/icono/color propios: solo la descripcion y que el donante ya la obtuvo.
    return new InsigniaVistaDto(null, texto, "", "fa-award", "dorada", true, true);
  }

}
