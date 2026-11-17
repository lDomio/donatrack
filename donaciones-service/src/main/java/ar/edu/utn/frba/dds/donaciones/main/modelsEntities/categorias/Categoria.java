package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bloque 2 del DER - catálogo de bienes.
 * categoria 1--N subcategoria.
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Getter(lombok.AccessLevel.NONE)
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subcategoria> subcategorias = new ArrayList<>();

    public Categoria(String nombre, List<Subcategoria> subcategorias) {
        this.nombre = nombre;
        this.subcategorias = subcategorias;
    }


    public void agregarSubcategoria(Subcategoria subcategoria) {
        if (this.subcategorias == null) {
            this.subcategorias = new ArrayList<>();
        }
        this.subcategorias.add(subcategoria);
        if (subcategoria != null) {
            subcategoria.setCategoria(this);
        }
    }

    public List<Subcategoria> getSubcategorias() {
        if (this.subcategorias == null) {
            this.subcategorias = new ArrayList<>();
        }
        return this.subcategorias;
    }
}
