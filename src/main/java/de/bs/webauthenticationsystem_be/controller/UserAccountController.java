package de.bs.webauthenticationsystem_be.controller;

import de.bs.webauthenticationsystem_be.exception.*;
import de.bs.webauthenticationsystem_be.model.Userdata;
import de.bs.webauthenticationsystem_be.model.dto.ChangePasswordDTO;
import de.bs.webauthenticationsystem_be.model.dto.LoginDTO;
import de.bs.webauthenticationsystem_be.model.dto.UserAccountDTO;
import de.bs.webauthenticationsystem_be.model.dto.UserAccountRegisterDTO;
import de.bs.webauthenticationsystem_be.services.AsmyAuthentcationService;
import de.bs.webauthenticationsystem_be.services.imp.UserAccountServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = UserAccountController.USER_CONTROLLER_PATH)
@AllArgsConstructor
public class UserAccountController {

  public static final String USER_CONTROLLER_PATH = "api/v1/user";
  private UserAccountServiceImp userAccountService;
  private AsmyAuthentcationService asmyAuthentcation;

  @PostMapping(
      value = "/register",
      produces = {"application/json"},
      consumes = {"application/json"})
  public ResponseEntity<String> register(@RequestBody UserAccountRegisterDTO registerDTO) {
    return new ResponseEntity<>(userAccountService.register(registerDTO), CREATED);
  }

  @PostMapping(
      value = "/login",
      produces = {"application/json"},
      consumes = {"application/json"})
  public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) throws UserAccountNotActive {
    UserAccountDTO userAccountDTO =
        userAccountService.getUserAccountByUsername(loginDTO.getUsername());
    if (!userAccountDTO.isActive()) {
      throw new UserAccountNotActive("The account is not activated");
    }
    asmyAuthentcation.asmyAuthentcation(loginDTO);
    Userdata userdata = new Userdata(userAccountDTO);
    HttpHeaders jwtHeaders = asmyAuthentcation.getAsmyJwtHeaders(userdata);
    userAccountService.setLoginDate(userAccountDTO.toEntity());
    return new ResponseEntity<>("Login succeeded", jwtHeaders, OK);
  }

  @GetMapping(
      value = "activeUserAccount/{userAccountId}",
      produces = {"text/json"})
  public ResponseEntity<String> activeUserAccount(@PathVariable UUID userAccountId)
      throws UsernameNotExist, Validation {
    return new ResponseEntity<>(userAccountService.activeUserAccount(userAccountId), OK);
  }

  @PutMapping(
      value = "changepassword",
      produces = {"text/json"})
  public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO)
      throws  PasswordMatch {
    return new ResponseEntity<>(userAccountService.changePassword(changePasswordDTO), OK);
  }

  @GetMapping(
      value = "resetpassword/{email}",
      produces = {"text/json"})
  public ResponseEntity<String> sendResetPasswordLinkToEmail(@PathVariable String email)
      throws EmailNotExist, ResetPasswordLinkNotExist {
    return new ResponseEntity<>(userAccountService.sendResetPasswordLinkToEmail(email), OK);
  }

  @GetMapping(
      value = "resetpassword/link/{userAccountId}",
      produces = {"text/json"})
  public ResponseEntity<String> resetPassword(@PathVariable UUID userAccountId)
       {
    return new ResponseEntity<>(userAccountService.resetPassword(userAccountId), OK);
  }

  @GetMapping("/all")
  public List<UserAccountDTO> getAllUser() {
    return userAccountService.getAllUserAccount();
  }

  @GetMapping("/role")
  public String role() {
    return "login";
  }

  @GetMapping("/home")
  public String home() {
    return "Home Page";
  }

  @GetMapping("/admin")
  public String adminRole() {
    return "Home Page";
  }
}
