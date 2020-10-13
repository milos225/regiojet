package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class SimpleRouteSearchResult {
    @JsonProperty
    ArrayList<SimpleRoute> routes;

    @JsonProperty
    String routesMessage;

    @JsonProperty
    ArrayList<BannerBubble> bannerBubbles;

    @JsonProperty
    ArrayList<TextBubble> textBubbles;
}
