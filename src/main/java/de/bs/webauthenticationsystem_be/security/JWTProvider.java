package de.bs.webauthenticationsystem_be.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import de.bs.webauthenticationsystem_be.config.CompanyConfig;
import de.bs.webauthenticationsystem_be.config.JWTConfig;
import de.bs.webauthenticationsystem_be.model.Userdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JWTProvider {
  private final CompanyConfig COMPANY_DATA;
  private final JWTConfig JWT_CONFIG;

  @Autowired
  public JWTProvider(CompanyConfig COMPANY_DATA, JWTConfig JWT_CONFIG) {
    this.COMPANY_DATA = COMPANY_DATA;
    this.JWT_CONFIG = JWT_CONFIG;
  }

  public String jwtTokenGenerate(Userdata userdata) {
    Algorithm algorithm = Algorithm.HMAC512(JWT_CONFIG.getSecret().getBytes());
    List<String> authorities = new ArrayList<>();
    String jwtToken = "";
    userdata
        .getAuthorities()
        .forEach(
            role -> {
              authorities.add(role.getAuthority());
            });

    jwtToken =
        JWT.create()
            .withSubject(userdata.getUsername())
            .withIssuer(COMPANY_DATA.getName())
            .withClaim("roles", authorities)
            .withExpiresAt(new Date(System.currentTimeMillis() + JWT_CONFIG.getExpireTime()))
            .sign(algorithm);
    return jwtToken;
  }

  public String generateRefreshToken(Userdata userdata) {
    Algorithm algorithm = Algorithm.HMAC512(JWT_CONFIG.getSecret().getBytes());
    return JWT.create()
        .withSubject(userdata.getUsername())
        .withIssuer(COMPANY_DATA.getName())
        .withExpiresAt(new Date(System.currentTimeMillis() + JWT_CONFIG.getRefreshTokenTime()))
        .sign(algorithm);
  }

  public boolean validate(String token) {
    Date expiresAt = JWT.decode(token).getExpiresAt();
    String username = getUserName(token);
    return expiresAt.after(new Date()) && !username.isEmpty();
  }

  public String getUserName(String token) {
    return JWT.decode(token).getSubject();
  }
}
