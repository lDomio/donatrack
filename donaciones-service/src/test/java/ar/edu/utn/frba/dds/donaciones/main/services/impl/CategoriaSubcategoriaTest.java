package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.CategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.categoria.SubcategoriaResponse;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.repositories.CategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.repositories.SubcategoriaRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.CategoriaService;
import ar.edu.utn.frba.dds.donaciones.main.services.SubcategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoriaSubcategoriaTest {

    private CategoriaRepository categoriaRepository;
    private SubcategoriaRepository subcategoriaRepository;
    private CategoriaService categoriaService;
    private SubcategoriaService subcategoriaService;

    @BeforeEach
    void setUp() {
        categoriaRepository = Mockito.mock(CategoriaRepository.class);
        subcategoriaRepository = Mockito.mock(SubcategoriaRepository.class);
        categoriaService = new CategoriaServiceImpl(categoriaRepository);
        subcategoriaService = new SubcategoriaServiceImpl(subcategoriaRepository, categoriaRepository);

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria c = invocation.getArgument(0);
            if (c.getId() == null) c.setId(1L);
            return c;
        });

        when(categoriaRepository.findById(1L)).thenAnswer(invocation -> {
            Categoria c = new Categoria();
            c.setId(1L);
            c.setNombre("Alimentos");
            return Optional.of(c);
        });

        when(subcategoriaRepository.save(any(Subcategoria.class))).thenAnswer(invocation -> {
            Subcategoria s = invocation.getArgument(0);
            if (s.getId() == null) s.setId(1L);
            return s;
        });

        when(subcategoriaRepository.findByCategoria_Id(1L)).thenAnswer(invocation -> {
            Categoria c = new Categoria();
            c.setId(1L);
            c.setNombre("Alimentos");
            Subcategoria s = new Subcategoria("Arroz", c, "KG");
            s.setId(1L);
            return Collections.singletonList(s);
        });

        when(subcategoriaRepository.findById(999L)).thenReturn(Optional.empty());
        when(subcategoriaRepository.findByNombre("Inexistente")).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Debe crear una categoría y recuperarla")
    void testCrearYRecuperarCategoria() {
        CategoriaResponse response = categoriaService.crear(new CategoriaRequest("Alimentos"));
        assertNotNull(response.id());
        assertEquals("Alimentos", response.nombre());

        CategoriaResponse recuperada = categoriaService.obtenerPorId(response.id());
        assertEquals("Alimentos", recuperada.nombre());
    }

    @Test
    @DisplayName("Debe crear una subcategoría asociada a una categoría con su unidad de medida")
    void testCrearSubcategoriaConUnidad() {
        CategoriaResponse cat = categoriaService.crear(new CategoriaRequest("Alimentos"));

        SubcategoriaResponse sub = subcategoriaService.crear(new SubcategoriaRequest("Arroz", cat.id(), "KG"));
        assertNotNull(sub.id());
        assertEquals("ARROZ", sub.nombre());
        assertEquals("KG", sub.unidadDeMedida());
        assertEquals(cat.id(), sub.categoriaId());
        assertEquals("Alimentos", sub.categoriaNombre());

        List<SubcategoriaResponse> subcategoriasDeCat = subcategoriaService.obtenerPorCategoriaId(cat.id());
        assertEquals(1, subcategoriasDeCat.size());
        assertEquals("ARROZ", subcategoriasDeCat.get(0).nombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando se busca una subcategoría inexistente")
    void testSubcategoriaInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> subcategoriaService.buscarEntidadPorId(999L));
        assertThrows(ResourceNotFoundException.class, () -> subcategoriaService.buscarEntidadPorNombre("Inexistente"));
    }
}
