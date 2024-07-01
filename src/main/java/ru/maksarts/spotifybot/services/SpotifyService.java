package ru.maksarts.spotifybot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.spotifybot.dto.TokenResponse;
import ru.maksarts.spotifybot.dto.TracksSearchResponse;
import ru.maksarts.spotifybot.dto.types.Track;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SpotifyService {

    public static final String BASE_URL = "https://api.spotify.com/v1/";
    public static final String AUTH_URL = "https://accounts.spotify.com/api/token";

    private static final String CLIENT_ID = "<id>";
    private static final String CLIENT_SECRET = "<secret>"; //TODO переложить в конфиг

    private static final String TRACK_TYPE = "track";

    @Autowired
    private RestTemplate restTemplate;

    private String token;

    public Track getTracks(String q){
        return getTracks(q, TRACK_TYPE);
    }
    public Track getTracks(String q, String type){
        if (this.token == null) token = sendAuth();
        ResponseEntity<TracksSearchResponse> response;

        try {
            response = sendSearchRequest(q, type, token);

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
                token = sendAuth();

                try {
                    response = sendSearchRequest(q, type, token);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        return response.getBody().getTracks();
                    }

                } catch (HttpStatusCodeException ex2) {
                    throw new RuntimeException("Search failed: " + ex2.getMessage(), ex2);
                }
            }
        }

        throw new RuntimeException("Search failed");
    }

    private String sendAuth(){
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
            //if (response.getBody() != null) log.info(response.getBody().toString());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                return response.getBody().getAccess_token();
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
