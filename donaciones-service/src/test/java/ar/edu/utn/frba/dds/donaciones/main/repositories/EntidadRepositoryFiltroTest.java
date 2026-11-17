package ar.edu.utn.frba.dds.donaciones.main.repositories;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Bien;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.FormularioDeDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Representante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.TipoEntidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Ciudad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Periodo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test de repositorio real (H2 + Hibernate, sin Spring Context): ejecuta el
 * JPQL de {@link EntidadBeneficiariaRepository#buscarConFiltros} contra datos
 * de prueba. Boot 4 ya no trae el slice @DataJpaTest, por eso el bootstrap
 * es manual.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntidadRepositoryFiltroTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private EntidadBeneficiariaRepository repository;

    @BeforeAll
    void bootstrap() {
        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(EntidadBeneficiaria.class);
        cfg.addAnnotatedClass(MedioDeContacto.class);
        cfg.addAnnotatedClass(Categoria.class);
        cfg.addAnnotatedClass(Subcategoria.class);
        cfg.addAnnotatedClass(Donacion.class);
        cfg.addAnnotatedClass(FormularioDeDonacion.class);
        cfg.addAnnotatedClass(Bien.class);
        cfg.addAnnotatedClass(Donante.class);
        cfg.addAnnotatedClass(Representante.class);
        cfg.addAnnotatedClass(EstadoDeUnaDonacion.class);
        cfg.addAnnotatedClass(Periodo.class);
        cfg.addAnnotatedClass(Necesidad.class);
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.h2.Driver");
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:filtro;DB_CLOSE_DELAY=-1;MODE=MySQL");
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa");
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "");
        cfg.setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, "create-drop");
        SessionFactory sf = cfg.buildSessionFactory();
        emf = sf;
        em = emf.createEntityManager();
        repository = new JpaRepositoryFactory(em).getRepository(EntidadBeneficiariaRepository.class);

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            persistir("Comedor Los Bajitos", "COMEDOR", "Av. Siempreviva", "Rosario");
            persistir("Fundacion Vida", "ONG", "Calle Falsa", "CABA");
            persistir("Escuela Esperanza", "EDUCACION", "Mitre", "CABA");
            persistirSinDireccion("Centro Salud Norte", "SALUD");
            persistir("Refugio Amanecer", "REFUGIO", "Rivadavia", null);
            persistir("Club 100% Barrial", "ONG", "Siempre", "La Plata");
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
        em.clear();
    }

    @AfterAll
    void shutdown() {
        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
    }

    private void persistir(String razonSocial, String tipo, String calle, String ciudad) {
        EntidadBeneficiaria e = new EntidadBeneficiaria();
        e.setRazonSocial(razonSocial);
        TipoEntidad t = new TipoEntidad();
        t.setTipoEntidad(tipo);
        e.setTipo(t);
        Direccion d = new Direccion();
        d.setCalle(calle);
        if (ciudad != null) {
            Ciudad c = new Ciudad();
            ReflectionTestUtils.setField(c, "Nombre", ciudad);
            d.setCiudad(c);
        }
        e.setDireccion(d);
        em.persist(e);
    }

    private void persistirSinDireccion(String razonSocial, String tipo) {
        EntidadBeneficiaria e = new EntidadBeneficiaria();
        e.setRazonSocial(razonSocial);
        TipoEntidad t = new TipoEntidad();
        t.setTipoEntidad(tipo);
        e.setTipo(t);
        e.setDireccion(null);
        em.persist(e);
    }

    private List<String> enTransaccion(java.util.function.Supplier<List<EntidadBeneficiaria>> consulta) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            List<String> razones = consulta.get().stream()
                    .map(EntidadBeneficiaria::getRazonSocial).sorted().toList();
            tx.commit();
            return razones;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Test
    @DisplayName("Sin filtros devuelve todas")
    void testSinFiltros() {
        assertEquals(6, enTransaccion(() ->
                repository.buscarConFiltros(null, null, null, null)).size());
    }

    @Test
    @DisplayName("busqueda por nombre aunque la ubicacion no coincida")
    void testBusquedaPorNombre() {
        assertEquals(List.of("Comedor Los Bajitos"), enTransaccion(() ->
                repository.buscarConFiltros("bajitos", null, null, null)));
    }

    @Test
    @DisplayName("busqueda por calle o ciudad aunque el nombre no coincida")
    void testBusquedaPorUbicacion() {
        assertEquals(List.of("Comedor Los Bajitos"), enTransaccion(() ->
                repository.buscarConFiltros("siempreviva", null, null, null)));
        assertEquals(List.of("Escuela Esperanza", "Fundacion Vida"), enTransaccion(() ->
                repository.buscarConFiltros("CABA", null, null, null)));
    }

    @Test
    @DisplayName("Filtros finos en AND y entidad sin direccion excluida de ubicacion")
    void testFinos() {
        assertEquals(List.of("Fundacion Vida"), enTransaccion(() ->
                repository.buscarConFiltros(null, "vida", null, null)));
        assertEquals(List.of("Comedor Los Bajitos"), enTransaccion(() ->
                repository.buscarConFiltros(null, null, "rosario", null)));
        assertTrue(enTransaccion(() ->
                repository.buscarConFiltros(null, null, "norte", null)).isEmpty());
        assertTrue(enTransaccion(() ->
                repository.buscarConFiltros(null, "escuela", "rosario", null)).isEmpty());
    }

    @Test
    @DisplayName("Tipo canonico case-insensitive y combinado en AND")
    void testTipo() {
        assertEquals(List.of("Comedor Los Bajitos"), enTransaccion(() ->
                repository.buscarConFiltros(null, null, null, "comedor")));
        assertEquals(List.of("Fundacion Vida"), enTransaccion(() ->
                repository.buscarConFiltros("vida", null, null, "ONG")));
        assertTrue(enTransaccion(() ->
                repository.buscarConFiltros("vida", null, null, "COMEDOR")).isEmpty());
    }

    @Test
    @DisplayName("Comodines LIKE escapados se buscan literal")
    void testEscapeLike() {
        //texto de usuario "100%": escapado a "100\%" solo matchea el nombre literal
        assertEquals(List.of("Club 100% Barrial"), enTransaccion(() ->
                repository.buscarConFiltros("100\\%", null, null, null)));
        //texto de usuario "_": escapado a "\_" no matchea nada;
        //sin escapar, "%_%" matchearia todas las filas no vacias
        assertTrue(enTransaccion(() ->
                repository.buscarConFiltros("\\_", null, null, null)).isEmpty());
    }
}
