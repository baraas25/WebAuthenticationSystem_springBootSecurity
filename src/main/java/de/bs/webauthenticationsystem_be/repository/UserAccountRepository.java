package de.bs.webauthenticationsystem_be.repository;

import de.bs.webauthenticationsystem_be.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  UserAccount findUserAccountByUsername(String username);

  UserAccount findUserAccountByEmail(String email);

  UserAccount findUserAccountById(UUID id);
}
