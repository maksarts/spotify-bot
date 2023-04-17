package ru.maksarts.spotifybot.dto.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@JsonAutoDetect
public class Album implements Serializable {
    private ArrayList<Artist> artists;
    private ExternalUrl external_urls;
    private String href;
    private String id;
    private String name;
    private ArrayList<Image> images;
}
