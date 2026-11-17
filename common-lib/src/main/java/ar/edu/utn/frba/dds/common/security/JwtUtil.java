package ar.edu.utn.frba.dds.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jwt.secret")
public final class JwtUtil {

  public static final String CLAIM_ROL = "rol";
  public static final String CLAIM_PERMISOS = "permisos";
  public static final String CLAIM_USUARIO_ID = "usuarioId";
  public static final String CLAIM_TYPE = "type";
  public static final String TYPE_REFRESH = "refresh";

  private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 min
  private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 días

  private final Key key;

  public JwtUtil(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Genera el access token incluyendo el id, rol y los permisos del usuario como claims.
   */
  public String generarAccessToken(
      Long usuarioId, String username, String rol, List<String> permisos) {
    var builder = Jwts.builder()
        .setSubject(username)
        .setIssuer("autenticacion-service")
        .claim(CLAIM_ROL, rol)
        .claim(CLAIM_PERMISOS, permisos != null ? permisos : List.of());

    if (usuarioId != null) {
      builder.claim(CLAIM_USUARIO_ID, usuarioId);
    }

    return builder
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generarAccessToken(String username, String rol, List<String> permisos) {
    return generarAccessToken(null, username, rol, permisos);
  }

  /**
   * Genera el refresh token.
   */
  public String generarRefreshToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuer("autenticacion-service")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
        .claim(CLAIM_TYPE, TYPE_REFRESH)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Parsea y valida la firma y vigencia del token JWT.
   */
  public Claims parsearClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String validarToken(String token) {
    return parsearClaims(token).getSubject();
  }
}
