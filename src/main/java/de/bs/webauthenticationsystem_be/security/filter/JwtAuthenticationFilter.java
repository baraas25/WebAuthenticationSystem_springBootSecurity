package de.bs.webauthenticationsystem_be.security.filter;

import de.bs.webauthenticationsystem_be.config.CompanyConfig;
import de.bs.webauthenticationsystem_be.security.JWTProvider;
import de.bs.webauthenticationsystem_be.services.imp.UserAccountServiceImp;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTProvider jwtProvider;
  private final CompanyConfig companyConfig;
  private final UserAccountServiceImp userAccountService;

  public JwtAuthenticationFilter(
      JWTProvider jwtProvider,
      CompanyConfig companyConfig,
      UserAccountServiceImp userAccountService) {
    this.jwtProvider = jwtProvider;
    this.companyConfig = companyConfig;
    this.userAccountService = userAccountService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String header = request.getHeader(AUTHORIZATION);
    if (!StringUtils.hasLength(header) || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String token = header.split(" ")[1].trim();
    if (!jwtProvider.validate(token)) {
      filterChain.doFilter(request, response);
      return;
    }
    UserDetails userDetails = userAccountService.loadUserByUsername(jwtProvider.getUserName(token));
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            Optional.ofNullable(userDetails).map(UserDetails::getAuthorities).orElse(List.of()));
    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    filterChain.doFilter(request, response);
  }
}
