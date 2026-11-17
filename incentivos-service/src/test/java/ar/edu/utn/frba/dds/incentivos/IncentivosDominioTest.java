package ar.edu.utn.frba.dds.incentivos;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletitud;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionHabilDonador;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionRacha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IncentivosDominioTest {

    private Donante donante;
    private Insignia insigniaColaborador;
    private Mision misionColaborador;

    @BeforeEach
    void setUp() {
        donante = new Donante();
        donante.setDonaciones(new ArrayList<>());
        donante.setMisionesCompletadas(new ArrayList<>());
        donante.setInsignias(new ArrayList<>());

        insigniaColaborador = new Insignia("Primer Paso - Test");

        // 1. Instanciar categorías
        CategoriaDonante catColaborador = new CategoriaDonante("COLABORADOR", null, 1);
        CategoriaDonante catSostenedor = new CategoriaDonante("SOSTENEDOR", null, 2);
        CategoriaDonante catTransformador = new CategoriaDonante("TRANSFORMADOR", null, 1);

        // 2. Instanciar misiones
        misionColaborador = new Mision("Habil donador", insigniaColaborador, new MisionHabilDonador(5.0));
        misionColaborador.setCategoria(catColaborador);
        catColaborador.setPrimeraMision(misionColaborador);

        Mision m2 = new Mision("Racha", new Insignia("Constancia Test"), new MisionRacha(3.0));
        m2.setCategoria(catSostenedor);
        catSostenedor.setPrimeraMision(m2);

        Mision m3 = new Mision("Completitud", new Insignia("Impacto Test"), new MisionRacha(3.0));
        m3.setCategoria(catSostenedor);

        Mision m4 = new Mision("Habil donador 2", new Insignia("Mega Donador Test"), new MisionHabilDonador(500.0));
        m4.setCategoria(catTransformador);
        catTransformador.setPrimeraMision(m4);

        // 3. Enlazar misiones
        misionColaborador.setMisionSiguiente(m2);
        
        m2.setMisionAnterior(misionColaborador);
        m2.setMisionSiguiente(m3);

        m3.setMisionAnterior(m2);
        m3.setMisionSiguiente(m4);

        m4.setMisionAnterior(m3);

        // 4. Asignar estado inicial al donante
        donante.setCategoria(catColaborador);
        donante.setMisionActual(misionColaborador);
        donante.setProgresoMisionActual(misionColaborador.calcularProgreso(donante));
    }

    @Test
    void testRegistrarDonacion_IncrementaProgresoSinCompletarMision() {
        // Arrange
        Donacion donacionParcial = new Donacion();
        donacionParcial.setCantidadDeBien(3.0); // 3 de 5 requeridos
        donacionParcial.setFechaDeCreacion(java.time.LocalDate.now());

        // Act
        donante.registrarDonacion(donacionParcial);
        donante.completarMisionSiCompletadaYGenerarNotificaciones();

        // Assert
        assertNotNull(donante.getProgresoMisionActual());
        assertTrue(donante.getProgresoMisionActual().getProgreso() > 0.0);
        assertTrue(donante.getProgresoMisionActual().getProgreso() < 100.0);

        // Verificamos que no se disparó ningún evento de completitud
        assertFalse(donante.getInsignias().contains(insigniaColaborador));
        
        boolean misionCompletada = donante.getMisionesCompletadas().stream()
                .anyMatch(mc -> mc.getMision().getNombre().equals(misionColaborador.getNombre()));
        assertFalse(misionCompletada);
    }

    @Test
    void testRegistrarDonacion_CompletaMisionYSubreDeCategoria() {
        // Arrange
        Donacion donacionSuficiente = new Donacion();
        donacionSuficiente.setCantidadDeBien(5.0); // 5 de 5 -> Completa la misión única de Colaborador
        donacionSuficiente.setFechaDeCreacion(java.time.LocalDate.now());

        CategoriaDonante categoriaAntesDelRegistro = donante.getCategoria();

        // Act
        donante.registrarDonacion(donacionSuficiente);
        donante.completarMisionSiCompletadaYGenerarNotificaciones();

        // Assert: Efectos a nivel Donante
        boolean misionCompletada = donante.getMisionesCompletadas().stream()
                .anyMatch(mc -> mc.getMision().getNombre().equals(misionColaborador.getNombre()));
        assertTrue(misionCompletada, "El donante debe tener la misión en su historial");
        assertTrue(donante.getInsignias().contains(insigniaColaborador), "El donante debe haber recibido la insignia");

        // Assert: Efectos a nivel Categoría
        assertNotEquals(categoriaAntesDelRegistro, donante.getCategoria(), "La instancia de categoría tuvo que cambiar");

        // Verificamos que efectivamente subió a SOSTENEDOR
        assertEquals("SOSTENEDOR", donante.getCategoria().getNombre(), "El nombre de la categoría ahora debe ser SOSTENEDOR");

        // Verificamos que la nueva categoría arrancó con la misión de Sostenedor que seteamos en el Factory
        // Al completarse con una donación de hoy, MisionRacha cuenta esa donación como el primer mes de racha (1 de 3 = 33.33%)
        assertEquals(100.0 / 3.0, donante.getProgresoMisionActual().getProgreso(), 0.01, "El progreso en la nueva categoría (MisionRacha) debe ser 33.33% por la donación actual");
    }
}