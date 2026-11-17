/*package ar.edu.utn.frba.dds.incentivos.main.repositories.impl;


import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.incentivos.main.repositories.PaqueteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaqueteRepositoryImpl implements PaqueteRepository {
  @Autowired
  private DonanteRepository donanteRepository;
  @Override
  public List<Paquete> findAllNoEnviados() {
    //Implementar correctamente cuando haya persistencia real
    return donanteRepository.findAll().stream().flatMap(d->d.getPaquetesPendientes().stream()).toList();
  }

  @Override
  public void guardar(Paquete paquete) {
    //TODO
  }

  @Override
  public void eliminar(Paquete paquete) {
    //TODO
  }
}
*/