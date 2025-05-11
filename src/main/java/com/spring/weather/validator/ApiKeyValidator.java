package com.spring.weather.validator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Component responsible for validating API keys required for weather providers.
 * Ensures that the necessary API keys are present and logs errors if they are missing.
 */
@Component
@Slf4j
public class ApiKeyValidator {

  /**
   * API key for the WeatherStack service.
   * Retrieved from the application properties.
   */
  @Value("${weatherstack.api.key:}")
  private String weatherStackKey;

  /**
   * API key for the OpenWeatherMap service.
   * Retrieved from the application properties.
   */
  @Value("${openweathermap.api.key:}")
  private String openWeatherMapKey;

  /**
   * Validates the presence of API keys after the component is initialized.
   * Logs an error and throws an exception if any required API key is missing.
   *
   * @throws IllegalStateException if any API key is missing.
   */
  @PostConstruct
  public void validateKeys() {
    if (weatherStackKey.isBlank()) {
      log.error("WeatherStack API key is missing!");
      throw new IllegalStateException("Missing WeatherStack API key");
    }
    if (openWeatherMapKey.isBlank()) {
      log.error("OpenWeatherMap API key is missing!");
      throw new IllegalStateException("Missing OpenWeatherMap API key");
    }
    log.info("API keys validated successfully.");
  }
}
