package ar.edu.utn.frba.dds.logistica.main.modelsEntities;

import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Delivery;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.TimeWindow;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Truck;
import ar.edu.utn.frba.dds.logistica.main.dtos.requestApiRutas.Warehouse;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("planificadorRutaRequests")
public class PlanificadorRutaRequest {
    @Setter
    @Id
    String requestId;
    List<DeliveryYDonaciones> deliveriesYDonaciones;
    List<Truck> trucks;
}
