package de.bs.webauthenticationsystem_be.model.dto;

import lombok.Getter;

@Getter
public class ChangePasswordDTO extends LoginDTO {
  private String newPassword;
  private String confirmPassword;
}
