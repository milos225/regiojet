import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import domain.SimpleRoute;
import domain.SimpleRouteSearchResult;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import pages.RegioSearchFormPage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class RegioTest {

  public static final String LOCATION_FROM = "Ostrava";
  public static final String LOCATION_TO = "Brno";
  public static final String OSTRAVA_LOCATION_ID = "10202000";
  public static final String BRNO_LOCATION_ID = "10202002";
  public static final String LOCATION_TYPE = "CITY";
  public static final String TARIFFS = "REGULAR";
  public static final DayOfWeek DAY_OF_WEEK = DayOfWeek.MONDAY;
  public static final String DATE_FORMAT_API = "yyyy-MM-dd";
  public static final int SECONDS_COUNT = 120;

  @BeforeClass
  public static void setup() {
    Properties props = System.getProperties();
    props.setProperty("webdriver.driver", "chrome");
    props.setProperty("webdriver.chrome.driver", "src\\test\\java\\drivers\\chromedriver_84.exe");
    props.setProperty("selenide.startMaximized", "true");
  }

  @Test
  public void searchingUsingWeb() {
    // Search
    open("https://shop.regiojet.sk");
    $(RegioSearchFormPage.FROM).setValue(LOCATION_FROM);
    $(RegioSearchFormPage.FROM_SUGGESTION_ITEM).click();
    $(RegioSearchFormPage.TO).setValue(LOCATION_TO);
    $(RegioSearchFormPage.TO_SUGGESTION_ITEM).click();

    LocalDate day = LocalDate.now().with(TemporalAdjusters.next(DAY_OF_WEEK));
    $(RegioSearchFormPage.DATE_THERE_CLEAR_BTN).click();
    $(RegioSearchFormPage.DATE_THERE_CLEAR_BTN).should(Condition.disappears);
    $(RegioSearchFormPage.DATE_THERE).setValue(String.valueOf(day.getDayOfMonth()));
    $(RegioSearchFormPage.SEARCH_BTN).click();
    $(RegioSearchFormPage.RESULTS_TAB).shouldBe(Condition.appears);

    // Results
    List<Double> arrivalTimes =
        $$(RegioSearchFormPage.ARRIVAL_TIME).stream()
            .map(SelenideElement::getText)
            .map(a -> a.replace(":", "."))
            .map(Double::valueOf)
            .sorted()
            .collect(Collectors.toList());

    List<Double> travelTimes =
        $$(RegioSearchFormPage.TRAVEL_TIME).stream()
            .map(SelenideElement::getText)
            .map(a -> a.replace("Doba cesty\n", "").replace(" h", "").replace(":", "."))
            .map(Double::valueOf)
            .sorted()
            .collect(Collectors.toList());

    List<Double> prices =
        $$(RegioSearchFormPage.PRICE).stream()
            .map(SelenideElement::getText)
            .map(a -> a.replace("od ", ""))
            .map(a -> a.replaceAll(" \u20ac\n.*", ""))
            .map(a -> a.replaceAll(" \u20ac", ""))
            .map(a -> a.replace(",", "."))
            .map(Double::valueOf)
            .sorted()
            .collect(Collectors.toList());

    String fastestArrivalTime = String.format("%.2f", arrivalTimes.get(0)).replace(".", ":");
    String shortestTravelTime = String.format("%.2f", travelTimes.get(0)).replace(".", ":");
    String lowestPrice = prices.get(0).toString().replace(".", ",");

    SelenideElement rowWithLowestPrice =
        $(By.xpath(String.format(RegioSearchFormPage.ROW_WITH_LOWEST_PRICE_XPATH, lowestPrice)));
    SelenideElement rowWithShortestTravelTime =
        $(
            By.xpath(
                String.format(
                    RegioSearchFormPage.ROW_WITH_SHORTEST_TRAVEL_TIME_XPATH, shortestTravelTime)));
    SelenideElement rowWithFastestArrivalTime =
        $(
            By.xpath(
                String.format(
                    RegioSearchFormPage.ROW_WITH_FASTEST_ARRIVAL_TIME_XPATH, fastestArrivalTime)));

    String departureTimeOfFastestArrivalTime =
        rowWithFastestArrivalTime.find(RegioSearchFormPage.DEPARTURE_TIME).getText();
    String numberOfStopsOfFastestArrivalTime =
        rowWithFastestArrivalTime
            .find(RegioSearchFormPage.NUMBER_OF_STOPS)
            .getText()
            .replace("Prestup: ", "");
    String priceOfFastestArrivalTime =
        rowWithFastestArrivalTime
            .find(RegioSearchFormPage.PRICE)
            .getText()
            .replace("od ", "")
            .replaceAll("\n.*", "");
    String travelTimeOfFastestArrivalTime =
        rowWithFastestArrivalTime
            .find(RegioSearchFormPage.TRAVEL_TIME)
            .getText()
            .replace("Doba cesty\n", "");

    String departureTimeOfLowestPrice =
        rowWithLowestPrice.find(RegioSearchFormPage.DEPARTURE_TIME).getText();
    String arrivalTimeOfLowestPrice =
        rowWithLowestPrice.find(RegioSearchFormPage.ARRIVAL_TIME).getText();
    String numberOfStopsOfLowestPrice =
        rowWithLowestPrice
            .find(RegioSearchFormPage.NUMBER_OF_STOPS)
            .getText()
            .replace("Prestup: ", "");
    String travelTimeOfLowestPrice =
        rowWithLowestPrice
            .find(RegioSearchFormPage.TRAVEL_TIME)
            .getText()
            .replace("Doba cesty\n", "");

    String priceOfShortestTravelTime =
        rowWithShortestTravelTime
            .find(RegioSearchFormPage.PRICE)
            .getText()
            .replace("od ", "")
            .replaceAll("\n.*", "");

    String departureTimeOfShortestTravelTime =
        rowWithShortestTravelTime.find(RegioSearchFormPage.DEPARTURE_TIME).getText();
    String arrivalTimeOfShortestTravelTime =
        rowWithShortestTravelTime.find(RegioSearchFormPage.ARRIVAL_TIME).getText();
    String numberOfStopsOfShortestTravelTime =
        rowWithShortestTravelTime
            .find(RegioSearchFormPage.NUMBER_OF_STOPS)
            .getText()
            .replace("Prestup: ", "");

    rowWithLowestPrice.click();
    $(RegioSearchFormPage.LOADER).should(Condition.disappears);
    rowWithShortestTravelTime.click();
    $(RegioSearchFormPage.LOADER).should(Condition.disappears);
    rowWithFastestArrivalTime.click();
    $(RegioSearchFormPage.LOADER).should(Condition.disappears);

    System.out.println("===================== FASTEST ARRIVAL TIME ===========================");
    System.out.println("The fastest arrival time is ".concat(fastestArrivalTime).concat("h"));
    System.out.println(
        "The departure time of fastest arrival time is "
            .concat(departureTimeOfFastestArrivalTime)
            .concat("h"));
    System.out.println(
        "The travel time of fastest arrival time is ".concat(travelTimeOfFastestArrivalTime));
    System.out.println(
        "The number of stop of fastest arrival time is ".concat(numberOfStopsOfFastestArrivalTime));
    System.out.println(
        "The price of fastest arrival time is ".concat(priceOfFastestArrivalTime).concat("\u20ac"));
    System.out.println("======================================================================");

    System.out.println("===================== SHORTEST TRAVEL TIME ===========================");
    System.out.println("The shortest travel time is ".concat(shortestTravelTime).concat("h"));
    System.out.println(
        "The arrival time of shortest travel time is "
            .concat(arrivalTimeOfShortestTravelTime)
            .concat("h"));
    System.out.println(
        "The departure time of shortest travel time is "
            .concat(departureTimeOfShortestTravelTime)
            .concat("h"));
    System.out.println(
        "The number of stop of shortest travel time is ".concat(numberOfStopsOfShortestTravelTime));
    System.out.println("The price of shortest travel time is ".concat(priceOfShortestTravelTime));
    System.out.println("======================================================================");

    System.out.println("===================== LOWEST PRICE======== ===========================");
    System.out.println("The lowest price is ".concat(lowestPrice).concat("\u20ac"));
    System.out.println(
        "The departure time of lowest price is ".concat(departureTimeOfLowestPrice).concat("h"));
    System.out.println(
        "The arrival time of lowest price is ".concat(arrivalTimeOfLowestPrice).concat("h"));
    System.out.println("The travel time of lowest price is ".concat(travelTimeOfLowestPrice));
    System.out.println("The number of stop of lowest price is ".concat(numberOfStopsOfLowestPrice));
    System.out.println("======================================================================");
    System.out.println(
        "Now you have "
            + SECONDS_COUNT
            + "seconds for check values in console output and chromedriver...");

    try {
      Thread.sleep(SECONDS_COUNT * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void searchingUsingApi() {
    SimpleRouteSearchResult simpleRouteSearchResult = sendApiRequest();

    Double lowestPrice =
        simpleRouteSearchResult.getRoutes().stream()
            .map(SimpleRoute::getPriceFrom)
            .sorted()
            .collect(Collectors.toList())
            .get(0);

    String lowestPriceRouteId =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getPriceFrom().equals(lowestPrice))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getId();

    String departureTimeOfLowestPrice =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(lowestPriceRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getDepartureTime();

    departureTimeOfLowestPrice =
        String.valueOf(
            departureTimeOfLowestPrice.subSequence(
                departureTimeOfLowestPrice.indexOf("T") + 1,
                departureTimeOfLowestPrice.indexOf("T") + 6));

    String arrivalTimeOfLowestPrice =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(lowestPriceRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getArrivalTime();

    arrivalTimeOfLowestPrice =
        String.valueOf(
            arrivalTimeOfLowestPrice.subSequence(
                arrivalTimeOfLowestPrice.indexOf("T") + 1,
                arrivalTimeOfLowestPrice.indexOf("T") + 6));

    String travelTimeOfLowestPrice =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(lowestPriceRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getTravelTime();

    Integer numberOfStopsOfLowestPrice =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(lowestPriceRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getTransfersCount();

    String fastestArrivalTime =
        simpleRouteSearchResult.getRoutes().stream()
            .map(SimpleRoute::getArrivalTime)
            .sorted()
            .collect(Collectors.toList())
            .get(0);

    String fastestArrivalTimeOutput =
        String.valueOf(
            fastestArrivalTime.subSequence(
                fastestArrivalTime.indexOf("T") + 1, fastestArrivalTime.indexOf("T") + 6));

    String fastestArrivalTimeRouteId =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getArrivalTime().equals(fastestArrivalTime))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getId();

    String departureTimeOfFastestArrivalTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(fastestArrivalTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getDepartureTime();

    departureTimeOfFastestArrivalTime =
        String.valueOf(
            departureTimeOfFastestArrivalTime.subSequence(
                departureTimeOfFastestArrivalTime.indexOf("T") + 1,
                departureTimeOfFastestArrivalTime.indexOf("T") + 6));

    String travelTimeOfFastestArrivalTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(fastestArrivalTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getTravelTime();

    Integer numberOfStopsOfFastestArrivalTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(fastestArrivalTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getTransfersCount();

    Double priceOfFastestArrivalTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(fastestArrivalTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getPriceFrom();

    String shortestTravelTime =
        simpleRouteSearchResult.getRoutes().stream()
            .map(SimpleRoute::getTravelTime)
            .sorted()
            .collect(Collectors.toList())
            .get(0);

    String shortestTimeRouteId =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getTravelTime().equals(shortestTravelTime))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getId();

    String departureTimeOfShortestTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(shortestTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getDepartureTime();

    departureTimeOfShortestTime =
        String.valueOf(
            departureTimeOfShortestTime.subSequence(
                departureTimeOfShortestTime.indexOf("T") + 1,
                departureTimeOfShortestTime.indexOf("T") + 6));

    String arrivalTimeOfShortestTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(shortestTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getArrivalTime();

    arrivalTimeOfShortestTime =
        String.valueOf(
            arrivalTimeOfShortestTime.subSequence(
                arrivalTimeOfShortestTime.indexOf("T") + 1,
                arrivalTimeOfShortestTime.indexOf("T") + 6));

    Integer numberOfStopsOfShortestTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(shortestTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getTransfersCount();

    Double priceOfShortestTime =
        simpleRouteSearchResult.getRoutes().stream()
            .filter(a -> a.getId().equals(shortestTimeRouteId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Route ID was not found."))
            .getPriceFrom();

    System.out.println("===================== FASTEST ARRIVAL TIME ===========================");
    System.out.println("The fastest arrival time is ".concat(fastestArrivalTimeOutput).concat("h"));
    System.out.println(
        "The departure time of fastest arrival time is "
            .concat(departureTimeOfFastestArrivalTime)
            .concat("h"));
    System.out.println(
        "The travel time of fastest arrival time is ".concat(travelTimeOfFastestArrivalTime));
    System.out.println(
        "The number of stop of fastest arrival time is "
            .concat(numberOfStopsOfFastestArrivalTime.toString()));
    System.out.println(
        "The price of fastest arrival time is "
            .concat(priceOfFastestArrivalTime.toString().concat("\u20ac")));
    System.out.println("======================================================================");

    System.out.println("===================== SHORTEST TRAVEL TIME ===========================");
    System.out.println("The shortest travel time is ".concat(shortestTravelTime).concat("h"));
    System.out.println(
        "The arrival time of shortest travel time is "
            .concat(arrivalTimeOfShortestTime)
            .concat("h"));
    System.out.println(
        "The departure time of shortest travel time is "
            .concat(departureTimeOfShortestTime)
            .concat("h"));
    System.out.println(
        "The number of stop of shortest travel time is "
            .concat(numberOfStopsOfShortestTime.toString()));
    System.out.println(
        "The price of shortest travel time is "
            .concat(priceOfShortestTime.toString().concat("\u20ac")));
    System.out.println("======================================================================");

    System.out.println("===================== LOWEST PRICE======== ===========================");
    System.out.println("The lowest price is ".concat(lowestPrice.toString()).concat("\u20ac"));
    System.out.println(
        "The departure time of lowest price is ".concat(departureTimeOfLowestPrice).concat("h"));
    System.out.println(
        "The arrival time of lowest price is ".concat(arrivalTimeOfLowestPrice).concat("h"));
    System.out.println("The travel time of lowest price is ".concat(travelTimeOfLowestPrice));
    System.out.println(
        "The number of stop of lowest price is ".concat(numberOfStopsOfLowestPrice.toString()));
    System.out.println("======================================================================");
  }

  public static SimpleRouteSearchResult sendApiRequest() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Host", "brn-ybus-pubapi.sa.cz");
    headers.put("X-Lang", "sk");
    headers.put("X-Currency", "EUR");

    String day =
        LocalDate.now()
            .with(TemporalAdjusters.next(DAY_OF_WEEK))
            .format(DateTimeFormatter.ofPattern(DATE_FORMAT_API));

    Map<String, String> params = new HashMap<>();
    params.put("departureDate", day);
    params.put("fromLocationId", OSTRAVA_LOCATION_ID);
    params.put("toLocationId", BRNO_LOCATION_ID);
    params.put("fromLocationType", LOCATION_TYPE);
    params.put("toLocationType", LOCATION_TYPE);
    params.put("tariffs", TARIFFS);

    Response response =
        given()
            .headers(headers)
            .queryParams(params)
            .log()
            .all()
            .relaxedHTTPSValidation()
            .baseUri("https://brn-ybus-pubapi.sa.cz/restapi/routes/search/simple")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .get();

    return validateAndExtractResponse(200, SimpleRouteSearchResult.class, response);
  }

  public static <T> T validateAndExtractResponse(
      Integer statusCode, Class<T> clazz, Response response) {
    return response
        .then()
        .log()
        .status()
        .log()
        .body()
        .statusCode(statusCode)
        .extract()
        .body()
        .as(clazz);
  }
}
