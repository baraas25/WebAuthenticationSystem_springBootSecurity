package de.bs.webauthenticationsystem_be.services.imp;

import de.bs.webauthenticationsystem_be.exception.ResetPasswordLinkNotExist;
import de.bs.webauthenticationsystem_be.model.entity.ResetPasswordLink;
import de.bs.webauthenticationsystem_be.model.entity.UserAccount;
import de.bs.webauthenticationsystem_be.repository.ResetPasswordLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ResetPasswordLinkService {

  private final ResetPasswordLinkRepository resetPasswordLinkRepository;

  @Autowired
  public ResetPasswordLinkService(ResetPasswordLinkRepository resetPasswordLinkRepository) {
    this.resetPasswordLinkRepository = resetPasswordLinkRepository;
  }

  public void addResetPasswordLink(UserAccount userAccount) throws ResetPasswordLinkNotExist {
    ResetPasswordLink resetPasswordLink =
        resetPasswordLinkRepository.findByEmail(userAccount.getEmail());
    System.out.println(resetPasswordLink);
    if (resetPasswordLink == null) {
      resetPasswordLinkRepository.save(createResetPasswordLink(userAccount));
      return;
    } else if (resetPasswordLink.getCreated().isBefore(OffsetDateTime.now().minusHours(1))) {
      resetPasswordLinkRepository.deleteById(resetPasswordLink.getId());
      resetPasswordLinkRepository.save(createResetPasswordLink(userAccount));
      return;
    }
    long resetTime =
        ChronoUnit.MINUTES.between(
            resetPasswordLink.getCreated(), OffsetDateTime.now().plusHours(1));
    System.out.println(resetTime);
    throw new ResetPasswordLinkNotExist(
        "You already have a link. You can reset password after " + resetTime + " minutes");
  }

  // TODO delete
  public List<ResetPasswordLink> getAll() {
    return resetPasswordLinkRepository.findAll();
  }

  private ResetPasswordLink createResetPasswordLink(UserAccount userAccount) {
    ResetPasswordLink newResetPasswordLink = new ResetPasswordLink();
    newResetPasswordLink.setCreated(OffsetDateTime.now(ZoneId.of("Europe/Berlin")));
    newResetPasswordLink.setEmail(userAccount.getEmail());
    return newResetPasswordLink;
  }
}
