package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.donaciones.main.dtos.DonanteCsv.RegistroDonanteDto;
import ar.edu.utn.frba.dds.donaciones.main.exceptions.ImportacionException;
import ar.edu.utn.frba.dds.donaciones.main.services.LectorDonantes;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LectorDonantesOpenCsv implements LectorDonantes {

  @Override
  public List<RegistroDonanteDto> leerDonantes(InputStream inputStream) {
    // try-with-resources para asegurar que el InputStream se cierre automáticamente
    try (
        // BOMInputStream para manejar el BOM (Byte Order Mark) si está presente
        BOMInputStream bomInputStream = BOMInputStream.builder()
            .setInputStream(inputStream)
            .get();

        // Reader para leer el flujo de entrada con codificacion UTF-8
        Reader reader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8)) {
      // API de OpenCSV para mapear el CSV a objetos RegistroDonanteDto
      CsvToBean<RegistroDonanteDto> csvToBean = new CsvToBeanBuilder<RegistroDonanteDto>(reader)
          .withType(RegistroDonanteDto.class) // Clase a la que mapea
          .withIgnoreLeadingWhiteSpace(true) // Ignora espacios en blanco al inicio de los campos
          .withIgnoreEmptyLine(true)
          .build();

      // Retorna la lista de objetos mapeados desde el CSV
      return csvToBean.parse();
    } catch (Exception e) {
      throw new ImportacionException("Error al leer el archivo CSV de donantes con OpenCSV", e);
    }
  }
}
