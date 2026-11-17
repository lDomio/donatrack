package ar.edu.utn.frba.dds.front_end_app.interceptors;

import ar.edu.utn.frba.dds.front_end_app.services.back_services.donaciones.DonanteApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VerificarIdDonanteInterceptor implements HandlerInterceptor {
  private final DonanteApiService donanteApiService;

  public VerificarIdDonanteInterceptor(DonanteApiService donanteApiService) {
    this.donanteApiService = donanteApiService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
    HttpSession session = request.getSession();

    Object donanteId = session.getAttribute("idDonante");
    if (donanteId == null && session.getAttribute("idUsuarioAuth") != null) {
      try {
        Long usuarioId = Long.parseLong(session.getAttribute("idUsuarioAuth").toString());
        Long donId = donanteApiService.obtenerPorIdUsuarioAuth(usuarioId).id();
        if (donId != null) {
          session.setAttribute("idDonante", donId);
        }
      } catch (Exception e) {
        // En caso de que no tenga entidad asociada aún
        System.out.println("No pude conseguir el id del donante");
      }
    }
    return true;
  }
}
