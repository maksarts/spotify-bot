package ru.maksarts.spotifybot.configs.credentials;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties(prefix = "spotify")
@ConfigurationPropertiesScan
public class SpotifyProperties {
    @NotBlank
    private String clientId;
    @NotBlank
    private String secret;
}
