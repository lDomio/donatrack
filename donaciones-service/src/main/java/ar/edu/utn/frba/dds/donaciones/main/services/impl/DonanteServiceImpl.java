package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.BusinessException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donacion.CrearDonacionRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteCreateRequest;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.*;
import ar.edu.utn.frba.dds.donaciones.main.dtos.notificacion.MedioDeContactoDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.representante.RepresentanteDTO;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.NotificacionClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.*;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.localizacionGeograficaDeEntidades.Direccion;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.DonanteService;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DonanteServiceImpl implements DonanteService {

    private final DonanteRepository donanteRepository;
    private final DonacionRepository donacionRepository;
    private final NotificacionClient notificacionClient;
    private final IncentivosClient incentivosClient;

    public DonanteServiceImpl(DonanteRepository donanteRepository, DonacionRepository donacionRepository, NotificacionClient notificacionClient, IncentivosClient incentivosClient) {
        this.donanteRepository = donanteRepository;
        this.donacionRepository = donacionRepository;
        this.notificacionClient = notificacionClient;
        this.incentivosClient = incentivosClient;
    }

    //------------------ VALIDACIONES DE AUTORIZACIÓN (Incoming) ------------------
    @Override
    public Boolean validarUsuarioDeDonante(Long idDonante, Long idUsuarioAuth) {
        Donante donante = donanteRepository.findById(idDonante)
                .orElseThrow(() -> new ResourceNotFoundException("No existe donante con ese id"));
        return Objects.equals(donante.getIdUsuarioAuth(), idUsuarioAuth);
    }

    @Override
    public Boolean validarDonacionRequestDeDonante(CrearDonacionRequest donacionRequest, Long idUsuarioAuth) {
        Donante donante = donanteRepository.findById(donacionRequest.idDonante())
                .orElseThrow(() -> new ResourceNotFoundException("El donante no existe"));
        return Objects.equals(donante.getIdUsuarioAuth(), idUsuarioAuth);
    }
    //------------------------------------------------------------------------------

    //----------------------- GET Y FILTROS (Entrega 5) ----------------------------
    @Override
    public DonanteResponse buscarPorIdUsuarioAuth(Long idUsuarioAuth) {
        Donante donante = donanteRepository.findByIdUsuarioAuthActivo(idUsuarioAuth)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró donante activo con idUsuarioAuth " + idUsuarioAuth));
        return this.mapear(donante);
    }

    public List<DonanteResponse> obtenerTodos() {
        return donanteRepository.findAllActivos().stream().map(this::mapear).collect(Collectors.toList());
    }

    public List<DonanteResponse> obtenerTodosIncluyendoInactivos(String buscar, String tipo, String estado) {
        return donanteRepository.findAll().stream()
                .filter(donante -> {
                    if (estado != null && !estado.isEmpty() && !estado.equals("todos")) {
                        if (estado.equals("alta") && !donante.getEstaActivo()) return false;
                        if (estado.equals("baja") && donante.getEstaActivo()) return false;
                    }
                    if (tipo != null && !tipo.isEmpty() && !tipo.equals("todos")) {
                        if (tipo.equals("humana") && !(donante instanceof Humana)) return false;
                        if (tipo.equals("juridica") && !(donante instanceof Juridica)) return false;
                    }
                    if (buscar != null && !buscar.trim().isEmpty()) {
                        String busqueda = buscar.toLowerCase().trim();
                        String nombreCompleto = obtenerNombreDonante(donante).toLowerCase();
                        if (!nombreCompleto.contains(busqueda)) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    private DonanteResponse mapear(Donante donante) {
        String nombre;
        String tipo;
        List<RepresentanteDTO> representantes = List.of();
        if (donante instanceof Humana h) {
            nombre = h.getNombre() + " " + h.getApellido();
            tipo = "HUMANA";
        } else if (donante instanceof Juridica j) {
            nombre = j.getRazonSocial();
            tipo = "JURIDICA";
            if (j.getRepresentantes() != null) {
                representantes = j.getRepresentantes().stream()
                        .map(this::mapearRepresentante)
                        .toList();
            }
        } else {
            nombre = "Sin nombre";
            tipo = "DESCONOCIDO";
        }

        return new DonanteResponse(
                donante.getId(),
                nombre,
                donante.getContactos() == null ? List.of() : donante.getContactos().stream()
                        .filter(Objects::nonNull)
                        .map(contacto -> new MedioDeContactoDTO(contacto.getTipoDeContacto(), contacto.getValorDelContacto(), contacto.getEsPreferido()))
                        .toList(),
                tipo,
                donante.getEstaActivo(),
                representantes
        );
    }

    private RepresentanteDTO mapearRepresentante(Representante rep) {
        List<MedioDeContactoDTO> contactosDto = rep.getContactos() == null ? List.of() : rep.getContactos().stream()
                .filter(Objects::nonNull)
                .map(c -> new MedioDeContactoDTO(c.getTipoDeContacto(), c.getValorDelContacto(), c.getEsPreferido()))
                .toList();
        return new RepresentanteDTO(rep.getId_representante(), rep.getNombre(), contactosDto);
    }

    @Override
    public DonanteResponse buscarPorId(Long id) {
        Donante donante = this.obtenerDonanteActivo(id);
        return this.mapear(donante);
    }

    public Donante obtenerDonanteActivo(Long id) {
        return donanteRepository.findByIdActivo(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró donante activo con id " + id));
    }

    public Donante obtenerDonante(Long id) {
        return donanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró donante con id " + id));
    }
    //------------------------------------------------------------------------------

    //------------ CREACIÓN (Arquitectura Entrega 5 + Detalles Incoming) -----------
    @Override
    @Transactional
    public DonanteResponse agregarDonante(DonanteCreateRequest request) {
        Donante donante;
        if (request instanceof HumanoCreateRequest humanoReq) {
            donante = this.crearHumano(humanoReq);
            donante.setIdUsuarioAuth(humanoReq.idUsuarioAuth()); // Restaurado de incoming
            this.agregarContactos(donante, humanoReq.contactos());
        } else if (request instanceof JuridicoCreateRequest juridicoReq) {
            donante = this.crearJuridica(juridicoReq);
            // Si JuridicoCreateRequest llegara a tener idUsuarioAuth, deberías setearlo aquí también
            this.agregarContactos(donante, juridicoReq.contactos());
        } else {
            throw new IllegalArgumentException("Tipo de donante desconocido: " + request.getClass());
        }
        this.donanteRepository.save(donante);
        DonanteResponse donanteResponse = this.mapear(donante);
        this.incentivosClient.agregarDonante(donanteResponse, donante.getIdUsuarioAuth());
        return donanteResponse;
    }

    private Juridica crearJuridica(JuridicoCreateRequest juridicaRequest) {
        Juridica juridica = new Juridica();
        juridica.setRazonSocial(juridicaRequest.razonSocial());
        juridica.setRubro(juridicaRequest.rubro());
        if (juridicaRequest.tipoJuridica() != null) {
            TipoPersonaJuridica tipo = new TipoPersonaJuridica();
            tipo.setTipo(juridicaRequest.tipoJuridica());
            juridica.setTipoJuridica(tipo);
        }
        this.agregarRepresentantes(juridica, juridicaRequest.representantes());
        return juridica;
    }

    private Humana crearHumano(HumanoCreateRequest humanoRequest) {
        Humana humana = new Humana();
        humana.setNombre(humanoRequest.nombre());
        humana.setApellido(humanoRequest.apellido());
        humana.setNumeroDeDocumento(humanoRequest.numeroDeDocumento());
        humana.setGenero(humanoRequest.genero());
        if (humanoRequest.fechaDeNacimiento() != null) {
            humana.setFechaDeNacimiento(humanoRequest.fechaDeNacimiento());
        }

        // Direccion restaurada de la rama incoming
        if (humanoRequest.calle() != null || humanoRequest.altura() != null) {
            Direccion direccion = new Direccion();
            direccion.setCalle(humanoRequest.calle());
            direccion.setAltura(humanoRequest.altura());
            humana.setDireccion(direccion);
        }
        return humana;
    }

    private void agregarContactos(Donante donante, List<MedioDeContactoDTO> contactosDTO) {
        if (contactosDTO != null) {
            contactosDTO.forEach(dto -> {
                // Validación esPreferido restaurada de la rama incoming
                boolean preferido = Boolean.TRUE.equals(dto.esPreferido());
                MedioDeContacto medio = new MedioDeContacto(dto.tipo(), dto.valor(), preferido);
                donante.agregarContacto(medio);
            });
        }
    }

    private void agregarRepresentantes(Juridica juridica, List<RepresentanteDTO> representantesDTO) {
        if (representantesDTO != null) {
            representantesDTO.forEach(representanteDTO -> {
                Representante rep = construirRepresentanteDesdeDto(representanteDTO);
                juridica.agregarRepresentante(rep);
            });
        }
    }

    private Representante construirRepresentanteDesdeDto(RepresentanteDTO representanteDTO) {
        List<MedioDeContacto> medios = extraerMediosDeContacto(representanteDTO);
        return new Representante(representanteDTO.nombre(), medios);
    }

    private List<MedioDeContacto> extraerMediosDeContacto(RepresentanteDTO representanteDTO) {
        List<MedioDeContacto> medios = new ArrayList<>();
        if (representanteDTO.contactos() != null && !representanteDTO.contactos().isEmpty()) {
            for (int i = 0; i < representanteDTO.contactos().size(); i++) {
                MedioDeContactoDTO c = representanteDTO.contactos().get(i);
                if (c != null && c.tipo() != null && c.valor() != null && !c.valor().isBlank()) {
                    boolean pref = c.esPreferido() != null ? c.esPreferido() : (i == 0);
                    medios.add(new MedioDeContacto(c.tipo(), c.valor().trim(), pref));
                }
            }
        } else if (representanteDTO.contacto() != null && representanteDTO.contacto().tipo() != null
                && representanteDTO.contacto().valor() != null && !representanteDTO.contacto().valor().isBlank()) {
            medios.add(new MedioDeContacto(representanteDTO.contacto().tipo(), representanteDTO.contacto().valor().trim(), true));
        }
        return medios;
    }

    //------------------ GESTIÓN DE REPRESENTANTES (Persona Jurídica) --------------
    private Juridica obtenerDonanteJuridica(Long idDonante) {
        Donante donante = this.obtenerDonante(idDonante);
        if (!(donante instanceof Juridica juridica)) {
            throw new BusinessException("El donante con id " + idDonante + " no es una persona jurídica");
        }
        return juridica;
    }

    @Override
    @Transactional
    public DonanteResponse agregarRepresentante(Long idDonante, RepresentanteDTO representanteDTO) {
        if (representanteDTO == null || representanteDTO.nombre() == null || representanteDTO.nombre().isBlank()) {
            throw new BusinessException("El nombre del representante es obligatorio");
        }
        Juridica juridica = this.obtenerDonanteJuridica(idDonante);
        Representante nuevo = construirRepresentanteDesdeDto(representanteDTO);
        juridica.agregarRepresentante(nuevo);
        this.donanteRepository.save(juridica);
        return this.mapear(juridica);
    }

    @Override
    @Transactional
    public DonanteResponse editarRepresentante(Long idDonante, Long idRepresentante, RepresentanteDTO representanteDTO) {
        if (representanteDTO == null || representanteDTO.nombre() == null || representanteDTO.nombre().isBlank()) {
            throw new BusinessException("El nombre del representante es obligatorio");
        }
        Juridica juridica = this.obtenerDonanteJuridica(idDonante);
        Representante representante = juridica.getRepresentantes().stream()
                .filter(r -> Objects.equals(r.getId_representante(), idRepresentante))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el representante con id " + idRepresentante + " en la persona jurídica " + idDonante));

        representante.setNombre(representanteDTO.nombre().trim());
        List<MedioDeContacto> nuevosContactos = extraerMediosDeContacto(representanteDTO);
        representante.getContactos().clear();
        representante.getContactos().addAll(nuevosContactos);

        this.donanteRepository.save(juridica);
        return this.mapear(juridica);
    }

    @Override
    @Transactional
    public DonanteResponse eliminarRepresentante(Long idDonante, Long idRepresentante) {
        Juridica juridica = this.obtenerDonanteJuridica(idDonante);
        boolean eliminado = juridica.getRepresentantes()
                .removeIf(r -> Objects.equals(r.getId_representante(), idRepresentante));
        if (!eliminado) {
            throw new ResourceNotFoundException(
                    "No se encontró el representante con id " + idRepresentante + " en la persona jurídica " + idDonante);
        }
        this.donanteRepository.save(juridica);
        return this.mapear(juridica);
    }
    //------------------------------------------------------------------------------

  //-----------------------------------------Delete de un donante--------------------------------------------------------
  @Override
  @Transactional
  public void eliminarDonante(Long id){
    this.donanteRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró donante con id " + id));
    this.donanteRepository.deleteById(id);
  }
  //-----------------------------------------Delete de un donante--------------------------------------------------------

    @Override
    @Transactional
    public void darDeBajaDonante(Long id) {
        Donante donante = this.obtenerDonante(id);
        donante.setEstaActivo(false);
        this.donanteRepository.save(donante);
    }

    @Override
    @Transactional
    public void darDeAltaDonante(Long id) {
        Donante donante = this.obtenerDonante(id);
        donante.setEstaActivo(true);
        this.donanteRepository.save(donante);
    }

    @Override
    public void verificarInactividadDonantes() {
        List<Donante> donantes = donanteRepository.findAllActivos();

        for (Donante donante : donantes) {
            List<Donacion> donacionesDelDonante = donacionRepository.findByIdDonante(donante.getId());

            Optional<Donacion> ultimaDonacion = donacionesDelDonante.stream()
                    .max(Comparator.comparing(Donacion::getFechaDeCreacion));

            if (ultimaDonacion.isPresent()) {
                long diasInactividad = ChronoUnit.DAYS.between(ultimaDonacion.get().getFechaDeCreacion(), LocalDate.now());
                if (diasInactividad > 20) {
                    String nombre = obtenerNombreDonante(donante);
                    List<MedioDeContactoDto> contactosMapeados = donante.getContactos().stream()
                            .map(contacto -> new MedioDeContactoDto(contacto.getValorDelContacto(), contacto.getTipoDeContacto().name()))
                            .collect(Collectors.toList());

                    notificacionClient.notificarInactividad(nombre, contactosMapeados);
                }
            }
        }
    }

    @Override
    public MedioDeContacto buscarMedioDeContactoPorId(Long usuariosId) {
        Donante donante = this.obtenerDonanteActivo(usuariosId);
        return donante.getContactos().stream()
                .filter(MedioDeContacto::getEsPreferido)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El donante con id " + usuariosId + " no tiene un medio de contacto preferido"));
    }

    private String obtenerNombreDonante(Donante donante) {
        if (donante instanceof Humana h) {
            return h.getNombre() + " " + h.getApellido();
        } else if (donante instanceof Juridica j) {
            return j.getRazonSocial();
        }
        return "Donante";
    }
}
