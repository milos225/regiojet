package pages;

import org.openqa.selenium.By;

public class RegioSearchFormPage {

  // Search
  public static final By FROM = By.id("route-from");
  public static final By FROM_SUGGESTION_ITEM =
      By.xpath("//div[@id = 'react-select-route-from--list']/div");
  public static final By TO = By.id("route-to");
  public static final By TO_SUGGESTION_ITEM =
      By.xpath("//div[@id = 'react-select-route-to--list']/div");
  public static final By DATE_THERE = By.id("route-there-input");
  public static final By DATE_THERE_CLEAR_BTN =
      By.xpath("//div[contains(@class, 'date-select')][1]//button");
  public static final By DATE_BACK = By.id("route-back-input");
  public static final By DATE_BACK_CLEAR_BTN =
      By.xpath("//div[contains(@class, 'date-select')][2]//button");
  public static final By SEARCH_BTN = By.id("search-button");
  public static final By LOADER = By.className("loader-wrapper");

  // Results
  public static final By RESULTS_TAB = By.className("connections-wrap");
  public static final By ARRIVAL_TIME = By.xpath(".//div[contains(@class, 'times')]/span[5]");
  public static final By DEPARTURE_TIME = By.xpath(".//div[contains(@class, 'times')]/span[2]");
  public static final By TRAVEL_TIME = By.className("travel-time");
  public static final By PRICE = By.xpath(".//*[@id='price-yellow-desktop']/div");
  public static final By NUMBER_OF_STOPS = By.id("transfers-desktop");

  public static String ROW_WITH_LOWEST_PRICE_XPATH =
      "//div[contains(@class, 'connection-detail') and .//*[contains(., '%s')]]";
  public static String ROW_WITH_FASTEST_ARRIVAL_TIME_XPATH =
      ".//div[contains(@class, 'connection-detail') and .//*[contains(@class, 'times')]/span[5]/span[contains(., '%s')]]";
  public static String ROW_WITH_SHORTEST_TRAVEL_TIME_XPATH =
      "//div[contains(@class, 'connection-detail') and .//*[contains(@class, 'travel-time') and contains(., '%s')]]";


}
