package de.bs.webauthenticationsystem_be.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Person {
  private String gender;
  private String firstname;
  private String lastname;
  private String email;
  private String username;
}
