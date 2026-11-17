package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaDonante;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.categoriaDonante.CategoriaCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donacion.Donacion;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteEventoObserver;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteObserverFactory;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.DonanteObserverListener;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.CategoriaCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante.observer.generadores.MisionCompletadaEvento;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.Mision;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.mision.MisionCompletada;
import ar.edu.utn.frba.dds.incentivos.main.modelsEntities.paquete.Paquete;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.ToString;

@Entity @Table(name = "donante")
@EntityListeners(DonanteObserverListener.class)
@NoArgsConstructor
@Data
public class Donante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donante")
    private Long id;

    @Column(name = "id_donante_donaciones_service", unique = true)
    private Long idDonanteDonacionesService;

    @Column(name = "id_usuario_auth", unique = true)
    private Long idUsuarioAuth;

    @Transient
    private DonanteEventoObserver observer;

    @Getter @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="donante_id", referencedColumnName="id_donante")
    List<Paquete> paquetesPendientes = new ArrayList<>();

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @OneToMany(mappedBy = "donante", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Donacion> donaciones =new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private CategoriaDonante categoria;

    @Transient
    @Getter(AccessLevel.NONE)
    private List<Insignia> insignias = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "donante_id", referencedColumnName = "id_donante")
    private List<MisionCompletada> misionesCompletadas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_actual_id", referencedColumnName = "id")
    private Mision misionActual;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "donante_id", referencedColumnName = "id_donante")
    private List<CategoriaCompletada> categoriasCompletadas = new ArrayList<>();

    @Embedded
    private ProgresoMision progresoMisionActual;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "donante_id", referencedColumnName = "id_donante")
    private List<MedioDeContacto> medioContacto = new ArrayList<>();

    @Column(name = "fecha_inicio_mision_actual")
    private LocalDate fechaInicioMisionActual = LocalDate.now();

    public Donante(DonanteObserverFactory observerFactory, Long idDonanteDonacionesService, Mision misionActual, List<MedioDeContacto> medioContacto, String nombreUsuario, Long idUsuarioAuth) {
        this.observer = observerFactory != null ? observerFactory.generar(this) : null;
        this.id = null;
        this.idDonanteDonacionesService = idDonanteDonacionesService;
        this.donaciones = new ArrayList<>();
        this.insignias = new ArrayList<>();
        this.misionesCompletadas = new ArrayList<>();
        this.misionActual = misionActual;
        this.categoria = misionActual != null ? misionActual.getCategoria() : null;
        this.categoriasCompletadas = new ArrayList<>();
        this.progresoMisionActual = null;
        this.medioContacto = medioContacto != null ? medioContacto : new ArrayList<>();
        this.fechaInicioMisionActual = LocalDate.now();
        this.nombreUsuario = nombreUsuario;
        this.idUsuarioAuth = idUsuarioAuth;
    }
    public Integer getCantidadDonaciones(){
        return donaciones.size();
    }
    public void registrarDonacion(Donacion donacion) {
        this.donaciones.add(donacion);
    }

    public void completarMisionSiCompletadaYGenerarNotificaciones() {
        if (this.misionActual != null) {
            this.progresoMisionActual = this.misionActual.calcularProgreso(this);
            if (this.progresoMisionActual.getProgreso() >= 100.0) {
                this.misionActual.completar(this);
            }
        }
    }
    public List<Insignia> getInsignias(){
        return this.misionesCompletadas.stream().map(MisionCompletada::getInsigniaOtorgada).toList();
    }
    public void recalcularProgreso(){
        this.setProgresoMisionActual(misionActual.calcularProgreso(this));
    }
    public ProgresoMision getProgresoMisionActual() {
        return this.progresoMisionActual;
    }

    public Integer getTotalEntidadesBeneficiariasQueRecibieron() {
        return new HashSet<>(this.donaciones.stream().filter(Donacion::getFueRecibida).map(Donacion::getEntidadBeneficiariaId).toList()).size();
    }

    public Integer getDonacionesEnPeriodo(YearMonth periodo) {
        return (int) this.donaciones.stream().filter(d ->
                YearMonth.from(d.getFechaDeCreacion()).equals(periodo)).count();
    }
    public Optional<Donacion> obtenerPrimeraDonacion(){
        return donaciones.stream().min(Comparator.comparing(Donacion::getFechaDeCreacion));
    }
    public Optional<Donacion> obtenerUltimaDonacion(){
        return donaciones.stream().max(Comparator.comparing(Donacion::getFechaDeCreacion));
    }
    public void otorgarInsignia(Insignia insignia) {
        this.insignias.add(insignia);
    }


    public void agregarMisionCompletada(Mision mision) {
        MisionCompletada completada = new MisionCompletada(
            mision.getInsignia(),
            mision
        );
        this.misionesCompletadas.add(completada);
        if (this.observer !=null){
            observer.notificarMisionCompletada(new MisionCompletadaEvento(this, completada, mision.getMisionSiguiente(), mision.getInsignia()));
        }

    }

    public LocalDate getFechaDelInicioMisionActual() {
        return this.fechaInicioMisionActual;
    }

    public void cambiarMisionActual(Mision nuevaMision) {
        if (this.misionActual != null) {
            this.agregarMisionCompletada(this.misionActual);
        }

        Mision misionAnterior = this.misionActual;
        this.misionActual = nuevaMision;
        this.fechaInicioMisionActual = LocalDate.now();

        if (nuevaMision != null) {
            this.progresoMisionActual = nuevaMision.calcularProgreso(this);
        } else {
            this.progresoMisionActual = null;
        }

        if (nuevaMision != null) {
            CategoriaDonante catNueva = nuevaMision.getCategoria();
            CategoriaDonante catAnt = (misionAnterior != null) ? misionAnterior.getCategoria() : null;

            boolean diferentes;
            if (catNueva == null && catAnt == null) {
                diferentes = false;
            } else if (catNueva == null || catAnt == null) {
                diferentes = true;
            } else {
                diferentes = !catNueva.equals(catAnt);
            }

            if (misionAnterior == null || diferentes) {
                if (misionAnterior != null && catAnt != null) {
                    this.agregarCategoriaCompletada(new CategoriaCompletada(catAnt), (catNueva != null ? catNueva:this.getCategoria()));
                }
                if (catNueva != null) {
                    this.categoria = catNueva;
                }
            }
        } else {
            if (misionAnterior != null && misionAnterior.getCategoria() != null) {
                this.agregarCategoriaCompletada(new CategoriaCompletada(misionAnterior.getCategoria()),this.getCategoria());
            }
        }
    }
    private void agregarCategoriaCompletada(CategoriaCompletada categoriaCompletada, CategoriaDonante categoriaNueva){
        this.categoriasCompletadas.add(categoriaCompletada);
        if(this.observer !=null){
            observer.notificarCategoriaCompletada(new CategoriaCompletadaEvento(this, categoriaCompletada, categoriaNueva));
        }
    }
    public void reiniciarMision(){
        this.setFechaInicioMisionActual(LocalDate.now());
        this.recalcularProgreso();
    }
    public Boolean donoEnMes(YearMonth mes){
        return this.getDonaciones().stream().anyMatch( donacion -> YearMonth.from(donacion.getFechaDeCreacion()).equals(mes));
    }
    public Long getMisionesCompletadasEnPeriodo(YearMonth periodo){
        return this.getMisionesCompletadas().stream().filter(m -> YearMonth.from(m.getFechaDeComplecion()).equals(periodo)).count();
    }
}
