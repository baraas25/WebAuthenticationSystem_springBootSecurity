package de.bs.webauthenticationsystem_be.controller;

import de.bs.webauthenticationsystem_be.model.entity.ResetPasswordLink;
import de.bs.webauthenticationsystem_be.services.imp.ResetPasswordLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/pass")
public class ResetPasswordLinkControler {

  @Autowired private ResetPasswordLinkService resetPasswordLinkService;

  @GetMapping("/getall")
  public List<ResetPasswordLink> adminRole() {
    return resetPasswordLinkService.getAll();
  }
}
