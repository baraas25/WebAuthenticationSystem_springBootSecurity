package de.bs.webauthenticationsystem_be.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@AllArgsConstructor
public class HTTPProtocolResponse {

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "MM.dd.yyy hh:mm:ss",
      timezone = "Europe/Berlin")
  private Date httpStampServerResponseTime;

  private int httpResponseStatusCodes;
  private HttpStatus httpStatus;
  private String httpResponsePhraseReason;
  private String httpMessage;
}
