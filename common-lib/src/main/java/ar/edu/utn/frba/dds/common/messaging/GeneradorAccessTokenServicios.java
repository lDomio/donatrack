package ar.edu.utn.frba.dds.common.messaging;

import ar.edu.utn.frba.dds.common.security.JwtUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jwt.secret")
public class GeneradorAccessTokenServicios {
  private final String serviceRole;
  private final String serviceUsername;
  private final JwtUtil jwtUtil;

  public GeneradorAccessTokenServicios(@Value("${service.role:ROLE_SERVICE}") String serviceRole, @Value("${service.username:Service}") String serviceUsername, JwtUtil jwtUtil) {
    this.serviceRole = serviceRole;
    this.serviceUsername = serviceUsername;
    this.jwtUtil = jwtUtil;
  }
  public String generarAccessToken(){
    return jwtUtil.generarAccessToken(serviceUsername, serviceRole, List.of());
  }
}
