package ar.edu.utn.frba.dds.incentivos.main.services.impl;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.EnrutadorDePaquetes;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.repositories.PaqueteRepository;
import ar.edu.utn.frba.dds.incentivos.main.services.PaqueteService;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaqueteServiceImpl implements PaqueteService {

  private final PaqueteRepository repository;
  private final EnrutadorDePaquetes enrutador;

  public PaqueteServiceImpl(PaqueteRepository repository, EnrutadorDePaquetes enrutadorDePaquetes) {
    this.repository = repository;
    this.enrutador = enrutadorDePaquetes;
  }

  @Override
  @Transactional
  public void enviarPaquetes() {
    List<Paquete> paquetes = repository.findByFueEnviadoFalse().stream().sorted(Comparator.comparing(Paquete::getFechaDeCreacion)).toList();
    paquetes.forEach(paquete ->{
      try{
        enrutador.enviar(paquete);
        paquete.marcarComoEnviado();
        repository.save(paquete);
      }
      catch (Exception e){
        // El adapter ya registró el intento fallido; persistimos para que no se pierda
        // y NO marcamos el paquete como enviado. Se reintenta en el próximo ciclo.
        repository.save(paquete);
        log.warn("No se pudo enviar el paquete {}: {}", paquete.getClass().getSimpleName(), e.getMessage());
      }
    });
  }

  @Override
  @Transactional
  public void guardarPaquete(Paquete paquete) {
    repository.save(paquete);
  }
}
