package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;

import java.util.List;

public interface SubcategoriaService {
    List<SubcategoriaResponse> obtenerTodas();
    SubcategoriaResponse obtenerPorId(Long id);
    List<SubcategoriaResponse> obtenerPorCategoriaId(Long categoriaId);
    SubcategoriaResponse crear(SubcategoriaRequest request);
    SubcategoriaResponse actualizar(Long id, SubcategoriaRequest request);
    void eliminar(Long id);
    Subcategoria buscarEntidadPorId(Long id);
    Subcategoria buscarEntidadPorNombre(String nombre);
    Long guardar(Subcategoria subcategoria);
}
