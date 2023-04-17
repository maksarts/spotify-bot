package ru.maksarts.spotifybot.dto.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonAutoDetect
public class Image implements Serializable {
    private String url;
    private Integer height;
    private Integer width;
}
