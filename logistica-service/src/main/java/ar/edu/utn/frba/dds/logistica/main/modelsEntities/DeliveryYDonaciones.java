package ar.edu.utn.frba.dds.logistica.main.modelsEntities;

import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Delivery;
import ar.edu.utn.frba.dds.logistica.main.modelsEntities.donaciones.Donacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.morphia.annotations.Entity;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
public class DeliveryYDonaciones {
    Delivery delivery;
    List<Donacion> donaciones;

    public void agregarDonacion(Donacion donacion) {
        donaciones.add(donacion);
    }
}
