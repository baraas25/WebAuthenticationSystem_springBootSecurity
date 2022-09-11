package de.bs.webauthenticationsystem_be.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bs.webauthenticationsystem_be.exception.HTTPProtocolResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationHttp403 extends Http403ForbiddenEntryPoint {

  private static final String ERROR_MESSAGE =
      "To access this page need firstly to register then to login";

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    HTTPProtocolResponse httpResponse =
        new HTTPProtocolResponse(
            new Date(),
            FORBIDDEN.value(),
            FORBIDDEN,
            FORBIDDEN.getReasonPhrase().toUpperCase(),
            ERROR_MESSAGE);
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setStatus(FORBIDDEN.value());
    OutputStream outputStream = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(outputStream, httpResponse);
    outputStream.flush();
  }
}
