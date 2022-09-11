package de.bs.webauthenticationsystem_be.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandling implements ErrorController {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(PasswordValid.class)
  public ResponseEntity<HTTPProtocolResponse> passwordValidException(PasswordValid exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(PasswordMatch.class)
  public ResponseEntity<HTTPProtocolResponse> passwordMatchException(PasswordMatch exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<HTTPProtocolResponse> accountDisabledException() {
    return createHttpResponse(BAD_REQUEST, "Please contact administration");
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<HTTPProtocolResponse> badCredentialsException() {
    return createHttpResponse(BAD_REQUEST, "Username or password are not correct");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<HTTPProtocolResponse> accessDeniedException() {
    return createHttpResponse(FORBIDDEN, "You need more permission");
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<HTTPProtocolResponse> lockedException() {
    return createHttpResponse(UNAUTHORIZED, "Please contact administration");
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<HTTPProtocolResponse> tokenExpiredException(
      TokenExpiredException exception) {
    return createHttpResponse(UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler(EmailExist.class)
  public ResponseEntity<HTTPProtocolResponse> emailExistException(EmailExist exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(UsernameExist.class)
  public ResponseEntity<HTTPProtocolResponse> usernameExistException(UsernameExist exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(EmailNotExist.class)
  public ResponseEntity<HTTPProtocolResponse> emailNotFoundException(EmailNotExist exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(UsernameNotExist.class)
  public ResponseEntity<HTTPProtocolResponse> userNotFoundException(UsernameNotExist exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<HTTPProtocolResponse> methodNotSupportedException(
      HttpRequestMethodNotSupportedException exception) {
    HttpMethod supportedMethod =
        Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
    return createHttpResponse(
        METHOD_NOT_ALLOWED, String.format("HTTP Method is not allowed", supportedMethod));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<HTTPProtocolResponse> internalServerErrorException(Exception exception) {
    LOGGER.error(exception.getMessage());
    return createHttpResponse(
        INTERNAL_SERVER_ERROR, "Internal Server Error when processing your request");
  }

  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<HTTPProtocolResponse> notFoundException(NoResultException exception) {
    LOGGER.error(exception.getMessage());
    return createHttpResponse(NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<HTTPProtocolResponse> iOException(IOException exception) {
    LOGGER.error(exception.getMessage());
    return createHttpResponse(INTERNAL_SERVER_ERROR, "An error when processing the file");
  }

  @ExceptionHandler(Validation.class)
  public ResponseEntity<HTTPProtocolResponse> validationException(Validation exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(UserAccountNotActive.class)
  public ResponseEntity<HTTPProtocolResponse> userAccountNotActiveException(
      UserAccountNotActive exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<HTTPProtocolResponse> usernameNotFoundException(
      UsernameNotFoundException exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(ResetPasswordLinkNotExist.class)
  public ResponseEntity<HTTPProtocolResponse> resetPasswordLinkNotExist(
      ResetPasswordLinkNotExist exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  private ResponseEntity<HTTPProtocolResponse> createHttpResponse(
      HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(
        new HTTPProtocolResponse(
            new Date(),
            httpStatus.value(),
            httpStatus,
            httpStatus.getReasonPhrase().toUpperCase(),
            message),
        httpStatus);
  }

  @RequestMapping("/error")
  public ResponseEntity<HTTPProtocolResponse> notFound404() {
    return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
  }
}
