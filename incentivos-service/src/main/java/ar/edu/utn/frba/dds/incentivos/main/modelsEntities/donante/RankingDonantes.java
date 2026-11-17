package ar.edu.utn.frba.dds.incentivos.main.modelsEntities.donante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity @Table(name = "ranking_de_donantes")
public class RankingDonantes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ranking_id", referencedColumnName = "id")
    @OrderBy("puesto ASC")
    private List<DonanteEnRanking> ranking;

    @Column(name = "fecha_ranking")
    private LocalDate fechaRanking;

    public RankingDonantes(LocalDate fechaRanking, List<Donante> donantes) {
        this.fechaRanking = fechaRanking;
        this.generarRanking(donantes);
    }

    public YearMonth getPeriodo() {
        return YearMonth.from(this.fechaRanking);
    }

    public int obtenerPosicion(Long idDonante) {
        if (idDonante == null || ranking == null) return 0;
        for (int i = 0; i < ranking.size(); i++) {
            Donante d = ranking.get(i).getDonante();
            if (d != null && (idDonante.equals(d.getId()) || idDonante.equals(d.getIdDonanteDonacionesService()))) {
                return i + 1;
            }
        }
        return 0;
    }

    public void generarRanking(List<Donante> donantes) {
        YearMonth periodo = this.getPeriodo();

        Comparator<Donante> porMisiones = Comparator
                .comparingLong((Donante d) -> d.getMisionesCompletadasEnPeriodo(periodo))
                .reversed();
        Comparator<Donante> porDonaciones = Comparator
                .comparingLong((Donante d) -> d.getDonacionesEnPeriodo(periodo))
                .reversed();
        Comparator<Donante> porNombre = Comparator.comparing(
                (Donante d) -> d.getNombreUsuario() != null ? d.getNombreUsuario() : "",
                String.CASE_INSENSITIVE_ORDER);

        List<Donante> ordenados = donantes.stream()
                .filter(d -> d.getMisionesCompletadasEnPeriodo(periodo) > 0)
                .sorted(porMisiones.thenComparing(porDonaciones).thenComparing(porNombre))
                .toList();

        List<DonanteEnRanking> items = new ArrayList<>();
        int puesto = 1;
        for (Donante donante : ordenados) {
            items.add(new DonanteEnRanking(donante, puesto++));
        }
        this.ranking = items;
    }
}
