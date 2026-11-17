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
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
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

import java.time.LocalDate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test de repositorio real (H2 + Hibernate, sin Spring Context): ejecuta el
 * JPQL de {@link DonacionRepository#contarEntidadesDistintasAyudadas} contra
 * datos de prueba. Boot 4 ya no trae el slice @DataJpaTest, por eso el
 * bootstrap es manual.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DonacionRepositoryConteoTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private DonacionRepository repository;

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
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:conteo;DB_CLOSE_DELAY=-1;MODE=MySQL");
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa");
        cfg.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "");
        cfg.setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, "create-drop");
        SessionFactory sf = cfg.buildSessionFactory();
        emf = sf;
        em = emf.createEntityManager();
        repository = new JpaRepositoryFactory(em).getRepository(DonacionRepository.class);
    }

    @AfterAll
    void shutdown() {
        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
    }

    private void enTransaccion(Consumer<EntityManager> trabajo) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            trabajo.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
        em.clear();
    }

    private EntidadBeneficiaria entidad(EntityManager em, String razonSocial) {
        EntidadBeneficiaria e = new EntidadBeneficiaria();
        e.setRazonSocial(razonSocial);
        em.persist(e);
        return e;
    }

    private Necesidad necesidadDe(EntityManager em, EntidadBeneficiaria entidad) {
        Necesidad n = new Necesidad();
        n.setEntidadBeneficiaria(entidad);
        em.persist(n);
        return n;
    }

    private void donacion(EntityManager em, Long idDonante, LocalDate fecha, Necesidad necesidad) {
        Donacion d = new Donacion();
        d.setIdDonante(idDonante);
        d.setFechaDeCreacion(fecha);
        d.setNecesidad(necesidad);
        em.persist(d);
    }

    @Test
    @DisplayName("Cuenta distintas del mes: 2 entidades + duplicada + sin necesidad + mes anterior + otro donante = 2")
    void testContarEntidadesDistintasAyudadas() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.withDayOfMonth(1);
        LocalDate fin = hoy.withDayOfMonth(hoy.lengthOfMonth());

        enTransaccion(em -> {
            EntidadBeneficiaria a = entidad(em, "Comedor A");
            EntidadBeneficiaria b = entidad(em, "Fundacion B");
            //Ojo: Donacion.necesidad es @OneToOne (necesidad_id UNIQUE en DDL),
            //cada donacion necesita su propia fila de necesidad, como en produccion.
            Necesidad na1 = necesidadDe(em, a);
            Necesidad na2 = necesidadDe(em, a);
            Necesidad na3 = necesidadDe(em, a);
            Necesidad nb1 = necesidadDe(em, b);
            Necesidad nb2 = necesidadDe(em, b);

            donacion(em, 5L, hoy, na1); //entidad A
            donacion(em, 5L, hoy, nb1); //entidad B
            donacion(em, 5L, hoy, na2); //entidad A repetida -> no suma
            donacion(em, 5L, hoy, null); //sin necesidad -> se excluye
            donacion(em, 5L, inicio.minusDays(1), na3); //mes anterior -> se excluye
            donacion(em, 7L, hoy, nb2); //otro donante -> se excluye
        });

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            assertEquals(2L, repository.contarEntidadesDistintasAyudadas(5L, inicio, fin));
            assertEquals(1L, repository.contarEntidadesDistintasAyudadas(7L, inicio, fin));
            assertEquals(0L, repository.contarEntidadesDistintasAyudadas(999L, inicio, fin));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
