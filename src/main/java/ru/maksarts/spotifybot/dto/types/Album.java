package ru.maksarts.spotifybot.dto.types;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Album implements Serializable {
    private ArrayList<Artist> artists;
    private ExternalUrl external_urls;
    private String href;
    private String id;
    private String name;
    private ArrayList<Image> images;
}
