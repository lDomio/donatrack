package ar.edu.utn.frba.dds.incentivos.main.services;

import ar.edu.utn.frba.dds.incentivos.main.dtos.ActualizarDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.dtos.CrearDonacionRequest;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;

import java.util.List;

public interface DonacionService {
    Donacion agregarDonacion(CrearDonacionRequest donacion);
    Donacion marcarDonacionComoRecibida(Long idDonacion, String nombreDelEntiad);
    Donacion findById(Long idDonacion) throws Exception;
    List<Donacion> findAllByUserId(Long idDonante);
    List<Donacion> findAll();
}
