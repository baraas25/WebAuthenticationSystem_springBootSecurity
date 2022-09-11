package de.bs.webauthenticationsystem_be.model.dto;

import de.bs.webauthenticationsystem_be.model.Person;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class UserAccountRegisterDTO extends Person {
  private String password;
  private String confirmPassword;
  private Date birthday;
}
