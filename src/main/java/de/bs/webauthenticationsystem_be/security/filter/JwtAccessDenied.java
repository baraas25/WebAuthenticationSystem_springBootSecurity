package de.bs.webauthenticationsystem_be.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bs.webauthenticationsystem_be.exception.HTTPProtocolResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAccessDenied implements AccessDeniedHandler {

  private static final String ERROR_MESSAGE = "You do not have permission to access this page";

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    HTTPProtocolResponse httpResponse =
        new HTTPProtocolResponse(
            new Date(),
            UNAUTHORIZED.value(),
            UNAUTHORIZED,
            UNAUTHORIZED.getReasonPhrase().toUpperCase(),
            ERROR_MESSAGE);
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setStatus(UNAUTHORIZED.value());
    OutputStream outputStream = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(outputStream, httpResponse);
    outputStream.flush();
  }
}
