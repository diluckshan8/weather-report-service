package com.spring.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the response from the WeatherStack API.
 * Contains weather-related data such as current weather details and error information.
 */
@Data
public class WeatherStackResponse {
  private Current current;
  private WeatherStackError error;

  @Data
  public static class Current {
    private double temperature;
    @JsonProperty("wind_speed")
    private double windSpeed;
  }

  @Data
  public static class WeatherStackError {
    private int code;
    private String type;
    private String info;
  }

}
