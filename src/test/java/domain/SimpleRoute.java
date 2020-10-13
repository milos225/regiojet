package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import java.util.ArrayList;

@Data
public class SimpleRoute {

    @JsonProperty
    String id;

    @JsonProperty
    Long departureStationId;

    @JsonProperty
    String departureTime;

    @JsonProperty
    Long arrivalStationId;

    @JsonProperty
    String arrivalTime;

    @JsonProperty
    ArrayList<VehicleType> vehicleTypes;

    @JsonProperty
    Integer transfersCount;

    @JsonProperty
    Integer freeSeatsCount;

    @JsonProperty
    Double priceFrom;

    @JsonProperty
    Double priceTo;

    @JsonProperty
    Double creditPriceFrom;

    @JsonProperty
    Double creditPriceTo;

    @JsonProperty
    Integer pricesCount;

    @JsonProperty
    Boolean actionPrice;

    @JsonProperty
    Boolean surcharge;

    @JsonProperty
    Boolean notices;

    @JsonProperty
    Boolean support;

    @JsonProperty
    Boolean nationalTrip;

    @JsonProperty
    Boolean bookable;

    @JsonProperty
    String delay;

    @JsonProperty
    String travelTime;

    @JsonProperty
    String vehicleStandardKey;
}
