#!/usr/bin/env bash
#
# Renderiza diagramas PlantUML de los cambios OpenSpec usando Docker.
#
# Uso:
#   ./scripts/plantuml-render.sh openspec/changes/<cambio>/design.md
#   ./scripts/plantuml-render.sh openspec/changes/<cambio>/diagrams/mi-diagrama.puml
#   ./scripts/plantuml-render.sh openspec/changes/<cambio>/diagrams
#   ./scripts/plantuml-render.sh <objetivo> --open
#
# Para un .md:
#   - Extrae los bloques ```plantuml legacy a diagrams/<nombre>.puml.
#   - Reemplaza cada bloque por [![<nombre>](diagrams/<nombre>.png)](diagrams/<nombre>.png).
#   - Renderiza todos los .puml de diagrams/.
#
# Requiere Docker. Nunca usa Java del host.

set -euo pipefail

PLANTUML_IMAGE="${PLANTUML_IMAGE:-plantuml/plantuml:latest}"
PLANTUML_LIMIT_SIZE="${PLANTUML_LIMIT_SIZE:-16384}"

usage() {
  cat <<'EOF'
Uso: ./scripts/plantuml-render.sh <design.md | diagrama.puml | directorio> [--open]

Opciones:
  --open    Abre los PNG generados con xdg-open (si está disponible).
  -h, --help

Variables de entorno:
  PLANTUML_IMAGE         Imagen Docker (default: plantuml/plantuml:latest).
  PLANTUML_LIMIT_SIZE    Límite de tamaño de imagen (default: 16384).
EOF
}

die() {
  echo "Error: $*" >&2
  exit 1
}

TARGET=""
OPEN=0
for arg in "$@"; do
  case "$arg" in
    --open) OPEN=1 ;;
    -h|--help) usage; exit 0 ;;
    *)
      if [[ -n "$TARGET" ]]; then
        usage >&2
        die "solo se acepta un objetivo"
      fi
      TARGET="$arg"
      ;;
  esac
done

[[ -n "$TARGET" ]] || { usage >&2; exit 1; }
[[ -e "$TARGET" ]] || die "no existe: $TARGET"
command -v docker >/dev/null 2>&1 || die "Docker no está instalado o no está en el PATH"

PNGS=()

render_puml_files() {
  local diagrams_dir="$1"
  local -a files=()

  if [[ ! -d "$diagrams_dir" ]]; then
    echo "Sin diagramas en $diagrams_dir"
    return 0
  fi

  if [[ $# -gt 1 ]]; then
    files=("$2")
  else
    while IFS= read -r -d '' file; do
      files+=("$file")
    done < <(find "$diagrams_dir" -maxdepth 1 -type f -name '*.puml' -print0 | sort -z)
  fi

  if [[ ${#files[@]} -eq 0 ]]; then
    echo "Sin archivos .puml en $diagrams_dir"
    return 0
  fi

  local -a names=()
  local file
  for file in "${files[@]}"; do
    names+=("$(basename "$file")")
  done

  echo "Renderizando ${#names[@]} diagrama(s) en $diagrams_dir"
  # La imagen oficial no usa JAVA_OPTS: se sobreescribe el entrypoint para
  # pasar el límite como propiedad de la JVM (siempre dentro del contenedor).
  docker run --rm \
    -v "$diagrams_dir:/data" \
    -w /data \
    --entrypoint java \
    "$PLANTUML_IMAGE" \
    "-DPLANTUML_LIMIT_SIZE=$PLANTUML_LIMIT_SIZE" \
    -jar /opt/plantuml.jar \
    -tpng "${names[@]}"

  local png
  for file in "${files[@]}"; do
    png="${file%.puml}.png"
    if [[ -f "$png" ]]; then
      PNGS+=("$png")
    else
      echo "Aviso: no se generó $png" >&2
    fi
  done
}

extract_legacy_blocks() {
  local md_file="$1"
  local md_dir
  md_dir="$(cd "$(dirname "$md_file")" && pwd)"
  local diagrams_dir="$md_dir/diagrams"

  command -v python3 >/dev/null 2>&1 || die "python3 es necesario para migrar bloques plantuml embebidos"

  python3 - "$md_file" "$diagrams_dir" <<'PY'
import re
import sys
from pathlib import Path

md_path = Path(sys.argv[1])
diagrams_dir = Path(sys.argv[2])
text = md_path.read_text(encoding="utf-8")

pattern = re.compile(
    r"^(?P<indent>[ \t]*)```plantuml[ \t]*\r?\n(?P<body>.*?)^[ \t]*```[ \t]*$",
    re.MULTILINE | re.DOTALL,
)

state = {"count": 0}


def safe_name(raw: str) -> str:
    name = re.sub(r"[^A-Za-z0-9._-]", "-", raw.strip())
    return name or "diagrama"


def replace(match: re.Match) -> str:
    state["count"] += 1
    index = state["count"]
    body = match.group("body").replace("\r\n", "\n")

    start = re.search(r"@startuml[ \t]+([^\s]+)", body)
    name = safe_name(start.group(1)) if start else f"{md_path.stem}-{index}"

    if "@startuml" not in body:
        body = f"@startuml {name}\n{body}"
    if "@enduml" not in body:
        body = body.rstrip() + "\n@enduml"
    body = body.rstrip() + "\n"

    target = diagrams_dir / f"{name}.puml"
    target.parent.mkdir(parents=True, exist_ok=True)
    if not target.exists() or target.read_text(encoding="utf-8") != body:
        target.write_text(body, encoding="utf-8")
        print(f"Extraído: {target}")
    else:
        print(f"Sin cambios: {target}")

    indent = match.group("indent")
    return f"{indent}[![{name}](diagrams/{name}.png)](diagrams/{name}.png)"


new_text, replacements = pattern.subn(replace, text)
if replacements:
    md_path.write_text(new_text, encoding="utf-8")
    print(f"Migrados {replacements} bloque(s) en {md_path}")
else:
    print(f"Sin bloques ```plantuml en {md_path}")
PY
}

TARGET_ABS="$(cd "$(dirname "$TARGET")" && pwd)/$(basename "$TARGET")"

if [[ -d "$TARGET_ABS" ]]; then
  render_puml_files "$TARGET_ABS"
elif [[ "$TARGET_ABS" == *.md ]]; then
  extract_legacy_blocks "$TARGET_ABS"
  render_puml_files "$(dirname "$TARGET_ABS")/diagrams"
elif [[ "$TARGET_ABS" == *.puml ]]; then
  render_puml_files "$(dirname "$TARGET_ABS")" "$TARGET_ABS"
else
  die "objetivo no soportado (se esperaba .md, .puml o un directorio): $TARGET"
fi

if [[ $OPEN -eq 1 && ${#PNGS[@]} -gt 0 ]]; then
  if command -v xdg-open >/dev/null 2>&1; then
    for png in "${PNGS[@]}"; do
      xdg-open "$png" >/dev/null 2>&1 &
    done
  else
    echo "Aviso: xdg-open no está disponible; los PNG quedaron en disco."
  fi
fi

echo "Listo. ${#PNGS[@]} PNG generado(s)."
