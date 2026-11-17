package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EnrutadorDePaquetes {
  private final Map<Class<? extends Paquete>, IPaqueteAdapter> registro;

  public EnrutadorDePaquetes(List<IPaqueteAdapter<?>> todosLosAdapters) {

    // Transformamos la lista en un Mapa: Clave=Clase del Paquete, Valor=El Adapter
    this.registro = todosLosAdapters.stream()
        .collect(Collectors.toMap(
            IPaqueteAdapter::tipoDePaqueteSoportado, // Se usa como Key
            adapter -> adapter                        // Se usa como Value
        ));
  }

  // Registras dinámicamente todos los adapters (Spring hace esto automático si inyectas una List)
  public void registrarAdapter(IPaqueteAdapter<?> adapter) {
    registro.put(adapter.tipoDePaqueteSoportado(), adapter);
  }

  // El servicio llama a este método
  public void enviar(Paquete paquete) {
    IPaqueteAdapter adapter = registro.get(paquete.getClass());
    if (adapter == null) {
      throw new RuntimeException("No hay adapter configurado para: " + paquete.getClass());
    }
    adapter.enviar(paquete);
  }
}
