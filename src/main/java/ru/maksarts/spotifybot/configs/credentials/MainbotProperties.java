package ru.maksarts.spotifybot.configs.credentials;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties(prefix = "telegram.mainbot")
@ConfigurationPropertiesScan
public class MainbotProperties {
    @NotBlank
    private String username;
    @NotBlank
    private String token;
}