package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponseSinSubcategorias;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaSimpleResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.CategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CategoriaResponse> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::mapearCategoriaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseSinSubcategorias> obtenerTodasSinSubcategorias() {
        return categoriaRepository.findAll().stream()
            .map(this::mapearCategoriaResponseSinSubcategorias)
            .collect(Collectors.toList());
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = buscarEntidadPorId(id);
        return mapearCategoriaResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoriaRepository.save(categoria);
        return mapearCategoriaResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidadPorId(id);
        categoria.setNombre(request.nombre());
        categoriaRepository.save(categoria);
        return mapearCategoriaResponse(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscarEntidadPorId(id);
        categoriaRepository.deleteById(id);
    }

    @Override
    public Categoria buscarEntidadPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + id));
    }

    @Override
    public Categoria buscarEntidadPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con nombre: " + nombre));
    }

    @Override
    @Transactional
    public Long guardar(Categoria categoria) {
        return categoriaRepository.save(categoria).getId();
    }

    private CategoriaResponse mapearCategoriaResponse(Categoria categoria) {
        List<SubcategoriaSimpleResponse> subcategorias = categoria.getSubcategorias().stream()
                .map(s -> new SubcategoriaSimpleResponse(s.getId(), s.getNombre(), s.getUnidadDeMedida()))
                .collect(Collectors.toList());
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), subcategorias);
    }
    private CategoriaResponseSinSubcategorias mapearCategoriaResponseSinSubcategorias(Categoria categoria){
        return new CategoriaResponseSinSubcategorias(categoria.getId(),categoria.getNombre());
    }
}
