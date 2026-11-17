package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.generadoresMisionCompletada;


import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.IGeneradorPaqueteDonanteMisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.paqueteN8N.PaqueteN8N;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GeneradorPaqueteDonanteMisionCompletadaN8N implements IGeneradorPaqueteDonanteMisionCompletada {
  @Override
  public Optional<Paquete> generarPaqueteSiCorresponde(MisionCompletadaEvento misionCompletadaEvento) {

    Map<String, String> body = new HashMap<>();

    body.put("nombreUsuario", misionCompletadaEvento.donante().getNombreUsuario());
    body.put("nombreInsignia", misionCompletadaEvento.insigniaOtorgada().getDescripcion());
    String tipoMision = misionCompletadaEvento.misionAnterior() != null
        && misionCompletadaEvento.misionAnterior().getMision() != null
        ? misionCompletadaEvento.misionAnterior().getMision().getNombre()
        : null;
    body.put("tipoMision", tipoMision);

    return Optional.of(new PaqueteN8N(body));
  }
  public Paquete generarPaqueteParaService(String nombreDonante, String descripcionInsigniaObtenida, String tipoMision){
    Map<String, String> body = new HashMap<>();

    body.put("nombreUsuario", nombreDonante);
    body.put("nombreInsignia", descripcionInsigniaObtenida);
    body.put("tipoMision", tipoMision);

    return new PaqueteN8N(body);
  }
}
