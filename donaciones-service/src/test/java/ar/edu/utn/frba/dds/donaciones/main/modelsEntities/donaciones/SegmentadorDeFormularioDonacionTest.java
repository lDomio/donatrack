package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SegmentadorDeFormularioDonacionTest {

    private SegmentadorDeFormularioDonacion segmentador;
    private Subcategoria leche;
    private Subcategoria ropa;
    private Subcategoria sillas;
    private Subcategoria mesas;
    private Subcategoria fideos;
    private Subcategoria conservas;

    @BeforeEach
    void setUp() {
        segmentador = new SegmentadorDeFormularioDonacion();
        leche = new Subcategoria();
        leche.setNombre("Leche");

        ropa = new Subcategoria();
        ropa.setNombre("Ropa");

        sillas = new Subcategoria();
        sillas.setNombre("Sillas");

        mesas = new Subcategoria();
        mesas.setNombre("Mesas");

        fideos = new Subcategoria();
        fideos.setNombre("Fideos secos");

        conservas = new Subcategoria();
        conservas.setNombre("Latas de conserva");
    }

    @Test
    @DisplayName("Debe separar bienes de distintas subcategorías en donaciones diferentes")
    void testSegmentarPorSubcategoria() {
        System.out.println("\n--- Ejecutando: testSegmentarPorSubcategoria ---");
        Bien lecheBien = crearBien("Leche entera", leche, null);
        Bien remera = crearBien("Remera", ropa, null);

        List<Donacion> resultado = segmentador.segmentar(List.of(lecheBien, remera));
        System.out.println("Donaciones generadas: " + resultado.size());
        resultado.forEach(d -> System.out.println(" - Donación de subcategoría: " + d.getSubcategoria()));

        assertEquals(2, resultado.size(), "Deberían generarse 2 donaciones");
        assertTrue(resultado.stream().anyMatch(d -> d.getSubcategoria().getNombre().equals("LECHE")));
        assertTrue(resultado.stream().anyMatch(d -> d.getSubcategoria().getNombre().equals("ROPA")));
    }

    @Test
    @DisplayName("Debe separar bienes de la misma subcategoría pero distinta fecha de vencimiento")
    void testSegmentarPorVencimiento() {
        System.out.println("\n--- Ejecutando: testSegmentarPorVencimiento ---");
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        Bien lecheVenceHoy = crearBien("Leche A", leche, hoy);
        Bien lecheVenceManana = crearBien("Leche B", leche, manana);

        List<Donacion> resultado = segmentador.segmentar(List.of(lecheVenceHoy, lecheVenceManana));
        System.out.println("Bienes con fechas: " + hoy + " y " + manana);
        System.out.println("Donaciones generadas: " + resultado.size());

        assertEquals(2, resultado.size(), "Deberían generarse 2 donaciones por tener distintos vencimientos");
    }

    @Test
    @DisplayName("Debe agrupar bienes con la misma subcategoría y el mismo vencimiento")
    void testAgruparBienesIguales() {
        System.out.println("\n--- Ejecutando: testAgruparBienesIguales ---");
        LocalDate fecha = LocalDate.of(2026, 12, 31);
        
        Bien fideos1 = crearBien("Leche A", leche, fecha);
        Bien fideos2 = crearBien("Leche B", leche, fecha);

        List<Donacion> resultado = segmentador.segmentar(List.of(fideos1, fideos2));
        System.out.println("Donaciones generadas: " + resultado.size());
        if(!resultado.isEmpty()) {
            System.out.println("Cantidad de bienes en la donación: " + resultado.get(0).getBienes().size());
        }

        assertEquals(1, resultado.size(), "Debería haber una sola donación");
        assertEquals(2, resultado.get(0).getBienes().size(), "La donación debería contener ambos bienes");
    }

    @Test
    @DisplayName("Las donaciones generadas deben iniciar en estado DEPOSITO")
    void testEstadoInicial() {
        System.out.println("\n--- Ejecutando: testEstadoInicial ---");
        Bien lecheBien = crearBien("Leche entera", leche, null);

        List<Donacion> resultado = segmentador.segmentar(List.of(lecheBien));
        Donacion donacion = resultado.get(0);
        TipoDeEstado estado = donacion.getEstadosDeDonaciones().get(0).getTipo();
        System.out.println("Estado inicial detectado: " + estado);

        assertEquals(1, donacion.getEstadosDeDonaciones().size());
        assertEquals(TipoDeEstado.DEPOSITO, estado);
    }

    @Test
    @DisplayName("Debe separar bienes de la misma subcategoría pero distinto estado (NUEVO vs USADO)")
    void testSegmentarPorEstado() {
        System.out.println("\n--- Ejecutando: testSegmentarPorEstado ---");
        Bien sillaUsada = crearBien("Silla usada", sillas, null, EstadoDeBienNoPerecedero.USADO, 6.0);
        Bien sillaNueva = crearBien("Silla nueva", sillas, null, EstadoDeBienNoPerecedero.NUEVO, 2.0);

        List<Donacion> resultado = segmentador.segmentar(List.of(sillaUsada, sillaNueva));
        System.out.println("Donaciones generadas: " + resultado.size());

        assertEquals(2, resultado.size(), "Deberían generarse 2 donaciones por tener distinto estado");
        resultado.forEach(d -> assertEquals(1, d.getBienes().size(), "Cada donación debe tener bienes de un solo estado"));
        assertTrue(resultado.stream().anyMatch(d -> d.getBienes().get(0).getEstado() == EstadoDeBienNoPerecedero.USADO));
        assertTrue(resultado.stream().anyMatch(d -> d.getBienes().get(0).getEstado() == EstadoDeBienNoPerecedero.NUEVO));
    }

    @Test
    @DisplayName("Debe agrupar bienes con misma subcategoría, mismo estado y mismo vencimiento")
    void testAgruparMismoEstado() {
        System.out.println("\n--- Ejecutando: testAgruparMismoEstado ---");
        Bien silla1 = crearBien("Silla A", sillas, null, EstadoDeBienNoPerecedero.USADO, 3.0);
        Bien silla2 = crearBien("Silla B", sillas, null, EstadoDeBienNoPerecedero.USADO, 3.0);

        List<Donacion> resultado = segmentador.segmentar(List.of(silla1, silla2));
        System.out.println("Donaciones generadas: " + resultado.size());

        assertEquals(1, resultado.size(), "Debería haber una sola donación");
        assertEquals(2, resultado.get(0).getBienes().size(), "La donación debería contener ambos bienes");
    }

    @Test
    @DisplayName("Flujo 3: el lote variado se segmenta en donaciones independientes, todas en DEPOSITO")
    void testLoteFlujo3() {
        System.out.println("\n--- Ejecutando: testLoteFlujo3 ---");
        LocalDate vencimientoFuturo = LocalDate.now().plusMonths(6);

        List<Bien> lote = List.of(
            crearBien("Sillas de madera", sillas, null, EstadoDeBienNoPerecedero.USADO, 6.0),
            crearBien("Mesa de comedor", mesas, null, EstadoDeBienNoPerecedero.NUEVO, 1.0),
            crearBien("Fideos secos 500 g", fideos, null, EstadoDeBienNoPerecedero.NUEVO, 100.0),
            crearBien("Latas de conserva", conservas, vencimientoFuturo, EstadoDeBienNoPerecedero.NUEVO, 50.0)
        );

        List<Donacion> resultado = segmentador.segmentar(lote);
        System.out.println("Donaciones generadas: " + resultado.size());

        assertEquals(4, resultado.size(), "El lote debe dividirse en 4 donaciones independientes");
        resultado.forEach(d -> {
            assertEquals(1, d.getEstadosDeDonaciones().size());
            assertEquals(TipoDeEstado.DEPOSITO, d.getEstadosDeDonaciones().getLast().getTipo());
        });
    }

    private Bien crearBien(String nombre, Subcategoria subcat, LocalDate vencimiento) {
        return crearBien(nombre, subcat, vencimiento, null, 1.00);
    }

    private Bien crearBien(String nombre, Subcategoria subcat, LocalDate vencimiento,
                           EstadoDeBienNoPerecedero estado, Double cantidad) {
        Bien bien = new Bien();
        bien.setNombre(nombre);
        bien.setSubcategoria(subcat);
        bien.setFechaVencimiento(vencimiento);
        bien.setEstado(estado);
        bien.setCantidad(cantidad);
        return bien;
    }
}
