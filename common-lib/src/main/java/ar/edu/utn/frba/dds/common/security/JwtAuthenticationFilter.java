package ar.edu.utn.frba.dds.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        Claims claims = jwtUtil.parsearClaims(token);

        if (JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
          response.sendError(
              HttpServletResponse.SC_UNAUTHORIZED, "Token inválido (se esperaba access token)");
          return;
        }

        String username = claims.getSubject();
        Object usuarioIdObj = claims.get(JwtUtil.CLAIM_USUARIO_ID);
        Long usuarioId = usuarioIdObj instanceof Number num ? num.longValue() : null;
        Usuario usuario = new Usuario(username, usuarioId);

        List<GrantedAuthority> authorities = construirAuthorities(claims);

        var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception e) {
        System.err.println("Error al validar JWT en JwtAuthenticationFilter: " + e.getMessage());
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private List<GrantedAuthority> construirAuthorities(Claims claims) {
    List<GrantedAuthority> authorities = new ArrayList<>();

    // 1. Mapear rol con prefijo ROLE_
    String rol = claims.get(JwtUtil.CLAIM_ROL, String.class);
    if (rol != null && !rol.isBlank()) {
      if(rol.startsWith("ROLE_")){
        authorities.add(new SimpleGrantedAuthority( rol));
      }
      else{
        authorities.add(new SimpleGrantedAuthority("ROLE_"+ rol));
      }    }

    // 2. Mapear permisos finos
    List<?> permisos = claims.get(JwtUtil.CLAIM_PERMISOS, List.class);
    if (permisos != null) {
      permisos.forEach(p -> authorities.add(new SimpleGrantedAuthority(String.valueOf(p))));
    }

    return authorities;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    if (path == null || path.isBlank()) {
      path = request.getRequestURI();
    }
    // Solo excluir login y refresh públicos, NO register ni otros endpoints que requieren autenticación
    return "/api/auth".equals(path) || "/api/auth/".equals(path)
        || "/api/auth/refresh".equals(path) || "/api/auth/refresh/".equals(path);
  }
}