package ru.maksarts.spotifybot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.maksarts.spotifybot.services.SpotifyService;

@RestController
public class LocalController {

    @Autowired
    SpotifyService spotifyUtils;

    @RequestMapping("/")
    public String index() {
        return "Ну чо смотриш";
    }

    @GetMapping("/search")
    public String mySearch(@RequestParam(value="q") String q, @RequestParam(value="type", required = false, defaultValue = "track") String type) {
        return spotifyUtils.getTracks(q, type).getItems().get(0).getExternal_urls().getSpotify();
    }
}
