package de.bs.webauthenticationsystem_be.services.imp;

import de.bs.webauthenticationsystem_be.exception.*;
import de.bs.webauthenticationsystem_be.model.Gender;
import de.bs.webauthenticationsystem_be.model.Userdata;
import de.bs.webauthenticationsystem_be.model.dto.ChangePasswordDTO;
import de.bs.webauthenticationsystem_be.model.dto.EmailMessageDTO;
import de.bs.webauthenticationsystem_be.model.dto.UserAccountDTO;
import de.bs.webauthenticationsystem_be.model.dto.UserAccountRegisterDTO;
import de.bs.webauthenticationsystem_be.model.entity.UserAccount;
import de.bs.webauthenticationsystem_be.repository.UserAccountRepository;
import de.bs.webauthenticationsystem_be.security.UserRolesAuthentication;
import de.bs.webauthenticationsystem_be.services.EmailService;
import de.bs.webauthenticationsystem_be.services.LoginAttemptsService;
import de.bs.webauthenticationsystem_be.utility.RegexService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.passay.CharacterData;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccountServiceImp implements UserDetailsService {

  private final EmailService emailService;
  private final UserAccountRepository userAccountRepository;
  private final RegexService regexService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final LoginAttemptsService loginAttemptsService;

  @Autowired
  public UserAccountServiceImp(
      EmailService emailService,
      UserAccountRepository userAccountRepository,
      RegexService regexService,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      LoginAttemptsService loginAttemptsService) {
    this.emailService = emailService;
    this.userAccountRepository = userAccountRepository;
    this.regexService = regexService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.loginAttemptsService = loginAttemptsService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount userAccount = getUserAccountByUsername(username).toEntity();
    userLoginAttemptValidation(userAccount);

    return new Userdata(UserAccountDTO.fromEntity(userAccount));
  }

  private void userLoginAttemptValidation(UserAccount userAccount) {
    if (userAccount.isLocked()) {
      userAccount.setLocked(
          !loginAttemptsService.usernameOverpassMaxAttempts(userAccount.getUsername()));
    } else {
      loginAttemptsService.removeUserAttemptsFromCache(userAccount.getUsername());
    }
  }

  @SneakyThrows
  public String register(UserAccountRegisterDTO userAccountRegisterDTO) {
    UserAccountDTO userAccountDTO = new UserAccountDTO();

    Gender gender = isValidGender(userAccountRegisterDTO.getGender());
    String firstname = isValidFirstname(userAccountRegisterDTO.getFirstname());
    String lastname = isValidLastname(userAccountRegisterDTO.getLastname());
    String username = isValidUsername(userAccountRegisterDTO.getUsername());
    String email = isValidEmail(userAccountRegisterDTO.getEmail());
    String password =
        isValidPassword(
            userAccountRegisterDTO.getPassword(), userAccountRegisterDTO.getConfirmPassword());

    userAccountDTO.setGender(gender);
    userAccountDTO.setFirstname(firstname);
    userAccountDTO.setLastname(lastname);
    userAccountDTO.setEmail(email);
    userAccountDTO.setUsername(username);
    userAccountDTO.setPassword(passwordEncoder(password));
    userAccountDTO.setBirthday(userAccountRegisterDTO.getBirthday());
    userAccountDTO.setEnable(true);
    userAccountDTO.setLocked(true);
    userAccountDTO.setRole(UserRolesAuthentication.USER_PERMISSIONS.name());
    userAccountDTO.setAuthorites(UserRolesAuthentication.USER_PERMISSIONS.getAuthentications());

    UserAccount userAccount = userAccountRepository.save(userAccountDTO.toEntity());

    EmailMessageDTO emailMessageDTO = createEmailMessageDTO(userAccount);

    // emailService.sendActiveLinkByEmail(emailMessageDTO);
    return "Account successfully created:\n"
        + "Please click on the link in the email you received from us to activate your customer account.\n"
        + "If you cannot find the e-mail in your mailbox, please check your spam folder.";
  }

  public List<UserAccountDTO> getAllUserAccount() {
    List<UserAccount> userAccountList = userAccountRepository.findAll();
    return userAccountList.stream().map(UserAccountDTO::fromEntity).collect(Collectors.toList());
  }

  public String activeUserAccount(UUID userAccountId) throws UsernameNotExist, Validation {
    UserAccount userAccount = userAccountRepository.findUserAccountById(userAccountId);
    if (userAccount == null) {
      throw new UsernameNotExist("This account does not exist");
    }
    if (userAccount.isActive()) {
      throw new Validation("This account is already activated");
    }
    userAccount.setActive(true);
    userAccountRepository.save(userAccount);
    return "Your account has been successfully activated";
  }

  public String changePassword(ChangePasswordDTO changePasswordDTO) throws PasswordMatch {
    UserAccountDTO userAccountDTO = getUserAccountByUsername(changePasswordDTO.getUsername());
    UserAccount userAccount = userAccountDTO.toEntity();
    String newPassword =
        isValidPassword(changePasswordDTO.getNewPassword(), changePasswordDTO.getConfirmPassword());
    if (!bCryptPasswordEncoder.matches(
        changePasswordDTO.getPassword(), userAccount.getPassword())) {
      throw new PasswordMatch("incorrect current password");
    }
    userAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
    userAccountRepository.save(userAccount);
    return "Password changed Successfully";
  }

  public String sendResetPasswordLinkToEmail(String email) throws EmailNotExist {
    UserAccount userAccount = getUserAccountByEmail(email).toEntity();
    emailService.sendResetPasswordLinkToEmail(createEmailMessageDTO(userAccount));
    return "A password reset link has been sent to your email";
  }

  public UserAccount getUserAccountById(UUID userAccountId) {
    Optional<UserAccount> userAccount = userAccountRepository.findById(userAccountId);
    if (userAccount.isEmpty()) {
      throw new UsernameNotFoundException("No user found by id " + userAccountId);
    }
    return userAccount.get();
  }

  public UserAccountDTO getUserAccountByUsername(String username) {
    UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
    if (userAccount == null) {
      throw new UsernameNotFoundException("No user found by username " + username);
    }
    return UserAccountDTO.fromEntity(userAccount);
  }

  public UserAccountDTO getUserAccountByEmail(String email) throws EmailNotExist {
    email = email.trim().toLowerCase();
    UserAccount userAccount = userAccountRepository.findUserAccountByEmail(email);
    if (userAccount == null) {
      throw new EmailNotExist("No user found by email " + email);
    }
    return UserAccountDTO.fromEntity(userAccount);
  }

  public String resetPassword(UUID userAccountId) {
    UserAccount userAccount = getUserAccountById(userAccountId);
    String newPassword = generatePassword();
    userAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
    userAccount.setActive(true);
    userAccountRepository.save(userAccount);
    EmailMessageDTO emailMessageDTO = createEmailMessageDTO(userAccount);

    emailService.sendPasswordByEmail(newPassword, emailMessageDTO);

    return "The password has been successfully restored.\n"
        + "The username und new password find be in your email";
  }

  public void setLoginDate(UserAccount userAccount) {
    if (userAccount.getFirstTimeJoin() == null) {
      userAccount.setFirstTimeJoin(new Date());
    }
    userAccount.setLastTimeJoin(new Date());
    userAccountRepository.save(userAccount);
  }

  private Gender isValidGender(String gender) throws Validation {
    String newGender = StringUtils.capitalize(gender.trim());
    for (Gender value : Gender.values()) {
      if (value.name().equals(newGender)) {
        return value;
      }
    }
    throw new Validation("The Gender '" + gender + "' is not correct");
  }

  private String isValidFirstname(String firstname) throws Validation {
    String newFirstname = StringUtils.capitalize(firstname.trim());
    if (StringUtils.isBlank(newFirstname)) {
      throw new Validation("The firstname cannot be empty");
    }
    if (newFirstname.length() < 3 || newFirstname.length() > 30) {
      throw new Validation("The first name must be between 3 and 30 characters");
    }
    if (!regexService.getNamePattern().matcher(newFirstname).find()) {
      throw new Validation("The lastname '" + firstname + "' is not valid");
    }
    return StringUtils.capitalize(newFirstname);
  }

  private String isValidLastname(String lastname) throws Validation {
    String newLastname = StringUtils.capitalize(lastname.trim());
    if (StringUtils.isBlank(newLastname)) {
      throw new Validation("The last name cannot be empty");
    }
    if (newLastname.length() < 3 || newLastname.length() > 30) {
      throw new Validation("The last name must be between 3 and 30 characters");
    }
    if (!regexService.getNamePattern().matcher(newLastname).find()) {
      throw new Validation("The lastname '" + lastname + "' is not valid");
    }
    return newLastname;
  }

  private String isValidUsername(String username) throws UsernameExist, UsernameNotExist {
    String newUsername = username.trim().toLowerCase();

    if (StringUtils.isBlank(username)) {
      throw new UsernameNotExist("The username cannot be empty");
    }
    if (newUsername.length() < 3 || newUsername.length() > 20) {
      throw new UsernameNotExist("The password must be between 3 and 20 characters");
    }
    UserAccount userAccount = userAccountRepository.findUserAccountByUsername(newUsername);
    if (userAccount != null) {
      throw new UsernameExist("The username " + username + " already exist");
    }
    return newUsername;
  }

  private String isValidEmail(String email) throws Validation, EmailExist {
    String newEmail = email.trim().toLowerCase();

    if (StringUtils.isBlank(email)) {
      throw new Validation("The Email cannot be empty");
    }
    if (!regexService.getEmailPattern().matcher(newEmail).find()) {
      throw new Validation("The format of the email address isn't correct");
    }
    UserAccount userAccount = userAccountRepository.findUserAccountByEmail(newEmail);
    if (userAccount != null) {
      throw new EmailExist("The Email " + email + " already exist");
    }
    return newEmail;
  }

  @SneakyThrows
  private String isValidPassword(String newPassword, String confirmPassword) {
    newPassword = newPassword.trim();
    confirmPassword = confirmPassword.trim();
    if (!newPassword.equals(confirmPassword)) {
      throw new PasswordMatch("The passwords not match");
    }
    String messageTemplate = null;
    Properties properties = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
    properties.load(inputStream);
    MessageResolver messageResolver = new PropertiesMessageResolver(properties);
    PasswordValidator passwordValidator =
        new PasswordValidator(
            messageResolver,
            Arrays.asList(
                new LengthRule(8, 20),
                new CharacterRule(GermanCharacterData.UpperCase, 1),
                new CharacterRule(GermanCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new IllegalSequenceRule(GermanSequenceData.Alphabetical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false)));
    RuleResult ruleResult = passwordValidator.validate(new PasswordData(newPassword));
    if (!ruleResult.isValid()) {
      List<String> messages = passwordValidator.getMessages(ruleResult);
      messageTemplate = String.join(",", messages);
      throw new PasswordValid(messageTemplate);
    }
    return newPassword;
  }

  private String passwordEncoder(String password) {
    return bCryptPasswordEncoder.encode(password);
  }

  private EmailMessageDTO createEmailMessageDTO(UserAccount userAccount) {
    EmailMessageDTO emailMessageDTO = new EmailMessageDTO();
    emailMessageDTO.setUserAccountId(userAccount.getId());
    emailMessageDTO.setGender(userAccount.getGender().name());
    emailMessageDTO.setFirstname(userAccount.getFirstname());
    emailMessageDTO.setLastname(userAccount.getLastname());
    emailMessageDTO.setEmail(userAccount.getEmail());
    emailMessageDTO.setUsername(userAccount.getUsername());
    return emailMessageDTO;
  }

  public String generatePassword() {
    PasswordGenerator passwordGenerator = new PasswordGenerator();

    CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
    CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
    lowerCaseRule.setNumberOfCharacters(4);

    CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
    CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
    upperCaseRule.setNumberOfCharacters(4);

    CharacterData digitChars = EnglishCharacterData.Digit;
    CharacterRule digitRule = new CharacterRule(digitChars);
    digitRule.setNumberOfCharacters(2);

    CharacterData specialChars =
        new CharacterData() {
          @Override
          public String getErrorCode() {
            return null;
          }

          @Override
          public String getCharacters() {
            return "!@#$%^&*_+";
          }
        };

    CharacterRule specialRule = new CharacterRule(specialChars);
    digitRule.setNumberOfCharacters(1);

    return passwordGenerator.generatePassword(
        10, specialRule, lowerCaseRule, upperCaseRule, digitRule);
  }
}
