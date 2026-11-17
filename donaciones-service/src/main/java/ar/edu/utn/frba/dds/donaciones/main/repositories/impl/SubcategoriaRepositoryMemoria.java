package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.SubcategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubcategoriaRepositoryMemoria {
    private final List<Subcategoria> subcategorias = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    //@Override
    public List<Subcategoria> findAll() {
        return new ArrayList<>(subcategorias);
    }

    //@Override
    public Optional<Subcategoria> findById(Long id) {
        if (id == null) return Optional.empty();
        return subcategorias.stream().filter(s -> id.equals(s.getId())).findFirst();
    }

    //@Override
    public Optional<Subcategoria> findByNombre(String nombre) {
        if (nombre == null) return Optional.empty();
        return subcategorias.stream().filter(s -> s.getNombre() != null && s.getNombre().equalsIgnoreCase(nombre)).findFirst();
    }

    //@Override
    public List<Subcategoria> findByCategoriaId(Long categoriaId) {
        if (categoriaId == null) return List.of();
        return subcategorias.stream()
                .filter(s -> s.getCategoria() != null && categoriaId.equals(s.getCategoria().getId()))
                .collect(Collectors.toList());
    }

    //@Override
    public Long guardar(Subcategoria subcategoria) {
        if (subcategoria.getId() == null) {
            subcategoria.setId(generadorId.siguiente());
            subcategorias.add(subcategoria);
        } else {
            this.findById(subcategoria.getId()).ifPresent(subcategorias::remove);
            subcategorias.add(subcategoria);
        }
        return subcategoria.getId();
    }

    //@Override
    public void eliminar(Long id) {
        if (id != null) {
            subcategorias.removeIf(s -> id.equals(s.getId()));
        }
    }
}
