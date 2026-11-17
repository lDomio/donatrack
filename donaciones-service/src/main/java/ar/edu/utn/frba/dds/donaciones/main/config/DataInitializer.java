package ar.edu.utn.frba.dds.donaciones.main.config;

import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.BienRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.HumanoCreateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.JuridicoCreateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.EntidadBeneficiariaResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.entidadBeneficiaria.NecesidadRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Categoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.categorias.Subcategoria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.TipoDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Bien;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.TipoPersona;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.EstadoDeUnaDonacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.estadoDonaciones.TipoDeEstado;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.TipoNecesidadRecurrente;
import ar.edu.utn.frba.dds.donaciones.main.repositories.*;
import ar.edu.utn.frba.dds.donaciones.main.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(DonanteService donanteService, 
                                    DonacionService donacionService, 
                                    EntidadBeneficiariaService entidadService,
                                    CategoriaService categoriaService,
                                    SubcategoriaService subcategoriaService,
                                    DonanteRepository donanteRepository,
                                    DonacionRepository donacionRepository,
                                    EntidadBeneficiariaRepository entidadBeneficiariaRepository,
                                    CategoriaRepository categoriaRepository,
                                    SubcategoriaRepository subcategoriaRepository,
                                    NecesidadRepository necesidadRepository,
                                    TransactionTemplate transactionTemplate) {
        // Todo el seed corre en UNA transaccion. Dos motivos:
        // 1) mantiene la sesion de Hibernate abierta, asi las entidades que se leen por
        //    repositorio siguen managed y sus colecciones LAZY se pueden inicializar;
        // 2) lo hace atomico: si algo falla no queda la base a medio sembrar, que es lo que
        //    hace que los guards "if (...isEmpty())" saltee bloques en el arranque siguiente.
        return args -> transactionTemplate.executeWithoutResult(status -> {
            // 0. Cargar Categorías y Subcategorías iniciales
            if (categoriaRepository.findAll().isEmpty()) {
                Categoria catAlimentos = new Categoria();
                catAlimentos.setNombre("Alimentos");
                categoriaRepository.save(catAlimentos);

                Subcategoria subArroz = new Subcategoria("ARROZ", catAlimentos, "KG");
                subcategoriaRepository.save(subArroz);
                catAlimentos.agregarSubcategoria(subArroz);

                Subcategoria subArrozBlanco = new Subcategoria("ARROZ BLANCO", catAlimentos, "KG");
                subcategoriaRepository.save(subArrozBlanco);
                catAlimentos.agregarSubcategoria(subArrozBlanco);

                Subcategoria subLeche = new Subcategoria("LECHE LARGA VIDA", catAlimentos, "L");
                subcategoriaRepository.save(subLeche);
                catAlimentos.agregarSubcategoria(subLeche);

                Subcategoria subFideos = new Subcategoria("FIDEOS SECOS", catAlimentos, "KG");
                subcategoriaRepository.save(subFideos);
                catAlimentos.agregarSubcategoria(subFideos);

                Subcategoria subLatas = new Subcategoria("LATAS DE CONSERVA", catAlimentos, "UNIDADES");
                subcategoriaRepository.save(subLatas);
                catAlimentos.agregarSubcategoria(subLatas);

                Categoria catVestimenta = new Categoria();
                catVestimenta.setNombre("Vestimenta");
                categoriaRepository.save(catVestimenta);

                Subcategoria subCamperas = new Subcategoria("CAMPERAS", catVestimenta, "UNIDADES");
                subcategoriaRepository.save(subCamperas);
                catVestimenta.agregarSubcategoria(subCamperas);

                Subcategoria subRopa = new Subcategoria("ROPA", catVestimenta, "UNIDADES");
                subcategoriaRepository.save(subRopa);
                catVestimenta.agregarSubcategoria(subRopa);

                Categoria catMobiliario = new Categoria();
                catMobiliario.setNombre("Mobiliario");
                categoriaRepository.save(catMobiliario);

                Subcategoria subSillas = new Subcategoria("SILLAS", catMobiliario, "UNIDADES");
                subcategoriaRepository.save(subSillas);
                catMobiliario.agregarSubcategoria(subSillas);

                Subcategoria subMesas = new Subcategoria("MESAS", catMobiliario, "UNIDADES");
                subcategoriaRepository.save(subMesas);
                catMobiliario.agregarSubcategoria(subMesas);
            }

            // 1. Cargar Donantes iniciales (60 donantes en total: 52 humanas, 8 jurídicas; 4 humanas y 2 jurídicas inactivas)
            Long idDonanteJuan = null;
            Long idDonanteUtn = null;
            Long idDonanteFacundo = null;

            if (donanteRepository.count() == 0) {
                // --- A. 52 Donantes Humanos (48 activos, 4 dados de baja) ---
                String[] nombres = {
                    "Juan", "Facundo", "Martín", "Lucía", "Santiago", "Camila", "Agustín", "Valentina", "Joaquín", "Florencia",
                    "Ignacio", "Sofía", "Mateo", "Julieta", "Tomás", "Micaela", "Nicolás", "Paula", "Federico", "Delfina",
                    "Gonzalo", "Martina", "Rodrigo", "Rocío", "Emiliano", "Catalina", "Lucas", "Victoria", "Franco", "Carolina",
                    "Lautaro", "Agustina", "Esteban", "Malena", "Nahuel", "Bárbara", "Mariano", "Solana", "Gastón", "Luciana",
                    "Guillermo", "Cecilia", "Hernán", "Daniela", "Bruno", "Milagros", "Diego", "Abril", "Javier", "Candela",
                    "Pablo", "Clara"
                };
                String[] apellidos = {
                    "Perez", "Test", "González", "Rodríguez", "López", "Fernández", "García", "Martínez", "Romero", "Díaz",
                    "Álvarez", "Torres", "Ruiz", "Ramírez", "Flores", "Acosta", "Benítez", "Medina", "Herrera", "Suárez",
                    "Castro", "Giménez", "Gutiérrez", "Pereyra", "Ríos", "Vázquez", "Morales", "Sánchez", "Rojas", "Ortiz",
                    "Silva", "Nuñez", "Luna", "Juárez", "Cabrera", "Castillo", "Vega", "Paz", "Navarro", "Soto",
                    "Domínguez", "Reyes", "Molina", "Ponce", "Peralta", "Quiroga", "Alonso", "Pacheco", "Guzmán", "Muñoz",
                    "Blanco", "Rossi"
                };
                String[] calles = {
                    "Av. Corrientes", "Av. Rivadavia", "Av. Santa Fe", "Av. San Martín", "Av. Belgrano",
                    "Av. Cabildo", "Calle Florida", "Av. Córdoba", "Calle Lavalle", "Av. de Mayo"
                };

                for (int i = 0; i < 52; i++) {
                    String nombre = nombres[i];
                    String apellido = apellidos[i];
                    String genero = (i % 2 == 0) ? "Masculino" : "Femenino";
                    LocalDate fechaNac = LocalDate.of(1975 + (i % 25), (i % 12) + 1, (i % 27) + 1);
                    String dni = String.valueOf(25000000 + (i * 14753) % 20000000);
                    String calle = calles[i % calles.length];
                    int altura = 100 + (i * 73) % 4000;

                    String emailUser = (nombre + "." + apellido).toLowerCase()
                            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") + "@example.com";
                    String telefono = "+54911" + (41000000 + i * 11571);

                    List<MedioDeContactoDTO> contactos = new ArrayList<>();
                    contactos.add(new MedioDeContactoDTO(TipoDeContacto.EMAIL, emailUser));
                    contactos.add(new MedioDeContactoDTO(TipoDeContacto.SMS, telefono));
                    if (i % 3 == 0) {
                        contactos.add(new MedioDeContactoDTO(TipoDeContacto.WHATSAPP, telefono));
                    }

                    Long idUsuarioAuth = (i == 0) ? 2L : null;
                    HumanoCreateRequest request = new HumanoCreateRequest(
                            nombre, apellido, fechaNac, genero, dni, calle, altura, idUsuarioAuth, contactos
                    );
                    DonanteResponse resp = donanteService.agregarDonante(request);

                    // Los últimos 4 humanos (índices 48, 49, 50, 51) se dan de baja
                    if (i >= 48) {
                        donanteService.darDeBajaDonante(resp.id());
                    }

                    if (i == 0) idDonanteJuan = resp.id();
                    if (i == 1) idDonanteFacundo = resp.id();
                }

                // --- B. 8 Donantes Jurídicos (6 activos, 2 dados de baja) ---
                String[][] datosJuridicas = {
                    {"UTN FRBA", "INSTITUCION", "Educación", "contacto@frba.utn.edu.ar", "1144556677", "Ing. Roberto Rossi", "rrossi@frba.utn.edu.ar"},
                    {"Distribuidora Mayorista Los Andes S.A.", "EMPRESA", "Alimentos y Bebidas", "ventas@losandesmayorista.com", "1133221100", "Mariana Gómez", "mgomez@losandes.com"},
                    {"Supermercados El Progreso S.R.L.", "EMPRESA", "Consumo Masivo", "donaciones@elprogreso.com.ar", "1165432198", "Carlos Méndez", "cmendez@elprogreso.com"},
                    {"Tech Solutions Argentina S.A.", "EMPRESA", "Tecnología y Software", "info@techsolutions.com", "1198765432", "Laura Benítez", "lbenitez@techsolutions.com"},
                    {"Fundación Manos Solidarias", "ONG", "Asistencia Social", "contacto@manossolidarias.org", "1155443322", "Esteban Morales", "emorales@manossolidarias.org"},
                    {"Cooperativa Obrera Textil Ltda.", "COOPERATIVA", "Indumentaria y Calzado", "info@cooptextil.org.ar", "1177889900", "Silvia Castro", "scastro@cooptextil.org"},
                    // Las últimas 2 dadas de baja:
                    {"Logística Express del Plata S.A.", "EMPRESA", "Transporte y Logística", "contacto@logisticadelplata.com", "1122334455", "Jorge Valenzuela", "jvalenzuela@delplata.com"},
                    {"Asociación Civil Sembrando Futuro", "ONG", "Desarrollo Comunitario", "admin@sembrandofuturo.org", "1188776655", "Valeria Ramos", "vramos@sembrando.org"}
                };

                for (int j = 0; j < datosJuridicas.length; j++) {
                    String razonSocial = datosJuridicas[j][0];
                    String tipoJuridica = datosJuridicas[j][1];
                    String rubro = datosJuridicas[j][2];
                    String email = datosJuridicas[j][3];
                    String telefono = datosJuridicas[j][4];
                    String nombreRep = datosJuridicas[j][5];
                    String emailRep = datosJuridicas[j][6];

                    List<MedioDeContactoDTO> contactosJur = new ArrayList<>();
                    contactosJur.add(new MedioDeContactoDTO(TipoDeContacto.EMAIL, email));
                    contactosJur.add(new MedioDeContactoDTO(TipoDeContacto.SMS, telefono));

                    List<RepresentanteDTO> reps = new ArrayList<>();
                    reps.add(new RepresentanteDTO(nombreRep, new MedioDeContactoDTO(TipoDeContacto.EMAIL, emailRep)));

                    JuridicoCreateRequest jurReq = new JuridicoCreateRequest(razonSocial, tipoJuridica, rubro, contactosJur, reps);
                    DonanteResponse resp = donanteService.agregarDonante(jurReq);

                    // Las últimas 2 jurídicas (índices 6 y 7) se dan de baja
                    if (j >= 6) {
                        donanteService.darDeBajaDonante(resp.id());
                    }

                    if (j == 0) idDonanteUtn = resp.id();
                }
            } else {
                idDonanteJuan = buscarIdDonantePorNombre(donanteService, "Juan Perez");
                idDonanteUtn = buscarIdDonantePorNombre(donanteService, "UTN FRBA");
                idDonanteFacundo = buscarIdDonantePorNombre(donanteService, "Facundo Test");
            }

            // 2. Cargar Entidades y Necesidades
            if (entidadService.obtenerTodas().isEmpty()) {
                Subcategoria subArroz = subcategoriaRepository.findByNombre("ARROZ").orElse(null);
                Subcategoria subSillas = subcategoriaRepository.findByNombre("SILLAS").orElse(null);
                Subcategoria subCamperas = subcategoriaRepository.findByNombre("CAMPERAS").orElse(null);
                Subcategoria subArrozBlanco = subcategoriaRepository.findByNombre("ARROZ BLANCO").orElse(null);
                Subcategoria subLeche = subcategoriaRepository.findByNombre("LECHE LARGA VIDA").orElse(null);

                List<MedioDeContactoDTO> contactosEntidadTest = new ArrayList<>();
                contactosEntidadTest.add(new MedioDeContactoDTO(TipoDeContacto.SMS, "5491139393202"));

                // Comedor El Sol (idUsuarioAuth = 3L) - Necesita Leche, Arroz y Sillas
                Long idEntidadBajitos = entidadService.crear(new EntidadBeneficiariaRequest("Comedor El Sol", "COMEDOR", "Av. Paseo Colón", 850, contactosEntidadTest, new ArrayList<>(), 3L)).id();
                entidadService.agregarNecesidad(idEntidadBajitos, new NecesidadRequest(subLeche != null ? subLeche.getId() : null, 20.0, "Necesitamos leche para los chicos.", "RECURRENTE", "P1M", LocalDate.now()));
                entidadService.agregarNecesidad(idEntidadBajitos, new NecesidadRequest(subArroz != null ? subArroz.getId() : null, 50.0, "Necesitamos arroz para el mes.", "RECURRENTE", "P1M", LocalDate.now()));
                entidadService.agregarNecesidad(idEntidadBajitos, new NecesidadRequest(subSillas != null ? subSillas.getId() : null, 30.0, "Necesitamos 30 sillas urgente.", "EXTRAORDINARIA", null, null));

                // Merendero Sol Naciente (idUsuarioAuth = 4L) - Necesita Camperas
                Long idEntidadVida = entidadService.crear(new EntidadBeneficiariaRequest("Merendero Sol Naciente", "COMEDOR", "Av. San Martín", 2890, contactosEntidadTest, new ArrayList<>(), 4L)).id();
                entidadService.agregarNecesidad(idEntidadVida, new NecesidadRequest(
                    subCamperas != null ? subCamperas.getId() : null,
                    20.0,
                    "Abrigos y camperas para el invierno.",
                    "EXTRAORDINARIA",
                    null,
                    null
                ));

                List<RepresentanteDTO> representantesTest = new ArrayList<>();
                representantesTest.add(new RepresentanteDTO("Representante Test", new MedioDeContactoDTO(TipoDeContacto.SMS, "5491139393202")));

                Long idEntidadPostman = entidadService.crear(new EntidadBeneficiariaRequest("Entidad de Prueba Postman", "ONG", "Calle de Prueba", 1, contactosEntidadTest, representantesTest, 102L)).id();
                entidadService.agregarNecesidad(idEntidadPostman, new NecesidadRequest(
                    subArrozBlanco != null ? subArrozBlanco.getId() : null,
                    10.0,
                    "Necesitamos arroz para pruebas.",
                    "EXTRAORDINARIA",
                    null,
                    null
                ));
            }

            Long idEntidadBajitosSeed = buscarIdEntidadPorRazonSocial(entidadService, "Comedor El Sol");
            Long idEntidadVidaSeed = buscarIdEntidadPorRazonSocial(entidadService, "Merendero Sol Naciente");

            // Asegurar coordenadas y datos de Comedor El Sol
            Optional<EntidadBeneficiaria> ebBajitosOpt = entidadBeneficiariaRepository.findById(idEntidadBajitosSeed);
            if (ebBajitosOpt.isPresent()) {
                EntidadBeneficiaria ebBajitos = ebBajitosOpt.get();
                if (ebBajitos.getDireccion() != null) {
                    ebBajitos.getDireccion().setCalle("Av. Paseo Colón");
                    ebBajitos.getDireccion().setAltura(850);
                    ebBajitos.getDireccion().setLatitud(-34.6175);
                    ebBajitos.getDireccion().setLongitud(-58.3675);
                }
                ebBajitos.setIdUsuarioAuth(3L);
            }

            // Asegurar coordenadas y datos de Merendero Sol Naciente
            Optional<EntidadBeneficiaria> ebVidaOpt = entidadBeneficiariaRepository.findById(idEntidadVidaSeed);
            if (ebVidaOpt.isPresent()) {
                EntidadBeneficiaria ebVida = ebVidaOpt.get();
                ebVida.setRazonSocial("Merendero Sol Naciente");
                if (ebVida.getDireccion() != null) {
                    ebVida.getDireccion().setCalle("Av. San Martín");
                    ebVida.getDireccion().setAltura(2890);
                    ebVida.getDireccion().setLatitud(-34.6015);
                    ebVida.getDireccion().setLongitud(-58.4520);
                }
                ebVida.setIdUsuarioAuth(4L);
            }

            // 3. Cargar Donaciones en Depósito
            if (donacionRepository.findAll().isEmpty()) {
                Subcategoria subArroz = subcategoriaRepository.findByNombre("ARROZ").orElse(null);
                Subcategoria subArrozBlanco = subcategoriaRepository.findByNombre("ARROZ BLANCO").orElse(null);
                Subcategoria subCamperas = subcategoriaRepository.findByNombre("CAMPERAS").orElse(null);
                Subcategoria subLeche = subcategoriaRepository.findByNombre("LECHE LARGA VIDA").orElse(null);

                // Donación 1: Arroz Blanco (donante Facundo, matchea con entidad Postman)
                List<BienRequest> bienesArroz = new ArrayList<>();
                bienesArroz.add(new BienRequest("Arroz Blanco", "5kg de arroz", 5.0, "KG", subArrozBlanco != null ? subArrozBlanco.getId() : null, "NUEVO", LocalDate.now().plusMonths(6), null));
                donacionService.crearDonacion(new CrearDonacionRequest(idDonanteFacundo, "Donación de Facundo para pruebas", bienesArroz));

                // Donación 2: Camperas (donante Juan Perez, matchea con Merendero Sol Naciente) en DEPOSITO, SIN ASIGNAR
                List<BienRequest> bienesRopa = new ArrayList<>();
                bienesRopa.add(new BienRequest("Camperas", "Camperas de abrigo", 20.0, "UNIDADES", subCamperas != null ? subCamperas.getId() : null, "USADO", null, null));
                donacionService.crearDonacion(new CrearDonacionRequest(idDonanteJuan != null ? idDonanteJuan : 1L, "Ropa de invierno", bienesRopa));

                // Donación 3: Leche Larga Vida (donante Juan Perez, matchea con Comedor El Sol) en DEPOSITO, SIN ASIGNAR
                List<BienRequest> bienesLeche = new ArrayList<>();
                bienesLeche.add(new BienRequest("Leche Larga Vida", "20L de leche larga vida", 20.0, "L", subLeche != null ? subLeche.getId() : null, "NUEVO", LocalDate.now().plusMonths(3), null));
                donacionService.crearDonacion(new CrearDonacionRequest(idDonanteJuan != null ? idDonanteJuan : 1L, "Donación de Leche Larga Vida", bienesLeche));

                // Donación 4: Arroz para el Comedor (donante Juan Perez, en DEPOSITO, SIN ASIGNAR)
                List<BienRequest> bienesArrozComedor = new ArrayList<>();
                bienesArrozComedor.add(new BienRequest("Arroz", "30kg de arroz largo fino", 30.0, "KG", subArroz != null ? subArroz.getId() : null, "NUEVO", LocalDate.now().plusMonths(6), null));
                donacionService.crearDonacion(new CrearDonacionRequest(idDonanteJuan != null ? idDonanteJuan : 1L, "Donación de Arroz para Comedor", bienesArrozComedor));
            } else {
                // Si ya existen donaciones pero falta la de Arroz en DEPOSITO para el admin
                boolean existeArrozEnDeposito = donacionRepository.findAll().stream()
                        .anyMatch(d -> d.getDescripcionGeneral() != null && d.getDescripcionGeneral().contains("Donación de Arroz para Comedor"));
                if (!existeArrozEnDeposito) {
                    Subcategoria subArroz = subcategoriaRepository.findByNombre("ARROZ").orElse(null);
                    List<BienRequest> bienesArrozComedor = new ArrayList<>();
                    bienesArrozComedor.add(new BienRequest("Arroz", "30kg de arroz largo fino", 30.0, "KG", subArroz != null ? subArroz.getId() : null, "NUEVO", LocalDate.now().plusMonths(6), null));
                    donacionService.crearDonacion(new CrearDonacionRequest(idDonanteJuan != null ? idDonanteJuan : 1L, "Donación de Arroz para Comedor", bienesArrozComedor));
                }
            }

            // 4. FORZAR MATCHMAKING INICIAL
            System.out.println("Ejecutando Matchmaking inicial para precargar sugerencias...");
            donacionService.realizarMatcheoDonaciones();

            System.out.println("Data Seeding completed: Categories, Subcategories, Donors, Entities, Donations and SugerenciasMatch added.");
        });
    }

    private Long buscarIdDonantePorNombre(DonanteService donanteService, String nombre) {
        return donanteService.obtenerTodos().stream()
                .filter(d -> nombre.equals(d.nombre()))
                .map(DonanteResponse::id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed: no se encontró donante '" + nombre + "'"));
    }

    private Long buscarIdEntidadPorRazonSocial(EntidadBeneficiariaService entidadService, String razonSocial) {
        return entidadService.obtenerTodas().stream()
                .filter(e -> razonSocial.equalsIgnoreCase(e.razonSocial()))
                .map(EntidadBeneficiariaResponse::id)
                .findFirst()
                .orElse(1L);
    }
}
