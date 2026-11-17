package ar.edu.utn.frba.dds.donaciones.main.controllers;

import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donaciones-service/subcategorias")
public class SubcategoriaController {

    private final SubcategoriaService subcategoriaService;

    public SubcategoriaController(SubcategoriaService subcategoriaService) {
        this.subcategoriaService = subcategoriaService;
    }

    @GetMapping
    public List<SubcategoriaResponse> obtenerTodas(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return this.subcategoriaService.obtenerPorCategoriaId(categoriaId);
        }
        return this.subcategoriaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public SubcategoriaResponse obtenerPorId(@PathVariable Long id) {
        return this.subcategoriaService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('CREAR_SUBCATEGORIAS')")
    public SubcategoriaResponse crear(@RequestBody SubcategoriaRequest request) {
        return this.subcategoriaService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ACTUALIZAR_SUBCATEGORIAS')")
    public SubcategoriaResponse actualizar(@PathVariable Long id, @RequestBody SubcategoriaRequest request) {
        return this.subcategoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR') and hasAnyAuthority('ELIMINAR_SUBCATEGORIAS')")
    public void eliminar(@PathVariable Long id) {
        this.subcategoriaService.eliminar(id);
    }
}
