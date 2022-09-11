package de.bs.webauthenticationsystem_be.model.entity;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;


@Setter
@Getter
@Entity
@Table(indexes = {@Index(columnList = "email")})
public class ResetPasswordLink {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column( columnDefinition = "binary(16)",updatable = false)
    private UUID id;


    private String email;

    private boolean enable;

    private OffsetDateTime created;

}
