package ar.edu.utn.frba.dds.donaciones.main.dtos.DonanteCsv;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroDonanteDto {
    @CsvBindByName(column = "TipoPersona")
    private String tipoPersona;

    @CsvBindByName(column = "TipoDoc")
    private String tipoDoc;

    @CsvBindByName(column = "Documento")
    private String documento;

    @CsvBindByName(column = "Nombre/Razón Social")
    private String nombreORazonSocial;

    @CsvBindByName(column = "Email")
    private String email;

    @CsvBindByName(column = "Teléfono")
    private String telefono;
}