package ar.edu.utn.frba.dds.donaciones.main.services.impl;

import ar.edu.utn.frba.dds.donaciones.main.dtos.DonanteCsv.RegistroDonanteDto;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.DonanteResponse;
import ar.edu.utn.frba.dds.donaciones.main.dtos.donantes.MedioDeContactoDTO;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.AuthClient;
import ar.edu.utn.frba.dds.donaciones.main.infrastructure.IncentivosClient;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.MedioDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.contactos.TipoDeContacto;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Donante;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Humana;
import ar.edu.utn.frba.dds.donaciones.main.modelsEntities.donantes.Juridica;
import ar.edu.utn.frba.dds.donaciones.main.repositories.DonanteRepository;
import ar.edu.utn.frba.dds.donaciones.main.services.LectorDonantes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ImportadorDonantesService {

  private final LectorDonantes lectorDonantes;
  private DonanteRepository donanteRepository;
  private AuthClient authClient;
  private IncentivosClient incentivosClient;

  // Constructor para tests unitarios
  public ImportadorDonantesService(LectorDonantes lectorDonantes) {
    this.lectorDonantes = lectorDonantes;
  }

  // Constructor completo para inyección de dependencias en Spring Boot
  @Autowired
  public ImportadorDonantesService(
      LectorDonantes lectorDonantes,
      DonanteRepository donanteRepository,
      AuthClient authClient,
      IncentivosClient incentivosClient) {
    this.lectorDonantes = lectorDonantes;
    this.donanteRepository = donanteRepository;
    this.authClient = authClient;
    this.incentivosClient = incentivosClient;
  }

  public List<Donante> importar(MultipartFile file) {
    try {
      // Obtenemos el flujo de datos del archivo y se lo pasamos al lector de donantes
      List<RegistroDonanteDto> registros = lectorDonantes.leerDonantes(file.getInputStream());

      List<Donante> donantes = new ArrayList<>();
      for (RegistroDonanteDto dto : registros) {
        Donante donante = mapearDonante(dto);
        donantes.add(donante);
      }
      return donantes;
    } catch (java.io.IOException e) {
      throw new ar.edu.utn.frba.dds.donaciones.main.exceptions.ImportacionException(
          "Error al leer el archivo CSV de donantes", e);
    }
  }

  @Transactional
  public List<DonanteResponse> importarYPersistir(MultipartFile file) {
    List<Donante> donantes = importar(file);
    List<DonanteResponse> responses = new ArrayList<>();

    for (Donante donante : donantes) {
      String nombreCompleto = obtenerNombre(donante);
      String doc = "";
      if (donante instanceof Humana h && h.getNumeroDeDocumento() != null) {
        doc = h.getNumeroDeDocumento().replaceAll("[^a-zA-Z0-9]", "");
      }

      String username = !doc.isBlank()
          ? "donante_" + doc
          : "donante_" + normalizarTexto(nombreCompleto);
      String password = !doc.isBlank() ? "Donante." + doc : "Donante.1234";

      if (this.authClient != null) {
        Long idUsuarioAuth = this.authClient.registrarUsuario(username, password, nombreCompleto, "DONANTE");
        if (idUsuarioAuth != null) {
          donante.setIdUsuarioAuth(idUsuarioAuth);
        }
      }

      if (this.donanteRepository != null) {
        this.donanteRepository.save(donante);
      }

      DonanteResponse response = mapearResponse(donante);
      if (this.incentivosClient != null) {
        try {
          this.incentivosClient.agregarDonante(response, donante.getIdUsuarioAuth());
        } catch (Exception e) {
          log.warn("No se pudo notificar a incentivos para {}: {}", response.id(), e.getMessage());
        }
      }
      responses.add(response);
    }

    return responses;
  }

  private DonanteResponse mapearResponse(Donante donante) {
    String nombre = obtenerNombre(donante);
    String tipo = donante instanceof Humana ? "HUMANA" : (donante instanceof Juridica ? "JURIDICA" : "DESCONOCIDO");

    return new DonanteResponse(
        donante.getId(),
        nombre,
        donante.getContactos() == null ? List.of() : donante.getContactos().stream()
            .map(c -> new MedioDeContactoDTO(c.getTipoDeContacto(), c.getValorDelContacto()))
            .toList(),
        tipo,
        donante.getEstaActivo()
    );
  }

  private String obtenerNombre(Donante donante) {
    if (donante instanceof Humana h) {
      return ((h.getNombre() != null ? h.getNombre() : "") + " " + (h.getApellido() != null ? h.getApellido() : "")).trim();
    } else if (donante instanceof Juridica j) {
      return j.getRazonSocial() != null ? j.getRazonSocial() : "Empresa";
    }
    return "Donante";
  }

  private String normalizarTexto(String texto) {
    if (texto == null || texto.isBlank()) return "user" + System.currentTimeMillis();
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase()
        .replaceAll("[^a-z0-9]", "");
  }

  private Donante mapearDonante(RegistroDonanteDto dto) {

    Donante donante;
    if (dto.getTipoPersona() != null && dto.getTipoPersona().equalsIgnoreCase("HUMANA")) {
      donante = crearHumana(dto);
    } else if (dto.getTipoPersona() != null && dto.getTipoPersona().equalsIgnoreCase("JURIDICA")) {
      donante = crearJuridica(dto);
    } else {
      throw new IllegalArgumentException("Tipo de donante desconocido: " + dto.getClass());
    }

    donante.setEstaActivo(true);
    crearContactos(dto).forEach(donante::agregarContacto);

    return donante;
  }

  private Humana crearHumana(
      RegistroDonanteDto dto) {

    String[] datos = separarNombreApellido(
        dto.getNombreORazonSocial());

    Humana humana = new Humana();

    humana.setNombre(datos[0]);

    humana.setApellido(datos[1]);

    humana.setNumeroDeDocumento(dto.getDocumento());

    return humana;
  }

  private Juridica crearJuridica(
      RegistroDonanteDto dto) {

    Juridica juridica = new Juridica();

    juridica.setRazonSocial(
        dto.getNombreORazonSocial());

    return juridica;
  }

  private List<MedioDeContacto> crearContactos(
      RegistroDonanteDto dto) {

    List<MedioDeContacto> contactos = new ArrayList<>();

    if (dto.getEmail() != null &&
        !dto.getEmail().isBlank()) {

      MedioDeContacto email = new MedioDeContacto();

      email.setTipoDeContacto(
          TipoDeContacto.EMAIL);

      email.setValorDelContacto(
          dto.getEmail());

      email.setEsPreferido(true);

      contactos.add(email);
    }

    if (dto.getTelefono() != null &&
        !dto.getTelefono().isBlank()) {

      MedioDeContacto telefono = new MedioDeContacto();

      telefono.setTipoDeContacto(
          TipoDeContacto.SMS);

      telefono.setValorDelContacto(
          dto.getTelefono());

      telefono.setEsPreferido(false);

      contactos.add(telefono);
    }

    return contactos;
  }

  private String[] separarNombreApellido(
      String valor) {

    String[] partes = valor.trim()
        .split("\\s+", 2);

    if (partes.length < 2) {

      return new String[] {
          partes[0],
          ""
      };
    }

    return partes;
  }
}