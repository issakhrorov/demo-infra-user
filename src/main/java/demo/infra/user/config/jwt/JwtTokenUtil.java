package demo.infra.user.config.jwt;

import demo.infra.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
  @Value("${jwt.access.token.expiration}")
  private Long accessTokenExpiration;

  @Value("${jwt.refresh.token.expiration}")
  private Long refreshTokenExpiration;

  private final KeyPair keyPair;

  public JwtTokenUtil(KeyPair keyPair) {
    this.keyPair = keyPair;
  }

  public String generateAccessToken(User user) {
    return generateToken(user.getId(), accessTokenExpiration, keyPair.getPrivate());
  }

  public String generateRefreshToken(User user) {
    return generateToken(user.getId(), refreshTokenExpiration, keyPair.getPrivate());
  }

  public String generateToken(Long subject, Long expiration, PrivateKey privateKey) {
    String authoritiesStr = "USER";
    return Jwts.builder()
      .subject(subject.toString())
      .claim("scopes", authoritiesStr)
      .signWith(privateKey, Jwts.SIG.RS512)
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
      .compact();
  }

  public String getUserIdFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
    var publicKey = keyPair.getPublic();
    final JwtParserBuilder jwtParser = Jwts.parser().verifyWith(publicKey);
    final Jws<Claims> claimsJws = jwtParser.build().parseSignedClaims(token);
    final Claims claims = claimsJws.getPayload();
    return Arrays.stream(claims.get("scopes").toString().split(","))
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String userId = getUserIdFromToken(token);
    return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  private Claims getAllClaimsFromToken (String token){
    return Jwts
      .parser()
      .verifyWith(keyPair.getPublic())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}