package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloque 2 del DER - catálogo de bienes.
 * Es el hub del matcheo: su id se referencia desde donacion y necesidad,
 * por eso la FK categoria_id se indexa junto con el nombre (UNIQUE compuesto).
 */
@Entity
@Table(
    name = "subcategoria",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_subcategoria_categoria_nombre",
        columnNames = {"categoria_id", "nombre"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subcategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
    private Categoria categoria;

    @Column(name = "unidad", length = 30)
    private String unidadDeMedida;

    public static String normalizarNombre(String texto) {
        if (texto == null) {
            return null;
        }
        String descompuesto = java.text.Normalizer.normalize(texto.trim(), java.text.Normalizer.Form.NFD);
        return descompuesto.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toUpperCase();
    }

    public void setNombre(String nombre) {
        this.nombre = normalizarNombre(nombre);
    }

    public void setUnidadDeMedida(String unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida != null ? unidadDeMedida.trim().toUpperCase() : null;
    }

    public Subcategoria(String nombre, Categoria categoria, String unidadDeMedida) {
        this.nombre = normalizarNombre(nombre);
        this.categoria = categoria;
        this.unidadDeMedida = unidadDeMedida != null ? unidadDeMedida.trim().toUpperCase() : null;
    }

    public Boolean esIgual(Subcategoria subcategoria){
        if (subcategoria == null || this.nombre == null || subcategoria.getNombre() == null) {
            return false;
        }
        return this.nombre.equalsIgnoreCase(subcategoria.getNombre());
    }
}
