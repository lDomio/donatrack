package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DonanteRepositoryMemoria {
  private final List<Donante> donantes = new ArrayList<>();
  private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

  //@Override
  public List<Donante> findAll(){
    return new ArrayList<>(donantes);
  }

  //@Override
  public Optional <Donante> findById(Long id){
    return donantes.stream().filter(donante -> donante.getId().equals(id)).findFirst();
  }

  //@Override
  public void guardarDonante(Donante donante){
    if(donante.getId() == null){
      donante.setId(generadorId.siguiente());
      donantes.add(donante);
    }else{
      this.findById(donante.getId()).ifPresent(donanteSinActualizar-> {
        donantes.remove(donanteSinActualizar);
      });
      donantes.add(donante);
    }
  }

  //@Override
  public void eliminarDonante(Long id){
    donantes.removeIf(donante -> donante.getId().equals(id));
  }

}
