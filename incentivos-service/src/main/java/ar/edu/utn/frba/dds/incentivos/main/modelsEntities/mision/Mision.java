package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Donante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.Insignia;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.ProgresoMision;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mision")
public class Mision {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "insignia_id", referencedColumnName = "id")
    private Insignia insignia;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "mision_anterior_id", referencedColumnName = "id")
    private Mision misionAnterior;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "mision_siguiente_id", referencedColumnName = "id")
    private Mision misionSiguiente;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_donante_id", referencedColumnName = "id")
    private CategoriaDonante categoria;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "estado_mision_id", referencedColumnName = "id")
    private EstadoMision mision;

    public Mision(Insignia insignia) {
        this.insignia = insignia;
    }

    public Mision(String nombre, Insignia insignia, EstadoMision estadoMision) {
        this.nombre = nombre;
        this.insignia = insignia;
        this.mision = estadoMision;
    }

    public ProgresoMision calcularProgreso(Donante donante) {
        if (this.mision == null) {
            return new ProgresoMision(0.0, 0.0, 0.0);
        }
        return this.mision.calcularProgreso(donante);
    }

    public void completar(Donante donante) {
        donante.otorgarInsignia(this.insignia);
        donante.cambiarMisionActual(this.misionSiguiente);
    }

    public Boolean esDeRacha(){
        return mision.esDeRacha();
    }
    private void otorgarRecompensa(Donante donante) {
        donante.otorgarInsignia(this.getInsignia());
    }
}
