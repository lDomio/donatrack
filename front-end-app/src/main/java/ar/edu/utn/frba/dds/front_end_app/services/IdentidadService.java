package ar.edu.utn.frba.dds.front_end_app.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Unica fuente de identidad del donante: sesion real o, mientras no exista login,
 * la property {@code app.donante-demo-id}.
 */
@Service
public class IdentidadService {

  private final String donanteDemoId;

  public IdentidadService(@Value("${app.donante-demo-id:}") String donanteDemoId) {
    this.donanteDemoId = donanteDemoId;
  }

  /**
   * @return el id del donante logueado, el demo id configurado, o {@code null} (invitado).
   */
  public Long donanteIdActual(HttpSession session) {
    Object donanteId = session.getAttribute("donanteId");
    if (donanteId != null) {
      return Long.valueOf(donanteId.toString());
    }
    if (donanteDemoId != null && !donanteDemoId.isBlank()) {
      return Long.valueOf(donanteDemoId);
    }
    return null;
  }

  public boolean esInvitado(HttpSession session) {
    return donanteIdActual(session) == null;
  }
}
