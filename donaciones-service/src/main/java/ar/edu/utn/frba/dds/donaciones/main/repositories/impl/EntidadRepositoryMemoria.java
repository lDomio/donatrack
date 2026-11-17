package ar.edu.utn.frba.dds.donaciones.main.repositories.impl;


import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donaciones.Donacion;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.entidadesBeneficiarias.EntidadBeneficiaria;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.necesidades.Necesidad;
import ar.edu.utn.frba.dds.donaciones.main.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.dds.donaciones.main.utils.GeneradorIdSecuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class EntidadRepositoryMemoria {
  private final List<EntidadBeneficiaria> entidadesBeneficiarias = new ArrayList<>();
  private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

  public List<EntidadBeneficiaria> findAll(){
    return new ArrayList<>(entidadesBeneficiarias);
  }

  public List<Necesidad> findAllNecesidades(){
    return new ArrayList<>(entidadesBeneficiarias).stream().flatMap(e -> e.getNecesidades().stream()).toList();
  }
  //@Override
  public Optional<EntidadBeneficiaria> findById(Long id){
    return entidadesBeneficiarias.stream().filter(entidad-> entidad.getId().equals(id)).findFirst();
  }
  public Optional<Necesidad> findNecesidadById(Long id){
    Optional<EntidadBeneficiaria> entidadBeneficiaria = entidadesBeneficiarias.stream().filter(entidad -> entidad.getNecesidades().stream().mapToLong(Necesidad::getId).anyMatch(l -> l ==id)).findFirst();
    return entidadBeneficiaria.flatMap(beneficiaria -> beneficiaria.getNecesidades().stream().filter(n -> Objects.equals(n.getId(), id)).findFirst());
  }

    //@Override
    public Optional<EntidadBeneficiaria> findByRazonSocial(String razonSocial) {
      return entidadesBeneficiarias.stream().filter(entidad-> entidad.getRazonSocial().equals(razonSocial)).findFirst();
    }

    //@Override
  public void guardar(EntidadBeneficiaria entidad){
    if(entidad.getId() == null){
      entidad.setId(generadorId.siguiente());
      entidadesBeneficiarias.add(entidad);
    }else{
      this.findById(entidad.getId()).ifPresent(entidadBeneficiariaSinActualizar->{
        entidadesBeneficiarias.remove(entidadBeneficiariaSinActualizar);
        entidadesBeneficiarias.add(entidad);
      });
    }
  }

  //@Override
  public void eliminar(Long id){
    this.findById(id).ifPresent(entidadesBeneficiarias::remove);
  }
}
