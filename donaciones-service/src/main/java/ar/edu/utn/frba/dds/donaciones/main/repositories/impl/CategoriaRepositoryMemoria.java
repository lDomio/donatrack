package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.CategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaRepositoryMemoria {
    private final List<Categoria> categorias = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    //@Override
    public List<Categoria> findAll() {
        return new ArrayList<>(categorias);
    }

    //@Override
    public Optional<Categoria> findById(Long id) {
        if (id == null) return Optional.empty();
        return categorias.stream().filter(c -> id.equals(c.getId())).findFirst();
    }

    //@Override
    public Optional<Categoria> findByNombre(String nombre) {
        if (nombre == null) return Optional.empty();
        return categorias.stream().filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre)).findFirst();
    }

    //@Override
    public Long guardar(Categoria categoria) {
        if (categoria.getId() == null) {
            categoria.setId(generadorId.siguiente());
            categorias.add(categoria);
        } else {
            this.findById(categoria.getId()).ifPresent(categorias::remove);
            categorias.add(categoria);
        }
        return categoria.getId();
    }

    //@Override
    public void eliminar(Long id) {
        if (id != null) {
            categorias.removeIf(c -> id.equals(c.getId()));
        }
    }
}
