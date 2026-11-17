package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.services.impl.LectorDonantesOpenCsv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImportadorDonantesServiceTest {

    private ImportadorDonantesService importador;
    private MultipartFile archivoCsvMock;

    @BeforeEach
    void setUp() throws Exception {
        importador = new ImportadorDonantesService(new LectorDonantesOpenCsv());

        Path path = Paths.get(ClassLoader
                .getSystemResource("donacionesService/donantes_import_20000_UTF8_BOM.csv")
                .toURI());

        byte[] content = Files.readAllBytes(path);

        archivoCsvMock = new MockMultipartFile(
                "file",
                "donantes_import_20000_UTF8_BOM.csv",
                "text/csv",
                content);
    }

    @Test
    void importarCsv_deberiaRetornarListaDeDonantes() {
        List<Donante> resultado = importador.importar(archivoCsvMock);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }
}
