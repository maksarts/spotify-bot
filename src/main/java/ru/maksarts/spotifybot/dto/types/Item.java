package ru.maksarts.spotifybot.dto.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Item implements Serializable {
    private String name;
    private ExternalUrl external_urls;
    private ArrayList<Artist> artists;
    private Album album;
    private String preview_url;
}
