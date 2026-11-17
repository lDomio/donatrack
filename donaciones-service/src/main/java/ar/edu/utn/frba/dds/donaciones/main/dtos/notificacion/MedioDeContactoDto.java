package ar.edu.utn.frba.dds.donaciones.main.dtos.notificacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedioDeContactoDto {
    private String valor;
    private String tipo;
}
