package ru.maksarts.spotifybot.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.maksarts.spotifybot.dto.types.Track;

import java.io.Serializable;

@Data
public class TracksSearchResponse implements Serializable {
    Track tracks;
}
