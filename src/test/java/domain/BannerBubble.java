package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BannerBubble {
    @JsonProperty
    Integer id;

    @JsonProperty
    String text;

    @JsonProperty
    String url;

    @JsonProperty
    String imageUrl;
}
