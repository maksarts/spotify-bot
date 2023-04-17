package ru.maksarts.spotifybot.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonAutoDetect
public class TokenResponse implements Serializable {
    private String access_token;
    private String token_type;
    private Integer expires_in;

    @Override
    public String toString() {
        return String.format("[access_token : %s, token_type : %s, expires_in : %s]", access_token, token_type, expires_in);
    }
}
