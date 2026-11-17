package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.DonacionService;
import ar.edu.utn.frba.dds.incentivos.main.services.DonanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DonacionServiceImpl implements DonacionService {
    private final DonacionRepository donacionRepository;
    private final DonanteService donanteService;

    public DonacionServiceImpl(DonacionRepository donacionRepository, DonanteService donanteService) {
        this.donacionRepository = donacionRepository;
        this.donanteService = donanteService;
    }

    @Override
    @Transactional
    public Donacion agregarDonacion(CrearDonacionRequest donacionReq) {
        Donante donante = donanteService.findById(donacionReq.idDonante())
                .orElseThrow(() -> new RuntimeException("Donante "+donacionReq.idDonante() +" no encontrado en incentivos"));

        Donacion donacion = new Donacion(
            donacionReq.fechaCreacion(),
            donacionReq.CantidadDeBien(),
            donacionReq.categoria(),
            donacionReq.idDonacion(),
            donante,
            (donacionReq.entidadBeneficiaria() != null ? donacionReq.entidadBeneficiaria().toString():null)
        );

        donante.registrarDonacion(donacion);
        donante.completarMisionSiCompletadaYGenerarNotificaciones();

        donacionRepository.save(donacion);
        donanteService.guardarDonante(donante);
        return donacion;
    }

    @Override
    @Transactional
    public Donacion marcarDonacionComoRecibida(Long idDonacion, String entidadId) {
        try {
            Donacion donacionFromDb = this.donacionRepository.findByIdDonacionDonacionesService(idDonacion)
                    .orElseGet(() -> this.donacionRepository.findById(idDonacion)
                            .orElseThrow(() -> new RuntimeException("Donacion no encontrada con ID: " + idDonacion)));
            donacionFromDb.marcarComoRecibida(entidadId);

            Donante donante = donacionFromDb.getDonante();
            if (donante != null) {
                donante.completarMisionSiCompletadaYGenerarNotificaciones();
                this.donanteService.guardarDonante(donante);
            }

            this.donacionRepository.save(donacionFromDb);
            return donacionFromDb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Donacion findById(Long idDonacion) throws Exception {
        return this.donacionRepository.findById(idDonacion)
                .or(() -> this.donacionRepository.findByIdDonacionDonacionesService(idDonacion))
                .orElseThrow();
    }

    @Override
    public List<Donacion> findAllByUserId(Long idDonante) {
        return this.donacionRepository.findAllByDonante_Id(idDonante);
    }

    @Override
    public List<Donacion> findAll() {
        return this.donacionRepository.findAll();
    }
}
