package com.spring.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the weather response data.
 * Contains information about wind speed and temperature.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
  @JsonProperty("wind_speed")
  private double windSpeed;

  @JsonProperty("temperature_degrees")
  private double temperatureDegrees;
}
