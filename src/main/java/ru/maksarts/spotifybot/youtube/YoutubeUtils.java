package ru.maksarts.spotifybot.youtube;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.spotifybot.dto.TracksSearchResponse;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class YoutubeUtils {

    public static final String YT_SEARCH_URL = "https://www.youtube.com/results?search_query=";
    public static final String YT_WATCH_URL = "https://www.youtube.com/watch?v=";

    @Autowired
    private RestTemplate restTemplate;

    protected static Pattern patternVideoUrl;

    static {
        patternVideoUrl = Pattern.compile("\"videoId\":\"(\\w+)\"");
    }

    public String getVideoUrl(String artist, String song) throws FileNotFoundException, ScriptException {
        String searchUrl = (YT_SEARCH_URL + artist + "-Topic" + "+" + song).replaceAll(" ", "+");

        log.info("searchUrl = {}", searchUrl);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(searchUrl,
                                                                HttpMethod.GET,
                                                                request,
                                                                String.class);

//        log.info("response = {}", response.getBody());

        if(response.getBody() != null){
            Matcher matcher = patternVideoUrl.matcher(response.getBody());
            if(matcher.find()){
                String youtubeVideoId = matcher.group(1); // first link on the page
                String videoUrl = YT_WATCH_URL + youtubeVideoId;
                return videoUrl;
            }
        }

        //TODO "/usr/bin/env python3 script.py"

        return null;
    }
}
