package ar.edu.utn.frba.dds.donaciones.main.services;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponseSinSubcategorias;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponse> obtenerTodas();
    List<CategoriaResponseSinSubcategorias> obtenerTodasSinSubcategorias();
    CategoriaResponse obtenerPorId(Long id);
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    void eliminar(Long id);
    Categoria buscarEntidadPorId(Long id);
    Categoria buscarEntidadPorNombre(String nombre);
    Long guardar(Categoria categoria);
}
