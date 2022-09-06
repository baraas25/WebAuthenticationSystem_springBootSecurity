package de.bs.webauthenticationsystem_be.security;

import de.bs.webauthenticationsystem_be.services.LoginAttemptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptsFailure {

    private final LoginAttemptsService loginAttemptsService;

    @Autowired
    public LoginAttemptsFailure(LoginAttemptsService loginAttemptsService) {
        this.loginAttemptsService = loginAttemptsService;
    }

    @EventListener
    public void onLoginAttemptsFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String){
            String username = (String) principal;
            loginAttemptsService.addUserAttemptToCache(username);
        }
    }
}
