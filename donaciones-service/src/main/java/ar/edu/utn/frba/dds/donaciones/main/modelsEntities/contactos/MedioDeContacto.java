package ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity @Table(name = "MediosDeContacto")
public class MedioDeContacto{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_medioDeContacto")
	private Long id_medioDeContacto;

	public MedioDeContacto(TipoDeContacto tipoDeContacto, String valorDelContacto, Boolean esPreferido) {
		this.tipoDeContacto = tipoDeContacto;
		this.valorDelContacto = valorDelContacto;
		this.esPreferido = esPreferido;
	}

	@Enumerated(jakarta.persistence.EnumType.STRING)
	@Column(name = "tipoDeContacto", nullable = false)
	private TipoDeContacto tipoDeContacto;

	@Column(name = "valorDelContacto", nullable = false)
	private String valorDelContacto;

	@Column(name = "esPreferido", nullable = false)
	private Boolean esPreferido;
}
