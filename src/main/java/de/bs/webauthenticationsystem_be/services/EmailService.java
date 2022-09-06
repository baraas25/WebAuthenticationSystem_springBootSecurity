package de.bs.webauthenticationsystem_be.services;

import de.bs.webauthenticationsystem_be.config.CompanyConfig;
import de.bs.webauthenticationsystem_be.config.EmailSenderDataConfig;
import de.bs.webauthenticationsystem_be.model.dto.EmailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailService {

  private final CompanyConfig companyConfig;
  private final EmailSenderDataConfig emailSenderDataConfig;
  private final JavaMailSender mailSender;

  @Autowired
  public EmailService(
      CompanyConfig companyConfig,
      EmailSenderDataConfig emailSenderDataConfig,
      JavaMailSender mailSender) {
    this.companyConfig = companyConfig;
    this.emailSenderDataConfig = emailSenderDataConfig;
    this.mailSender = mailSender;
  }

  public void sendActiveLinkByEmail(EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = createUserActiveEmail(emailMessageDTO);
    mailSender.send(message);
  }

  private SimpleMailMessage createUserActiveEmail(EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailMessageDTO.getEmail());
    message.setSubject(emailSenderDataConfig.getSubject());
    message.setText(userActiveEmailText(emailMessageDTO));
    message.setSentDate(new Date());
    return message;
  }

  private String userActiveEmailText(EmailMessageDTO emailMessageDTO) {
    String salute = createSalute(emailMessageDTO);
    String text = "please click on the link to confirm your Account:\n ";
    String activeUrl =
        "http://localhost:8080/api/v1/user/activeUserAccount/" + emailMessageDTO.getUserAccountId();
    String footer = createFooter();
    return salute + text + activeUrl + footer;
  }

  public void sendPasswordByEmail(String newPassword, EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = createResetPasswordEmail(newPassword, emailMessageDTO);
    mailSender.send(message);
  }

  private SimpleMailMessage createResetPasswordEmail(
      String newPassword, EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailMessageDTO.getEmail());
    message.setSubject("Account recovery");
    message.setText(resetPasswordEmailText(newPassword, emailMessageDTO));
    message.setSentDate(new Date());
    return message;
  }

  private String resetPasswordEmailText(String newPassword, EmailMessageDTO emailMessageDTO) {
    String salute = createSalute(emailMessageDTO);
    String text = "Account Data:\n";
    String username = "Username: " + emailMessageDTO.getUsername() + "\n";
    String password = "Password: " + newPassword;
    String footer = createFooter();

    return salute + text + username + password + footer;
  }

  public void sendResetPasswordLinkToEmail(EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = createResetPasswordLinkEmail(emailMessageDTO);
    mailSender.send(message);
  }

  private SimpleMailMessage createResetPasswordLinkEmail(EmailMessageDTO emailMessageDTO) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailMessageDTO.getEmail());
    message.setSubject("Account recovery");
    message.setText(resetPasswordLinkEmailText(emailMessageDTO));
    message.setSentDate(new Date());
    return message;
  }

  private String resetPasswordLinkEmailText(EmailMessageDTO emailMessageDTO) {
    String salute = createSalute(emailMessageDTO);
    String text = "please click on the link to reset your password:\n";
    String resetUrl =
        "http://localhost:8080/api/v1/user/resetpassword/link/"
            + emailMessageDTO.getUserAccountId()
            + "\n";
    String footer = createFooter();
    return salute + text + resetUrl + footer;
  }

  private String createSalute(EmailMessageDTO emailMessageDTO) {
    switch (emailMessageDTO.getGender()) {
      case "Female":
        return "Mr " + emailMessageDTO.getLastname() + ",\n \n";
      case "Male":
        return "Mrs " + emailMessageDTO.getLastname() + ",\n \n";
      default:
        return "Mr/Mrs " + emailMessageDTO.getLastname() + ",\n \n";
    }
  }

  private String createFooter() {
    return "\n\n---------\n"
        + companyConfig.getName()
        + "\n Email: "
        + companyConfig.getEmail()
        + "\ntel: "
        + companyConfig.getTel()
        + "\n web: "
        + companyConfig.getWeb()
        + "\n ";
  }
}
