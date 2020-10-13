package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TextBubble {
    @JsonProperty
    Integer id;

    @JsonProperty
    String text;
}
