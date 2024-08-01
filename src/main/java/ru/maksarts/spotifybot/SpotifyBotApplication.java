package ru.maksarts.spotifybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.yaml")
public class SpotifyBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotifyBotApplication.class, args);
    }

}
