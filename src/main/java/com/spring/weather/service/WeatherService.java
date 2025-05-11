package com.spring.weather.service;

import com.spring.weather.dto.WeatherResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for fetching weather data.
 * Provides a contract for retrieving weather information for a given city.
 */
public interface WeatherService {

  /**
   * Fetches weather data for the specified city.
   *
   * @param city The name of the city for which to fetch weather data.
   * @return A Mono emitting the weather data in a standardized format.
   */
  Mono<WeatherResponse> getWeatherData(String city);
}
