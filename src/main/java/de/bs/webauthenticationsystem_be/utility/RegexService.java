package de.bs.webauthenticationsystem_be.utility;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Getter
@Component
public class RegexService {
  private final Pattern namePattern =
      Pattern.compile("^\\s?[a-zA-ZöÖüÜäÄß]+(\\s[a-zA-ZöÖüÜäÄß]+)*\\s?$");
  private final Pattern phoneNumberPattern = Pattern.compile("^[+]?\\d{9,15}$");
  private final Pattern emailPattern =
      Pattern.compile(
          "^\\s*(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\\s*$");
}
