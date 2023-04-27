package ru.maksarts.spotifybot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.maksarts.spotifybot.services.SpotifyUtils;
import ru.maksarts.spotifybot.services.YoutubeUtils;

@Component
@Slf4j
public class Starter implements CommandLineRunner {
    @Autowired
    SpotifyUtils spotifyUtils;
    @Autowired
    YoutubeUtils youtubeUtils;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("START");
//        spotifyUtils.getSongs("the foreshadowing departure", "track");

//        log.info("оксимирон={}", youtubeUtils.getVideoUrl("oxxxymiron", "тентакли"));
//        log.info("хеллиантус={}", youtubeUtils.getVideoUrl("HELLIANTHUS", "луна"));
//        log.info("дайте танк={}", youtubeUtils.getVideoUrl("Дайте танк (!)", "Мы"));
//        log.info("muse={}", youtubeUtils.getVideoUrl("Muse", "Resistance"));

//        youtubeUtils.downloadMp3("Гражданская оборона", "Солнцеворот");
    }
}
