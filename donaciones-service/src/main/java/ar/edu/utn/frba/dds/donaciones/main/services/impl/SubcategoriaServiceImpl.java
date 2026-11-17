package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.CategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.repositories.SubcategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SubcategoriaServiceImpl implements SubcategoriaService {
    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    public SubcategoriaServiceImpl(SubcategoriaRepository subcategoriaRepository, CategoriaRepository categoriaRepository) {
        this.subcategoriaRepository = subcategoriaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<SubcategoriaResponse> obtenerTodas() {
        return subcategoriaRepository.findAll().stream()
                .map(this::mapearSubcategoriaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubcategoriaResponse obtenerPorId(Long id) {
        Subcategoria subcategoria = buscarEntidadPorId(id);
        return mapearSubcategoriaResponse(subcategoria);
    }

    @Override
    public List<SubcategoriaResponse> obtenerPorCategoriaId(Long categoriaId) {
        return subcategoriaRepository.findByCategoria_Id(categoriaId).stream()
                .map(this::mapearSubcategoriaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubcategoriaResponse crear(SubcategoriaRequest request) {
        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setNombre(request.nombre());
        subcategoria.setUnidadDeMedida(request.unidadDeMedida());

        if (request.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + request.categoriaId()));
            categoria.agregarSubcategoria(subcategoria);
        }

        subcategoriaRepository.save(subcategoria);
        return mapearSubcategoriaResponse(subcategoria);
    }

    @Override
    @Transactional
    public SubcategoriaResponse actualizar(Long id, SubcategoriaRequest request) {
        Subcategoria subcategoria = buscarEntidadPorId(id);
        subcategoria.setNombre(request.nombre());
        subcategoria.setUnidadDeMedida(request.unidadDeMedida());

        if (request.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + request.categoriaId()));
            categoria.agregarSubcategoria(subcategoria);
        }

        subcategoriaRepository.save(subcategoria);
        return mapearSubcategoriaResponse(subcategoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Subcategoria subcategoria = buscarEntidadPorId(id);
        if (subcategoria.getCategoria() != null) {
            subcategoria.getCategoria().getSubcategorias().remove(subcategoria);
        }
        subcategoriaRepository.deleteById(id);
    }

    @Override
    public Subcategoria buscarEntidadPorId(Long id) {
        return subcategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la subcategoría con ID: " + id));
    }

    @Override
    public Subcategoria buscarEntidadPorNombre(String nombre) {
        return subcategoriaRepository.findByNombre(Subcategoria.normalizarNombre(nombre))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la subcategoría con nombre: " + nombre));
    }

    @Override
    @Transactional
    public Long guardar(Subcategoria subcategoria) {
        return subcategoriaRepository.save(subcategoria).getId();
    }

    private SubcategoriaResponse mapearSubcategoriaResponse(Subcategoria subcategoria) {
        Long categoriaId = subcategoria.getCategoria() != null ? subcategoria.getCategoria().getId() : null;
        String categoriaNombre = subcategoria.getCategoria() != null ? subcategoria.getCategoria().getNombre() : null;
        return new SubcategoriaResponse(
                subcategoria.getId(),
                subcategoria.getNombre(),
                categoriaId,
                categoriaNombre,
                subcategoria.getUnidadDeMedida()
        );
    }
}
