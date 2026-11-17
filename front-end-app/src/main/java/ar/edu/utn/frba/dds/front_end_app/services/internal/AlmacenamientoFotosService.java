package ar.edu.utn.frba.dds.front_end_app.services.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda en disco las fotos de los bienes y devuelve la URL pública (/uploads/bienes/...)
 * que se persiste en Bien.foto_url.
 */
@Service
public class AlmacenamientoFotosService {

  private static final long TAMANIO_MAXIMO_BYTES = 5L * 1024 * 1024;
  private static final Map<String, String> EXTENSION_POR_TIPO = Map.of(
      "image/jpeg", "jpg",
      "image/png", "png",
      "image/webp", "webp"
  );

  private final Path directorioBienes;

  public AlmacenamientoFotosService(@Value("${app.uploads.dir:uploads}") String directorioUploads) {
    this.directorioBienes = Path.of(directorioUploads).toAbsolutePath().normalize().resolve("bienes");
  }

  public String guardar(MultipartFile archivo) {
    if (archivo == null || archivo.isEmpty()) {
      throw new IllegalArgumentException("Seleccioná una imagen para adjuntar.");
    }
    if (archivo.getSize() > TAMANIO_MAXIMO_BYTES) {
      throw new IllegalArgumentException("La imagen supera el tamaño máximo permitido de 5 MB.");
    }
    String extension = EXTENSION_POR_TIPO.get(archivo.getContentType());
    if (extension == null) {
      throw new IllegalArgumentException("Formato de imagen no válido. Usá JPG, PNG o WEBP.");
    }

    String nombreArchivo = UUID.randomUUID() + "." + extension;
    try {
      Files.createDirectories(directorioBienes);
      try (InputStream contenido = archivo.getInputStream()) {
        Files.copy(contenido, directorioBienes.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo guardar la imagen en el servidor.", e);
    }
    return "/uploads/bienes/" + nombreArchivo;
  }
}
