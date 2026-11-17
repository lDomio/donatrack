package ar.edu.utn.frba.dds.common.security;

import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class RabbitJwtSecurityInterceptor implements MethodInterceptor {
  private final JwtUtil jwtUtil;

  public RabbitJwtSecurityInterceptor(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Message message = null;
    if (invocation.getArguments() != null) {
      for (Object arg : invocation.getArguments()) {
        if (arg instanceof Message msg) {
          message = msg;
          break;
        }
      }
    }

    if (message != null) {
      Object authHeader = message.getMessageProperties().getHeaders().get("Authorization");

      if (authHeader != null && authHeader.toString().startsWith("Bearer ")) {
        String token = authHeader.toString().substring(7);

        try {
          Claims claims = jwtUtil.parsearClaims(token);

          if (JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
            throw new AmqpRejectAndDontRequeueException("Token inválido (es refresh token)");
          }

          String username = claims.getSubject();
          Object usuarioIdObj = claims.get(JwtUtil.CLAIM_USUARIO_ID);
          Long usuarioId = usuarioIdObj instanceof Number num ? num.longValue() : null;
          Usuario usuario = new Usuario(username, usuarioId);

          List<GrantedAuthority> authorities = construirAuthorities(claims);

          var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (AmqpRejectAndDontRequeueException e) {
          throw e;
        } catch (Exception e) {
          System.out.println("Token invalido en Rabbit: " + token);
          throw new AmqpRejectAndDontRequeueException("Fallo de seguridad: Token inválido");
        }
      }
    }

    try {

      return invocation.proceed();
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  private List<GrantedAuthority> construirAuthorities(Claims claims) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    String rol = claims.get(JwtUtil.CLAIM_ROL, String.class);
    if (rol != null && !rol.isBlank()) {
      String roleAuthority = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
      authorities.add(new SimpleGrantedAuthority(roleAuthority));
    }
    List<?> permisos = claims.get(JwtUtil.CLAIM_PERMISOS, List.class);
    if (permisos != null) {
      permisos.forEach(permiso -> authorities.add(new SimpleGrantedAuthority(String.valueOf(permiso))));
    }
    return authorities;
  }


}
