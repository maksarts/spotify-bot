package ru.maksarts.spotifybot.configs.credentials;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties(prefix = "vk")
@ConfigurationPropertiesScan
public class VkProperties {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
