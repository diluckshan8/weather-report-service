package com.spring.weather.exception;

import lombok.Getter;

/**
 * Represents the response from the WeatherStack API.
 * Contains weather-related data such as current weather details and error information.
 */
@Getter
public class WeatherStackApiException extends RuntimeException {
  private final int code;
  private final String type;
  private final String info;

  public WeatherStackApiException(int code, String type, String info) {
    super(String.format("WeatherStack API Error - Code: %d, Type: %s, Info: %s", code, type, info));
    this.code = code;
    this.type = type;
    this.info = info;
  }
}
