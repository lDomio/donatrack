package ar.edu.utn.frba.dds.donaciones.main;

import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.TipoDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Juridica;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO charlarlo con Agus, esto está deprecado?
@NoArgsConstructor
public class ImportadorDeCsv {
    public List<Donante> ImportarCsv(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV está vacío.");
        }

        try (
                BOMInputStream bomInputStream = BOMInputStream.builder()
                        .setInputStream(file.getInputStream())
                        .get();
                Reader reader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build()
                        .parse(reader)
        ) {

            List<Donante> donantes = new ArrayList<>();

            for (CSVRecord record : parser) {
                Donante donante = mapearDonante(record);
                donantes.add(donante);
            }

            imprimirDonantesImportados(donantes);
            return donantes;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage(), e);
        }
    }

    private Donante mapearDonante(CSVRecord record) {
        String tipoPersona = record.get("TipoPersona").trim().toUpperCase();

        Donante donante;
        if (Objects.equals(tipoPersona, "HUMANA")) {
            donante = mapearHumana(record.get("Nombre/Razón Social"), record.get("Documento"));
        } else if (Objects.equals(tipoPersona, "JURIDICA")) {
            donante = mapearJuridica(record.get("Nombre/Razón Social"), record.get("Documento"));
        } else {
            throw new IllegalArgumentException("Tipo de persona inválido: " + tipoPersona);
        }

        agregarContactoSiPresente(donante, TipoDeContacto.EMAIL, record.get("Email"));
        agregarContactoSiPresente(donante, TipoDeContacto.TELEFONO, record.get("Teléfono"));

        return donante;
    }

    private Humana mapearHumana(String nombreORazonSocial, String documento) {
        String[] partes = nombreORazonSocial.trim().split("\\s+", 2);
        String nombre = partes[0];
        String apellido = partes.length > 1 ? partes[1] : "";

        Humana humana = new Humana();
        humana.setNombre(nombre);
        humana.setApellido(apellido);
        humana.setNumeroDeDocumento(documento);
        return humana;
    }

    private Juridica mapearJuridica(String razonSocial, String documento) {
        Juridica juridica = new Juridica();
        juridica.setRazonSocial(razonSocial);
        return juridica;
    }

    private void imprimirDonantesImportados(List<Donante> donantes) {
        for (Donante donante : donantes) {
            if (donante instanceof Humana h) {
                System.out.print(h.getNombre() + " " + h.getApellido());
            } else if (donante instanceof Juridica j) {
                System.out.print(j.getRazonSocial() + " ");
            }

            if (!donante.getContactos().isEmpty()) {
                System.out.print(donante.getContactos().getFirst().getValorDelContacto());
            }
            System.out.println();
        }
    }

    private void agregarContactoSiPresente(Donante donante, TipoDeContacto tipoDeContacto, String valor) {
        if (valor != null && !valor.isBlank()) {
            donante.agregarContacto(new MedioDeContacto(tipoDeContacto, valor.trim(), true));
        }
    }
}