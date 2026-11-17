package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonacionRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DonacionRepositoryMemoria {
  private final List<Donacion> donaciones = new ArrayList<>();
  private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

  //@Override
  public List<Donacion> findAll(){
    return new ArrayList<>(donaciones);
  }

  //@Override
  public Optional<Donacion> findById(Long id){
    return donaciones.stream().filter(donacion -> donacion.getId().equals(id)).findFirst();
  }

  //@Override
  public List<Donacion> findByIdOwner(Long idOwner){
    return donaciones.stream().filter(donacion -> donacion.getIdDonante().equals(idOwner)).collect(Collectors.toList());
  }

  //@Override
  public Long guardar(Donacion donacion){
    if(donacion.getId() == null){
      donacion.setId(generadorId.siguiente());
      donaciones.add(donacion);
    }else{
      this.findById(donacion.getId()).ifPresent(donacionSinActualizar->{
        donaciones.remove(donacionSinActualizar);
      });
      donaciones.add(donacion);
    }
    return donacion.getId();
  }

  //@Override
  public void eliminarDonacion(Long id){
    donaciones.removeIf(donacion -> donacion.getId().equals(id));
  }
}