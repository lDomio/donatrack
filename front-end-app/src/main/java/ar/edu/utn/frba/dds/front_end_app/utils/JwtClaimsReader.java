package ar.edu.utn.frba.dds.front_end_app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Verifica la firma del access token emitido por gestion-alumnos-server y expone sus claims,
 * para leer el rol y los permisos del usuario sin tener que llamar a un endpoint aparte.
 */
@Component
public class JwtClaimsReader {

  public static final String CLAIM_ROL = "rol";
  public static final String CLAIM_PERMISOS = "permisos";
  public static final String CLAIM_USUARIO_ID = "usuarioId";

  private final Key key;

  public JwtClaimsReader(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public Claims leerClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
