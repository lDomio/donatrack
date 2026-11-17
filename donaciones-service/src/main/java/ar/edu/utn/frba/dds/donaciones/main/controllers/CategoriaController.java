package ar.edu.utn.frba.dds.donaciones.main.controllers;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponseSinSubcategorias;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.services.CategoriaService;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones-service/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final SubcategoriaService subcategoriaService;

    public CategoriaController(CategoriaService categoriaService, SubcategoriaService subcategoriaService) {
        this.categoriaService = categoriaService;
        this.subcategoriaService = subcategoriaService;
    }

    @GetMapping
    public List<CategoriaResponse> obtenerTodas() {
        return this.categoriaService.obtenerTodas();
    }
    @GetMapping(params = "incluirSubcategorias=false")
    public List<CategoriaResponseSinSubcategorias> obtenerTodasSinSubcategoria(){
        return this.categoriaService.obtenerTodasSinSubcategorias();
    }

    @GetMapping("/{id}")
    public CategoriaResponse obtenerPorId(@PathVariable Long id) {
        return this.categoriaService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('CREAR_CATEGORIAS')")
    public CategoriaResponse crear(@RequestBody CategoriaRequest request) {
        return this.categoriaService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ACTUALIZAR_CATEGORIAS')")
    public CategoriaResponse actualizar(@PathVariable Long id, @RequestBody CategoriaRequest request) {
        return this.categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_CATEGORIAS')")
    public void eliminar(@PathVariable Long id) {
        this.categoriaService.eliminar(id);
    }

    @GetMapping("/{id}/subcategorias")
    public List<SubcategoriaResponse> obtenerSubcategoriasDeCategoria(@PathVariable Long id) {
        return this.subcategoriaService.obtenerPorCategoriaId(id);
    }
}
