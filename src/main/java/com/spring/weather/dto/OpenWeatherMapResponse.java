package com.spring.weather.dto;

import lombok.Data;

/**
 * Represents the response from the OpenWeatherMap API.
 * Contains weather-related data such as temperature , wind speed and error information.
 */
@Data
public class OpenWeatherMapResponse {
  private Main main;
  private Wind wind;
  private penWeatherMapError error;

  @Data
  public static class Main {
    private double temp;
  }

  @Data
  public static class Wind {
    private double speed;
  }

  @Data
  public static class penWeatherMapError {
    private int code;
    private String type;
    private String info;
  }
}