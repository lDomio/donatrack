package ar.edu.utn.frba.dds.logistica.main.services;

import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionAsignadaRequest;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.DonacionResponse;
import ar.edu.utn.frba.dds.logistica.main.dtos.donacion.PesajeDonacionRequest;

public interface RecepcionDonacionService {
    void procesarAsignacion(DonacionAsignadaRequest request);
    public DonacionResponse cargarPesaje(PesajeDonacionRequest request, Long id);
}
