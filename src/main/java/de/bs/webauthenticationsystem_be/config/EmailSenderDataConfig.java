package de.bs.webauthenticationsystem_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "email-sender-data")
@Getter
@Setter
public class EmailSenderDataConfig {
    private String cc;
    private String subject;
}
