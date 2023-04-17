package ru.maksarts.spotifybot.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocalController {

    @Autowired
    SpotifyUtils spotifyUtils;

    @RequestMapping("/")
    public String index() {
        return "Ну чо смотриш";
    }

    @GetMapping("/search")
    public String mySearch(@RequestParam(value="q") String q, @RequestParam(value="type", required = false, defaultValue = "track") String type) {
        return spotifyUtils.getSongs(q, type);
    }
}
