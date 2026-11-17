package ar.edu.utn.frba.dds.logistica.main.modelsEntities.transporte;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import java.util.Optional;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity("camiones")
public class Camion {
    @Id
    private ObjectId id;
    private String patente;
    private Double capacidadEnKg;
    private Double volumen;
    private Double altura;
    private Boolean estaEnDeposito;
    private String nombreChofer;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idUsuario;

    @Setter(AccessLevel.NONE)
    @Reference(lazy = true, ignoreMissing = true)
    private List<Ruta> rutas = new ArrayList<>();

    public List<Ruta> getRutas() {
        if (this.rutas == null) {
            this.rutas = new ArrayList<>();
        }
        return this.rutas;
    }

    public void agregarRuta(Ruta ruta) {
        if (this.rutas == null) {
            this.rutas = new ArrayList<>();
        }
        this.rutas.add(ruta);
    }
    public Optional<Ruta> getRutaActiva(){
        return getRutas().stream().filter(Ruta::esActiva).findFirst();
    }
    public Boolean tieneRutaActiva(){
        return getRutaActiva().isPresent();
    }
}
