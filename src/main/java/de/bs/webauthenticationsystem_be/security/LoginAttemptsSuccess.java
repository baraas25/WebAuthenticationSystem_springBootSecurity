package de.bs.webauthenticationsystem_be.security;

import de.bs.webauthenticationsystem_be.model.Userdata;
import de.bs.webauthenticationsystem_be.services.LoginAttemptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptsSuccess {

  private final LoginAttemptsService loginAttemptsService;

  @Autowired
  public LoginAttemptsSuccess(LoginAttemptsService loginAttemptsService) {
    this.loginAttemptsService = loginAttemptsService;
  }

  @EventListener
  public void onLoginAttemptsSuccess(AuthenticationFailureBadCredentialsEvent event){
    Object principal = event.getAuthentication().getPrincipal();
    if (principal instanceof Userdata) {
      Userdata userdata = (Userdata) principal;
      loginAttemptsService.removeUserAttemptsFromCache(userdata.getUsername());
    }
  }
}
