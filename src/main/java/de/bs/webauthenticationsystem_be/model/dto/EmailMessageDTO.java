package de.bs.webauthenticationsystem_be.model.dto;

import de.bs.webauthenticationsystem_be.model.Person;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class EmailMessageDTO extends Person {
  UUID userAccountId;
}
