package ar.edu.utn.frba.dds.logistica.main.config;

import ar.edu.utn.frba.dds.logistica.main.dtos.camion.CamionRequest;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.MedioDeContacto;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.TipoDeContacto;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.DonacionEnEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.TipoDeEstadoDonacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.EstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Camion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.EstadoDeLaRuta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.TipoDeEstadoRuta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Ciudad;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Pais;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Provincia;
import ar.edu.utn.frba.dds.logistica.main.repositories.CamionRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntregaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.RutaRepository;
import ar.edu.utn.frba.dds.logistica.main.services.CamionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

        private final CamionService camionService;
        private final CamionRepository camionRepository;
        private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;
        private final DonacionRepository donacionRepository;
        private final EntregaRepository entregaRepository;
        private final RutaRepository rutaRepository;

        public DataInitializer(CamionService camionService,
                               CamionRepository camionRepository,
                               EntidadBeneficiariaRepository entidadBeneficiariaRepository,
                               DonacionRepository donacionRepository,
                               EntregaRepository entregaRepository,
                               RutaRepository rutaRepository) {
                this.camionService = camionService;
                this.camionRepository = camionRepository;
                this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
                this.donacionRepository = donacionRepository;
                this.entregaRepository = entregaRepository;
                this.rutaRepository = rutaRepository;
        }

        @Override
        public void run(String... args) throws Exception {
                System.out.println("Iniciando DataInitializer de Logística...");

                // 1. CAMIONES (Solo ABC-123 asignado a chofer_juan idUsuario=5 para que el planificador le asigne la ruta generada)
                List<Camion> camionesExistentes = camionRepository.findAll();
                Camion camionAbc = camionesExistentes.stream().filter(c -> "ABC-123".equalsIgnoreCase(c.getPatente())).findFirst().orElse(null);
                if (camionAbc == null) {
                        System.out.println("Cargando camión ABC-123...");
                        camionService.agregarCamion(new CamionRequest(
                                        "ABC-123",
                                        5000.0,
                                        20.0,
                                        3.5,
                                        "Juan Pérez",
                                        "Av. Medrano 951, CABA",
                                        -34.5985,
                                        -58.4201,
                                        5L));
                } else if (camionAbc.getIdUsuario() == null || camionAbc.getIdUsuario() == 4L) {
                        System.out.println("Asignando idUsuario 5 al camión ABC-123...");
                        camionAbc.setIdUsuario(5L);
                        camionRepository.guardar(camionAbc);
                }

                Camion camionDef = camionesExistentes.stream().filter(c -> "DEF-456".equalsIgnoreCase(c.getPatente())).findFirst().orElse(null);
                if (camionDef == null) {
                        System.out.println("Cargando camión DEF-456 (asignado a chofer_ana idUsuario=6)...");
                        camionService.agregarCamion(new CamionRequest(
                                        "DEF-456",
                                        1500.0,
                                        8.0,
                                        2.2,
                                        "Ana García",
                                        "Av. Medrano 951, CABA",
                                        -34.5985,
                                        -58.4201,
                                        6L));
                } else if (camionDef.getIdUsuario() == null || camionDef.getIdUsuario() != 6L) {
                        System.out.println("Asignando idUsuario 6 al camión DEF-456...");
                        camionDef.setIdUsuario(6L);
                        camionRepository.guardar(camionDef);
                }

                // 2. ENTIDADES BENEFICIARIAS
                if (entidadBeneficiariaRepository.findAll().isEmpty()) {
                        System.out.println("Cargando entidades beneficiarias iniciales...");
                        Pais argentina = new Pais("Argentina", "AR");
                        Provincia caba = new Provincia("Ciudad Autónoma de Buenos Aires", argentina);

                        Ciudad ciudadCaba = new Ciudad("CABA", caba);

                        EntidadBeneficiaria comedorLosBajitos = new EntidadBeneficiaria();
                        comedorLosBajitos.setId(1L);
                        comedorLosBajitos.setIdEntidadDonacionesService(1L);
                        comedorLosBajitos.setRazonSocial("Comedor El Sol");
                        Direccion direccionBajitos = new Direccion("Av. Paseo Colón", 850, ciudadCaba, -34.6175, -58.3675);
                        comedorLosBajitos.setDireccion(direccionBajitos);
                        comedorLosBajitos.agregarContacto(new MedioDeContacto("contacto@losbajitos.org", TipoDeContacto.EMAIL));
                        comedorLosBajitos.agregarContacto(new MedioDeContacto("+5491139393202", TipoDeContacto.WHATSAPP));

                        EntidadBeneficiaria merenderoSolNaciente = new EntidadBeneficiaria();
                        merenderoSolNaciente.setId(2L);
                        merenderoSolNaciente.setIdEntidadDonacionesService(2L);
                        merenderoSolNaciente.setRazonSocial("Merendero Sol Naciente");
                        Direccion direccionSolNaciente = new Direccion("Av. San Martín", 2890, ciudadCaba, -34.6015, -58.4520);
                        merenderoSolNaciente.setDireccion(direccionSolNaciente);
                        merenderoSolNaciente.agregarContacto(new MedioDeContacto("1144445555", TipoDeContacto.SMS));

                        entidadBeneficiariaRepository.guardar(comedorLosBajitos);
                        entidadBeneficiariaRepository.guardar(merenderoSolNaciente);
                        System.out.println("2 Entidades beneficiarias creadas (Comedor El Sol y Merendero Sol Naciente).");
                } else {
                        System.out.println("Entidades beneficiarias ya existentes.");
                }

                /*
                 * 3, 4 y 5. DONACIONES, ENTREGAS Y RUTAS PRECARGADAS COMENTADAS
                 * para permitir simular de punta a punta la asignación, el pesaje y la generación dinámica de la entrega y ruta.
                 */
                System.out.println("DataInitializer completado (sin entregas ni rutas pre-armadas).");
        }
}
