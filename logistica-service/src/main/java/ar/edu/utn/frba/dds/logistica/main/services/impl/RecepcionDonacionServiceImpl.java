package ar.edu.utn.frba.dds.logistica.main.services.impl;

import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionAsignadaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.PesajeDonacionRequest;
import ar.edu.utn.frba.dds.logistica.main.exceptions.BusinessException;
import ar.edu.utn.frba.dds.logistica.main.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.actores.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.TipoDeEstadoDonacion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.Entrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.EstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.entrega.TipoDeEstadoEntrega;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.EstadoDeLaRuta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Parada;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.Ruta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte.TipoDeEstadoRuta;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Ciudad;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Pais;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.ubicacion.Provincia;
import ar.edu.utn.frba.dds.logistica.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.EntregaRepository;
import ar.edu.utn.frba.dds.logistica.main.repositories.RutaRepository;
import ar.edu.utn.frba.dds.logistica.main.services.RecepcionDonacionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class RecepcionDonacionServiceImpl implements RecepcionDonacionService {
  private final DonacionRepository donacionRepository;
  private final EntidadBeneficiariaRepository entidadRepository;
  private final EntregaRepository entregaRepository;
  private final RutaRepository rutaRepository;

  public RecepcionDonacionServiceImpl(DonacionRepository donacionRepository,
                                      EntidadBeneficiariaRepository entidadRepository,
                                      EntregaRepository entregaRepository,
                                      RutaRepository rutaRepository) {
    this.donacionRepository = donacionRepository;
    this.entidadRepository = entidadRepository;
    this.entregaRepository = entregaRepository;
    this.rutaRepository = rutaRepository;
  }

  @Override
  public void procesarAsignacion(DonacionAsignadaRequest request) {
    // 1. Aseguramos que la entidad exista
    EntidadBeneficiaria entidad = entidadRepository.findByIdEntidadDonacionesService(request.getEntidadId())
        .or(() -> entidadRepository.findById(request.getEntidadId()))
        .orElseGet(() -> {
          EntidadBeneficiaria nueva = new EntidadBeneficiaria();
          nueva.setId(request.getEntidadId());
          nueva.setIdEntidadDonacionesService(request.getEntidadId());
          nueva.setRazonSocial(request.getRazonSocialEntidad());
          Double lat = request.getLatitudDestino() != null ? request.getLatitudDestino() : -34.6175;
          Double lon = request.getLongitudDestino() != null ? request.getLongitudDestino() : -58.3675;
          nueva.setDireccion(new Direccion(request.getDireccionEntidad(), 0, new Ciudad("CABA", new Provincia("CABA", new Pais("Argentina", "AR"))), lat, lon));
          return nueva;
        });

    // 2. Creamos la donación local en estado PENDIENTE_DE_PESAJE a la espera de balanza/cubicaje
    Donacion donacion = new Donacion();
    donacion.setId(request.getDonacionId());
    donacion.setDonanteId(request.getDonanteId());
    donacion.setSubcategoria(request.getSubcategoria());
    donacion.setCantidad(request.getCantidad());
    donacion.setFechaVencimiento(request.getFechaVencimiento());
    donacion.setEntidadId(request.getEntidadId());
    donacion.setEstado(TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE);
    donacion.setPesoKg(null);
    donacion.setVolumen(null);

    donacionRepository.guardar(donacion);
    entidad.removerDonacionAsignada(donacion.getId());
    entidad.agregarDonacionAsignada(donacion);
    entidadRepository.guardar(entidad);

        /*
        // 3. Creamos la Entrega (PENDIENTE)
        Entrega entrega = new Entrega();
        entrega.setDireccionEntidad(request.getDireccionEntidad());
        entrega.setRazonSocialEntidadBeneficiaria(request.getRazonSocialEntidad());
        entrega.setLatitud(request.getLatitudDestino());
        entrega.setLongitud(request.getLongitudDestino());
        entrega.setFueRecibidoPorLaEntidad(false);
        entrega.agregarDonacion(donacion);
        entrega.agregarEstado(new EstadoEntrega("Entrega creada por asignación de donación", LocalDate.now(), TipoDeEstadoEntrega.PENDIENTE));

        Long idEntrega = entregaRepository.guardar(entrega);

        // --- MOCK DE PRUEBA (DESCOMENTAR PARA TESTEAR FLUJO COMPLETO SIN PLANIFICADOR) ---
        Ruta rutaFicticia = new Ruta();
        rutaFicticia.setId(999L);
        rutaFicticia.setDistanciaARecorrer(10.0);
        rutaFicticia.setFechaDeComienzoDeViaje(LocalDateTime.now().plusHours(1));
        rutaFicticia.setFechaFinDeViaje(LocalDateTime.now().plusHours(3));
        rutaFicticia.agregarEstado(new EstadoDeLaRuta(LocalDate.now(), TipoDeEstadoRuta.RUTA_ASIGNADA));

        Parada paradaUnica = new Parada();
        paradaUnica.setNumeroDeParada(1);
        paradaUnica.setFechaEntrega(LocalDateTime.now().plusHours(2));
        paradaUnica.setEntrega(entrega);
        rutaFicticia.agregarParada(paradaUnica);

        rutaRepository.guardar(rutaFicticia);
        System.out.println("MOCK ACTIVADO: Se creó automáticamente la Ruta 999");

        */
    System.out.println("Logistica: Donacion " + donacion.getId() + " encolada");
  }

  @Override
  public DonacionResponse cargarPesaje(PesajeDonacionRequest request, Long id){
    Donacion donacion = donacionRepository.findById(id).orElseGet(() -> {
      Donacion nueva = new Donacion();
      nueva.setId(id);
      nueva.setEstado(TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE);
      return nueva;
    });

    donacion.setPesoKg(request.peso());
    donacion.setVolumen(request.volumen());
    if (donacion.getEstado() == null || donacion.getEstado() == TipoDeEstadoDonacion.PENDIENTE_DE_PESAJE) {
      donacion.setEstado(TipoDeEstadoDonacion.LISTA_PARA_PLANIFICAR);
    }
    donacionRepository.guardar(donacion);

    return this.donacionResponse(donacion);
  }

  private DonacionResponse donacionResponse(Donacion donacion) {
    return new DonacionResponse(donacion.getId(), donacion.getSubcategoria(), donacion.getPesoKg(), donacion.getVolumen());
  }
}
