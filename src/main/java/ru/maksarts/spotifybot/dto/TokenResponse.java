package ru.maksarts.spotifybot.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class TokenResponse implements Serializable {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String error;
    private String error_description;
}
