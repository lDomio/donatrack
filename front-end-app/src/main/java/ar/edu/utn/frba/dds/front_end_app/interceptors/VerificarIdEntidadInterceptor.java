package ar.edu.utn.frba.dds.front_end_app.interceptors;

import ar.edu.utn.frba.dds.front_end_app.services.EntidadBeneficiariaServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VerificarIdEntidadInterceptor implements HandlerInterceptor {
  private final EntidadBeneficiariaServiceImpl entidadBeneficiariaService;

  public VerificarIdEntidadInterceptor(EntidadBeneficiariaServiceImpl entidadBeneficiariaService) {
    this.entidadBeneficiariaService = entidadBeneficiariaService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
    HttpSession session = request.getSession();

    Object entidadId = session.getAttribute("idEntidad");
    if (entidadId == null && session.getAttribute("idUsuarioAuth") != null) {
      try {
        Long usuarioId = Long.parseLong(session.getAttribute("idUsuarioAuth").toString());
        Long entId = entidadBeneficiariaService.getEntidadIdByUsuarioId(usuarioId);
        if (entId != null) {
          session.setAttribute("idEntidad", entId);
        }
      } catch (Exception e) {
        // En caso de que no tenga entidad asociada aún
        System.out.println("No pude conseguir el id de la entidad");
      }
    }
    return true;
  }
}
