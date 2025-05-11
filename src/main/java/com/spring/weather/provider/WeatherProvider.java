package com.spring.weather.provider;

import com.spring.weather.dto.WeatherResponse;
import reactor.core.publisher.Mono;

/**
 * Interface for weather providers. Defines the contract for fetching weather data and providing metadata about the
 * provider.
 */
public interface WeatherProvider {

  /**
   * Fetches weather data for the specified city.
   *
   * @param city The name of the city for which to fetch weather data.
   * @return A Mono emitting the weather data in a standardized format.
   */
  Mono<WeatherResponse> getWeatherData(String city);

  /**
   * Returns the name of the weather provider.
   *
   * @return The name of the weather provider.
   */
  String getProviderName();

  /**
   * Returns the priority of the weather provider. Providers with lower priority values are preferred.
   *
   * @return The priority of the weather provider.
   */
  int getPriority();
}
