package de.bs.webauthenticationsystem_be.repository;


import de.bs.webauthenticationsystem_be.model.entity.ResetPasswordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ResetPasswordLinkRepository extends JpaRepository<ResetPasswordLink, UUID> {
    ResetPasswordLink findByEmail(String email);
}
