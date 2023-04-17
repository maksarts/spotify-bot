package ru.maksarts.spotifybot.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import ru.maksarts.spotifybot.dto.types.Track;

import java.io.Serializable;

@Getter
@Setter
@JsonAutoDetect
public class TracksSearchResponse implements Serializable {
    Track tracks;
}
