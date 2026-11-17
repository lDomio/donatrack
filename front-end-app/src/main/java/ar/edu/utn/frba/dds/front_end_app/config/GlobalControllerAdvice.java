package ar.edu.utn.frba.dds.front_end_app.config;

import ar.edu.utn.frba.dds.front_end_app.exceptions.ExpiredTokenException;
import ar.edu.utn.frba.dds.front_end_app.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalControllerAdvice {

  @ModelAttribute("requestURI")
  public String getRequestURI(HttpServletRequest request){
    return request.getRequestURI();
  }
  @ExceptionHandler(ExpiredTokenException.class)
  public String handleExpiredToken(ExpiredTokenException e){
    return "redirect:/login?expired";
  }
  @ExceptionHandler(UnauthorizedException.class)
  public String handleUnauthorizedException(UnauthorizedException e){
    return "redirect:/403";
  }
  @ExceptionHandler(WebClientResponseException.Forbidden.class)
  public String handleUnauthorizedException(WebClientResponseException.Forbidden e){
    return "redirect:/403";
  }
}
