package ru.maksarts.spotifybot.dto.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Track implements Serializable {
    private String href;
    private Integer limit;
    private String next;
    private String previous;
    private Integer offset;
    private Integer total;
    ArrayList<Item> items;
}
