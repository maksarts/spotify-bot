package ru.maksarts.spotifybot.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TokenResponse implements Serializable {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String error;
    private String error_description;
    private LocalDateTime expireTime;
    private String refresh_token;
    private String scope;
}
