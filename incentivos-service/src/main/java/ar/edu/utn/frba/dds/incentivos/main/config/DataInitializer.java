package ar.edu.utn.frba.dds.incentivos.main.config;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.*;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionDonacionesExitosas;
import ar.edu.utn.frba.dds.incentivos.main.repositories.CategoriaDonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.MisionRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.RankingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {
    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");

    private final DonanteRepository donanteRepository;
    private final DonacionRepository donacionRepository;
    private final RankingRepository rankingRepository;
    private final MisionRepository misionRepository;
    private final CategoriaDonanteRepository categoriaDonanteRepository;

    public DataInitializer(DonanteRepository donanteRepository,
                           DonacionRepository donacionRepository,
                           RankingRepository rankingRepository,
                           MisionRepository misionRepository,
                           CategoriaDonanteRepository categoriaDonanteRepository) {
        this.donanteRepository = donanteRepository;
        this.donacionRepository = donacionRepository;
        this.rankingRepository = rankingRepository;
        this.misionRepository = misionRepository;
        this.categoriaDonanteRepository = categoriaDonanteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (donanteRepository.count() > 0 || categoriaDonanteRepository.count() > 0) {
            System.out.println("Incentivos: Los datos iniciales ya existen.");
            asegurarRankingInicial();
            return;
        }
        System.out.println("Cargando datos iniciales de Incentivos...");

        // 1. Crear insignias
        Insignia insignia1 = new Insignia("Insignia de Donador Novato (1 donación exitosa)");
        Insignia insignia2 = new Insignia("Insignia de Donador Avanzado (5 donaciones exitosas)");

        // 2. Crear y persistir categorías
        CategoriaDonante catActivo = categoriaDonanteRepository.save(new CategoriaDonante("COLABORADOR_ACTIVO", null, 2));
        CategoriaDonante catInicial = categoriaDonanteRepository.save(new CategoriaDonante("COLABORADOR_INICIAL", null, 1));
        CategoriaDonante catLeyenda = categoriaDonanteRepository.save(new CategoriaDonante("COLABORADOR_LEYENDA", null, 3));

        // 3. Crear y persistir misiones
        Mision mision1 = new Mision("Mision donaciones exitosas 1", insignia1, new MisionDonacionesExitosas(1.0));
        Mision mision2 = new Mision("Mision donaciones exitosas 2", insignia2, new MisionDonacionesExitosas(5.0));

        mision1.setCategoria(catActivo);
        mision2.setCategoria(catActivo);

        mision1 = misionRepository.save(mision1);
        mision2 = misionRepository.save(mision2);

        // Enlazar misiones luego de persistirlas
        mision1.setMisionSiguiente(mision2);
        mision2.setMisionAnterior(mision1);

        mision1 = misionRepository.save(mision1);
        mision2 = misionRepository.save(mision2);

        catActivo.setPrimeraMision(mision1);
        categoriaDonanteRepository.save(catActivo);

        // 4. Crear Donantes
        Donante donante1 = new Donante();
        donante1.setIdDonanteDonacionesService(1L);
        donante1.setNombreUsuario("Juan Pérez");
        donante1.setCategoria(catActivo);
        donante1.setMisionActual(mision1);
        donante1.setFechaInicioMisionActual(LocalDate.now().minusDays(10));
        donante1.setMedioContacto(new ArrayList<>(List.of(
            new MedioDeContacto(TipoDeContacto.EMAIL, "juan.perez@test.com"),
            new MedioDeContacto(TipoDeContacto.WHATSAPP, "+5491122334455")
        )));

        Donante donante2 = new Donante();
        donante2.setIdDonanteDonacionesService(2L);
        donante2.setNombreUsuario("María López");
        donante2.setCategoria(catInicial);
        donante2.setMisionActual(mision1);
        donante2.setFechaInicioMisionActual(LocalDate.now().minusDays(10));
        donante2.setMedioContacto(new ArrayList<>(List.of(
            new MedioDeContacto(TipoDeContacto.EMAIL, "maria.lopez@test.com")
        )));
 
        Donante donante3 = new Donante();
        donante3.setIdDonanteDonacionesService(3L);
        donante3.setNombreUsuario("Carlos García");
        donante3.setCategoria(catLeyenda);
        donante3.setMisionActual(mision1);
        donante3.setFechaInicioMisionActual(LocalDate.now().minusDays(10));
        donante3.setMedioContacto(new ArrayList<>(List.of(
            new MedioDeContacto(TipoDeContacto.SMS, "+5491166778899")
        )));
 
        // 4. Crear Donaciones iniciales con IDs de prueba
        Donacion donacion1 = new Donacion(LocalDate.now().minusDays(5), 3.0, "ALIMENTOS", 901L, donante1, "Catedral");
        donacion1.setFueRecibida(true);
        donacion1.setFechaDeRecepcion(LocalDate.now().minusDays(5));

        Donacion donacion2 = new Donacion(LocalDate.now().minusDays(2), 2.0, "ROPA", 902L, donante1, null);
        donacion2.setFueRecibida(false);
        
        Donacion donacion3 = new Donacion(LocalDate.now().minusDays(12), 10.0, "ALIMENTOS", 903L, donante2, "Hogar de Dia");
        donacion3.setFueRecibida(true);
        donacion3.setFechaDeRecepcion(LocalDate.now().minusDays(12));

        Donacion donacion4 = new Donacion(LocalDate.now().minusDays(15), 25.0, "ROPA", 904L, donante3, "Catedral");
        donacion4.setFueRecibida(true);
        donacion4.setFechaDeRecepcion(LocalDate.now().minusDays(15));

        // Asociar donaciones a donantes
        donante1.registrarDonacion(donacion1);
        donante1.registrarDonacion(donacion2);
        donante2.registrarDonacion(donacion3);
        donante3.registrarDonacion(donacion4);

        // Guardar en repositorios
        donante1 = donanteRepository.save(donante1);
        donante2 = donanteRepository.save(donante2);
        donante3 = donanteRepository.save(donante3);

        // Calcular progreso inicial
        donante1.setProgresoMisionActual(mision1.calcularProgreso(donante1));
        donante2.setProgresoMisionActual(mision1.calcularProgreso(donante2));
        donante3.setProgresoMisionActual(mision1.calcularProgreso(donante3));
        
        donante1 = donanteRepository.save(donante1);
        donante2 = donanteRepository.save(donante2);
        donante3 = donanteRepository.save(donante3);

        // 5. Crear y guardar Ranking ordenado por mayor cantidad de donaciones
        RankingDonantes ranking = new RankingDonantes(
                YearMonth.now(ZONA).minusMonths(1).atDay(1),
                List.of(donante1, donante2, donante3));
        rankingRepository.save(ranking);

        System.out.println("Incentivos: Datos iniciales cargados correctamente (3 Donantes, 4 Donaciones, 1 Ranking y Misiones).");
    }

    private void asegurarRankingInicial() {
        try {
            if (rankingRepository.count() == 0) {
                System.out.println("Incentivos: No se encontro ranking persistido. Asegurando ranking inicial...");
                List<Donante> donantes = donanteRepository.findAll();
                if (donantes.isEmpty()) {
                    Donante d1 = new Donante();
                    d1.setIdDonanteDonacionesService(9991L);
                    d1.setNombreUsuario("Juan Perez");
                    d1 = donanteRepository.save(d1);

                    Donante d2 = new Donante();
                    d2.setIdDonanteDonacionesService(9992L);
                    d2.setNombreUsuario("Maria Lopez");
                    d2 = donanteRepository.save(d2);

                    Donante d3 = new Donante();
                    d3.setIdDonanteDonacionesService(9993L);
                    d3.setNombreUsuario("Carlos Garcia");
                    d3 = donanteRepository.save(d3);

                    donantes = List.of(d1, d2, d3);
                }

                RankingDonantes ranking = new RankingDonantes(
                        YearMonth.now(ZONA).minusMonths(1).atDay(1),
                        donantes);
                rankingRepository.save(ranking);
                System.out.println("Incentivos: Ranking inicial creado con " + donantes.size() + " donantes.");
            } else {
                System.out.println("Incentivos: Ranking ya existe en base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Incentivos: Advertencia al asegurar ranking inicial: " + e.getMessage());
        }
    }
}
