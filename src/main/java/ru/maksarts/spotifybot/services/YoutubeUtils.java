package ru.maksarts.spotifybot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.spotifybot.dto.TracksSearchResponse;

import javax.script.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeUtils {

    public static final String YT_SEARCH_URL = "https://www.youtube.com/results?search_query=";
    public static final String YT_WATCH_URL = "https://www.youtube.com/watch?v=";

    @Autowired
    private RestTemplate restTemplate;

    protected static Pattern patternVideoUrl;

    static {
        patternVideoUrl = Pattern.compile("\"videoId\":\"(\\w+)\"");
    }

    public Boolean downloadMp3(String artist, String song){
        try {
            String videoUrl = getVideoUrl(artist, song);

            //"/usr/bin/env python3 script.py"
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                        "pyscripts/youtube_download.py",
                                                                        "--link", videoUrl,
                                                                        "--path", "../test_download/",
                                                                        "--ffmpeg", "bin");
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            while (line != null){
                log.info(line);
                line = bufferedReader.readLine();
            }

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
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

        return null;
    }
}
