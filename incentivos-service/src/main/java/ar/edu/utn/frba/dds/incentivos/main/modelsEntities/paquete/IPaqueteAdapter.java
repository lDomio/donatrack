package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete;

public interface IPaqueteAdapter<T extends Paquete> {
  public void enviar(T paquete);
  public Class<T> tipoDePaqueteSoportado();
}
