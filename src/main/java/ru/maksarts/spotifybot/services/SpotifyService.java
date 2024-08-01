package ru.maksarts.spotifybot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.spotifybot.configs.credentials.SpotifyProperties;
import ru.maksarts.spotifybot.dto.TokenResponse;
import ru.maksarts.spotifybot.dto.TracksSearchResponse;
import ru.maksarts.spotifybot.dto.types.Track;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyService {

    public static final String BASE_URL = "https://api.spotify.com/v1/";
    public static final String AUTH_URL = "https://accounts.spotify.com/api/token";

    private String CLIENT_ID;
    private String CLIENT_SECRET;

    private static final String TRACK_TYPE = "track";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SpotifyProperties props;

    private TokenResponse tokenResponse;

    @PostConstruct
    public void postConstructInitialisation(){
        this.CLIENT_ID = props.getClientId();
        this.CLIENT_SECRET = props.getSecret();
    }

    public Track getTracks(String q){
        return getTracks(q, TRACK_TYPE);
    }
    public Track getTracks(String q, String type){
        if (this.tokenResponse == null) {
            tokenResponse = sendAuth();
        }
        else if (this.tokenResponse.getExpireTime().isBefore(LocalDateTime.now())) {
            if(tokenResponse.getRefresh_token() != null) {
                try {
                    tokenResponse = refreshToken(tokenResponse);
                } catch (Exception ex) {
                    log.warn("Cannot refresh token: {}", ex.getMessage(), ex);
                    tokenResponse = sendAuth();
                }
            } else{
                tokenResponse = sendAuth();
            }
        }

        ResponseEntity<TracksSearchResponse> response;
        try {
            response = sendSearchRequest(q, type, tokenResponse.getAccess_token());

            if (response.getStatusCode().is2xxSuccessful()){
                if (response.getBody() != null) {
                    //log.info("response={}", response.getBody().toString());
                    return response.getBody().getTracks();
                }
                else{
                    log.error("status_code = {}, response body = null", response.getStatusCode());
                    return null;
                }
            }

        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 403){

                log.info("Re-auth...");
                try {
                    tokenResponse = refreshToken(tokenResponse);
                } catch (Exception refreshEx){
                    log.warn("Cannot refresh token: {}", refreshEx.getMessage(), refreshEx);
                    tokenResponse = sendAuth();
                }

                try {
                    response = sendSearchRequest(q, type, tokenResponse.getAccess_token());

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        return response.getBody().getTracks();
                    }

                } catch (HttpStatusCodeException ex2) {
                    throw new RuntimeException("Search failed: " + ex2.getMessage(), ex2);
                }
            }
        } catch (Exception undefined){
            throw new RuntimeException("Search failed", undefined);
        }
        return null;
    }

    private TokenResponse refreshToken(TokenResponse oldToken){
        if(oldToken.getRefresh_token() == null) throw new RuntimeException("refresh_token is null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", oldToken.getRefresh_token());
        map.add("client_id", CLIENT_ID);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(AUTH_URL,
                    request,
                    TokenResponse.class);

            log.info("refresh token response status code = {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                response.getBody().setExpireTime(LocalDateTime.now().plusSeconds(response.getBody().getExpires_in()));
                return response.getBody();
            }
            else if (response.getBody() != null && response.getBody().getError() != null) {
                throw new RuntimeException("Failed to refresh token: " + response.getStatusCode() + ": " + response.getBody());
            }
            else {
                throw new RuntimeException("Failed to refresh token");
            }

        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException("Failed to get token: " + ex.getMessage(), ex);
        }
    }
    private TokenResponse sendAuth(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET, StandardCharsets.UTF_8);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(AUTH_URL,
                                                                                request,
                                                                                TokenResponse.class);

            log.info("auth response status code = {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                response.getBody().setExpireTime(LocalDateTime.now().plusSeconds(response.getBody().getExpires_in()));
                return response.getBody();
            }
            else if (response.getBody() != null && response.getBody().getError() != null) {
                throw new RuntimeException("Failed to get token: " + response.getStatusCode() + ": " + response.getBody());
            }
            else {
                throw new RuntimeException("Failed to get token");
            }

        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException("Failed to get token: " + ex.getMessage(), ex);
        }
    }

    private ResponseEntity<TracksSearchResponse> sendSearchRequest(String q, String type, @NonNull String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");

        String url = BASE_URL + "/search"
                            + "?q=" + q
                            + "&type=" + type;

        log.info("request url = {}", url);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        ResponseEntity<TracksSearchResponse> response = restTemplate.exchange(url,
                                                                            HttpMethod.GET,
                                                                            request,
                                                                            TracksSearchResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("response status code={}", response.getStatusCode());
        }
        return response;
    }
}
