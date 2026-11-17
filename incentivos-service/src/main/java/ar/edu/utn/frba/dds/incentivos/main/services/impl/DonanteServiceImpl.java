package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.common.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.dds.incentivos.main.dtos.*;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.*;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteObserverFactory;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import ar.edu.utn.frba.dds.incentivos.main.services.MisionService;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionDonacionesExitosas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DonanteServiceImpl implements DonanteService {
    private final DonanteRepository donanteRepository;
    private final DonanteObserverFactory observerFactory;
    private final MisionService misionService;

    public DonanteServiceImpl(DonanteRepository donanteRepository, DonanteObserverFactory observerFactory, MisionService misionService) {
        this.donanteRepository = donanteRepository;
        this.observerFactory = observerFactory;
        this.misionService = misionService;
    }

    @Override
    @Transactional
    public DonanteResponse agregarDonante(CrearDonanteRequest donanteRequest) {
        try {
            Optional<Donante> existing = this.donanteRepository.findByIdDonanteDonacionesService(donanteRequest.id())
                    .or(() -> this.donanteRepository.findById(donanteRequest.id()));
            if(existing.isPresent()){
                existing.get().setIdUsuarioAuth(donanteRequest.idUsuarioAuth());
                this.donanteRepository.save(existing.get());
                return mapToResponse(existing.get());
            }

            List<MedioDeContacto> contactos = null;
            if (donanteRequest.contactos() != null) {
                contactos = donanteRequest.contactos().stream()
                        .map(c -> {
                            TipoDeContacto tipo = null;
                            if (c.tipo() != null) {
                                try {
                                    tipo = TipoDeContacto.valueOf(c.tipo().toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Tipo de contacto invalido: " + c.tipo());
                                }
                            }
                            return new MedioDeContacto(tipo, c.valor());
                        })
                        .toList();
            }

            Donante donante = new Donante(
                observerFactory,
                donanteRequest.id(),
                misionService.obtenerPrimeraMision().orElse(null),
                contactos,
                donanteRequest.nombreUsuario(),
                donanteRequest.idUsuarioAuth()
            );

            this.donanteRepository.save(donante);
            return mapToResponse(donante);
        } catch (Exception ex) {
            System.err.println("Error en agregarDonante: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public Optional<Donante> findById(Long id) {
        if (id == null) return Optional.empty();
        return this.donanteRepository.findById(id)
                .or(() -> this.donanteRepository.findByIdDonanteDonacionesService(id));
    }

    @Override
    public Optional<Donante> findByIdDonanteDonacionesService(Long idDonanteDonacionesService) {
        return this.donanteRepository.findByIdDonanteDonacionesService(idDonanteDonacionesService);
    }

    @Override
    public Boolean validarUsuarioDeDonante(Long idDonante, Long idUsuarioAuth) {
        Donante donante = buscarPorCualquierId(idDonante)
            .orElseThrow(() -> new ResourceNotFoundException("No existe donante con ese id"));
        return idUsuarioAuth != null && Objects.equals(donante.getIdUsuarioAuth(), idUsuarioAuth);
    }

    @Override
    public Optional<Donante> buscarPorCualquierId(Long id) {
        return findById(id);
    }

    @Override
    @Transactional
    public Donante guardarDonante(Donante donante) {
        this.donanteRepository.save(donante);
        return donante;
    }

    @Override
    public List<Donante> findAll() {
        return this.donanteRepository.findAll();
    }

    private DonanteResponse mapToResponse(Donante donante) {
        if (donante == null) {
            return null;
        }
        return new DonanteResponse(
            donante.getId(),
            donante.getIdDonanteDonacionesService(),
            donante.getDonaciones() != null ? donante.getDonaciones().stream().map(this::mapDonacion).toList() : List.of(),
            mapCategoria(donante.getCategoria()),
            donante.getInsignias().stream().map(this::mapInsignia).toList(),
            donante.getMisionesCompletadas() != null ? donante.getMisionesCompletadas().stream().map(MisionResponse::from).toList() : List.of(),
            mapMision(donante.getMisionActual()),
            donante.getCategoriasCompletadas() != null ? donante.getCategoriasCompletadas().stream().map(this::mapCategoriaCompletada).toList() : List.of(),
            mapProgresoMision(donante.getProgresoMisionActual()),
            donante.getMedioContacto() != null ? donante.getMedioContacto().stream().map(this::mapMedioContacto).toList() : List.of(),
            donante.getFechaDelInicioMisionActual(),
            donante.getTotalEntidadesBeneficiariasQueRecibieron()
        );
    }

    private DonacionDto mapDonacion(Donacion d) {
        if (d == null) return null;
        return new DonacionDto(
                d.getId(),
                d.getFechaDeCreacion(),
                d.getCantidadDeBien(),
                d.getCategoria(),
                d.getFechaDeRecepcion(),
                d.getFueRecibida(),
                d.getEntidadBeneficiariaId()
        );
    }

    private CategoriaDonanteDto mapCategoria(CategoriaDonante c) {
        if (c == null) return null;
        return new CategoriaDonanteDto(c.getNombre(), c.getCantidadDeMisionesACompletar());
    }

    private MisionDto mapMision(Mision m) {
        if (m == null) return null;
        return new MisionDto(m.getNombre(), m.getInsignia() != null ? InsigniaResponse.from(m.getInsignia()) : null);
    }

    private CategoriaCompletadaDto mapCategoriaCompletada(CategoriaCompletada cc) {
        if (cc == null) return null;
        return new CategoriaCompletadaDto(mapCategoria(cc.getCategoria()), cc.getFechaDeComplecion());
    }

    private MedioDeContactoDto mapMedioContacto(MedioDeContacto mc) {
        if (mc == null) return null;
        return new MedioDeContactoDto(mc.getTipo() != null ? mc.getTipo().name() : null, mc.getValor());
    }

    private InsigniaResponse mapInsignia(Insignia insignia) {
        if (insignia == null) return null;
        return new InsigniaResponse(insignia.getDescripcion());
    }

    private ProgresoMisionDto mapProgresoMision(ProgresoMision progresoMision) {
        if (progresoMision == null) return null;
        return new ProgresoMisionDto(progresoMision.getProgreso(), progresoMision.getCantidadActual(), progresoMision.getCantidadObjetivo());
    }

}
