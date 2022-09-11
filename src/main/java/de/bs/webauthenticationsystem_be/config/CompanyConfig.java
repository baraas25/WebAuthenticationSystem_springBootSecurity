package de.bs.webauthenticationsystem_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "company-data")
@Getter
@Setter
public class CompanyConfig {
  private String name;
  private String email;
  private String tel;
  private String web;
}
