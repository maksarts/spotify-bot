package ru.maksarts.spotifybot.dto;

import lombok.Data;

@Data
public class VkAuthInfo {
    public VkAuthInfo(String token, String userAgent){
        this.token = token;
        this.userAgent = userAgent;
    }
    String token;
    String userAgent;
}
