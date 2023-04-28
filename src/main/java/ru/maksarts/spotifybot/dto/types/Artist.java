package ru.maksarts.spotifybot.dto.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class Artist implements Serializable {
    private ExternalUrl external_urls;
    private String href;
    private String id;
    private String name;
    private String type;
    private String uri;
}
