package ru.maksarts.spotifybot;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.spotifybot.configs.RestTemplateConfig;
import ru.maksarts.spotifybot.dto.TracksSearchResponse;
import ru.maksarts.spotifybot.services.SpotifyService;
import ru.maksarts.spotifybot.services.YoutubeService;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class Starter implements CommandLineRunner {
    @Autowired
    SpotifyService spotifyUtils;
    @Autowired
    YoutubeService youtubeUtils;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("START");
//        log.info(spotifyUtils.getTracks("гражданская оборона", "track").toString());

//        log.info("оксимирон={}", youtubeUtils.getVideoUrl("oxxxymiron", "тентакли"));
//        log.info("хеллиантус={}", youtubeUtils.getVideoUrl("HELLIANTHUS", "луна"));
//        log.info("дайте танк={}", youtubeUtils.getVideoUrl("Дайте танк (!)", "Мы"));
//        log.info("muse={}", youtubeUtils.getVideoUrl("Muse", "Resistance"));

//        youtubeUtils.downloadMp3("Judas priest", "painkiller");
//        log.info("audioUrl={}", youtubeUtils.getAudioUrl(youtubeUtils.getVideoUrl("Гражданская оборона", "Солнцеворот")));
    }
}
