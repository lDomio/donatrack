package ar.edu.utn.frba.dds.notificacion.main.services.dtos;

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
    private String tipo; // "EMAIL", "WHATSAPP", "SMS"
}
