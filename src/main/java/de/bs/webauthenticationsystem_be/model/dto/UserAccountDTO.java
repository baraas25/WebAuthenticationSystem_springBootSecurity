package de.bs.webauthenticationsystem_be.model.dto;

import de.bs.webauthenticationsystem_be.model.entity.UserAccount;

public class UserAccountDTO extends UserAccount {

  public static UserAccountDTO fromEntity(UserAccount userAccount) {
    UserAccountDTO userAccountDTO = new UserAccountDTO();

    userAccountDTO.setId(userAccount.getId());
    userAccountDTO.setGender(userAccount.getGender());
    userAccountDTO.setFirstname(userAccount.getFirstname());
    userAccountDTO.setLastname(userAccount.getLastname());
    userAccountDTO.setUsername(userAccount.getUsername());
    userAccountDTO.setEmail(userAccount.getEmail());
    userAccountDTO.setPassword(userAccount.getPassword());
    userAccountDTO.setBirthday(userAccount.getBirthday());
    userAccountDTO.setFirstTimeJoin(userAccount.getFirstTimeJoin());
    userAccountDTO.setLastTimeJoin(userAccount.getLastTimeJoin());
    userAccountDTO.setRole(userAccount.getRole());
    userAccountDTO.setAuthorites(userAccount.getAuthorites());
    userAccountDTO.setEnable(userAccount.isEnable());
    userAccountDTO.setLocked(userAccount.isLocked());
    userAccountDTO.setActive(userAccount.isActive());
    return userAccountDTO;
  }

  public UserAccount toEntity() {
    UserAccount userAccount = new UserAccount();

    userAccount.setId(this.getId());
    userAccount.setGender(this.getGender());
    userAccount.setFirstname(this.getFirstname());
    userAccount.setLastname(this.getLastname());
    userAccount.setUsername(this.getUsername());
    userAccount.setEmail(this.getEmail());
    userAccount.setPassword(this.getPassword());
    userAccount.setBirthday(this.getBirthday());
    userAccount.setFirstTimeJoin(this.getFirstTimeJoin());
    userAccount.setLastTimeJoin(this.getLastTimeJoin());
    userAccount.setRole(this.getRole());
    userAccount.setAuthorites(this.getAuthorites());
    userAccount.setEnable(this.isEnable());
    userAccount.setLocked(this.isLocked());
    userAccount.setActive(this.isActive());
    return userAccount;
  }
}
