package de.bs.webauthenticationsystem_be.model;


import de.bs.webauthenticationsystem_be.model.dto.UserAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@AllArgsConstructor
public class Userdata implements UserDetails {
    private UserAccountDTO userAccount;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.userAccount.getAuthorites()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isEnable();
    }
}
