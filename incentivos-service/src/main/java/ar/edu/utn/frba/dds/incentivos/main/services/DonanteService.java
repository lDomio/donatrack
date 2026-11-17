package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonanteRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.DonanteResponse;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;

import java.util.List;
import java.util.Optional;

public interface DonanteService {
    DonanteResponse agregarDonante(CrearDonanteRequest donante);
    Optional<Donante> findById(Long id);
    Optional<Donante> findByIdDonanteDonacionesService(Long idDonanteDonacionesService);
    public Boolean validarUsuarioDeDonante(Long idDonante, Long idUsuarioAuth);
    Optional<Donante> buscarPorCualquierId(Long id);
    Donante guardarDonante(Donante donante);
    List<Donante> findAll();
}
