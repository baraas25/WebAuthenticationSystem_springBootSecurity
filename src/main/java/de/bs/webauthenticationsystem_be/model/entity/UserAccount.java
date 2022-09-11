package de.bs.webauthenticationsystem_be.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bs.webauthenticationsystem_be.model.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(
    indexes = {
      @Index(columnList = "username"),
      @Index(columnList = "email"),
    })
public class UserAccount {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(columnDefinition = "binary(16)", updatable = false)
  private UUID id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @NotEmpty(message = "The firstname cannot be empty")
  @Size(min = 3, max = 30, message = "The first name must be between 3 and 30 characters")
  private String firstname;

  @NotEmpty(message = "The lastname cannot be empty")
  @Size(min = 3, max = 30, message = "The last name must be between 3 and 30 characters")
  private String lastname;

  @NotEmpty(message = "The username cannot be empty")
  @Column(unique = true, nullable = false)
  @Size(min = 3, max = 30, message = "The username must be between 3 and 30 characters")
  private String username;

  @NotEmpty(message = "Gender cannot be empty")
  @Email(message = "The format of the email address isn't correct")
  @Column(unique = true, nullable = false)
  private String email;

  @NotEmpty(message = "Gender cannot be empty")
  @Size(min = 8, max = 30, message = "The password must be between 8 and 30 characters")
  @JsonIgnore
  private String password;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM.dd.yyy", timezone = "Europe/Berlin")
  @Past(message = "The Date must not be an instant, date or time in the future.")
  private Date birthday;

  @Column(name = "first_time_join")
  @Future(message = "The Date must be an instant, date or time in the future.")
  private Date firstTimeJoin;

  @Column(name = "last_time_join")
  @Future(message = "The Date must be an instant, date or time in the future.")
  private Date lastTimeJoin;

  private String role;
  private String[] authorites;
  private boolean enable;
  private boolean locked;
  private boolean active;
}
