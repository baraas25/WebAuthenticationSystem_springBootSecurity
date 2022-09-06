package de.bs.webauthenticationsystem_be.services;


import de.bs.webauthenticationsystem_be.model.Userdata;
import de.bs.webauthenticationsystem_be.model.dto.LoginDTO;
import de.bs.webauthenticationsystem_be.security.JWTProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AsmyAuthentcationService {


    private AuthenticationManager authenticationManager;
    private JWTProvider jwtProvider;

    public void asmyAuthentcation(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
    }
    public HttpHeaders getAsmyJwtHeaders(Userdata userdata) {
        HttpHeaders jwtHeaders = new HttpHeaders();
        jwtHeaders.add("Jwt-Token", jwtProvider.jwtTokenGenerate(userdata));
        return jwtHeaders;
    }
}
