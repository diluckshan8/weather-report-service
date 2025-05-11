package com.spring.weather.service;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.exception.ExternalRequestException;
import com.spring.weather.exception.WeatherServiceException;
import com.spring.weather.factory.WeatherProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the WeatherService interface.
 * Provides functionality to fetch weather data for a given city using multiple weather providers.
 * Includes caching and fallback mechanisms for handling provider failures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

  private final WeatherProviderFactory weatherProviderFactory;
  private final CacheManager cacheManager;

  /**
   * Fetches weather data for the specified city.
   * Uses caching to store and retrieve weather data for cities.
   *
   * @param city The name of the city for which to fetch weather data.
   * @return A Mono emitting the weather data in a standardized format.
   */
  @Override
  @Cacheable(value = "weatherCache", key = "#city.toLowerCase()")
  public Mono<WeatherResponse> getWeatherData(String city) {
    log.info("Fetching weather data for city: {}", city);

    // First, validate the city name
    if (!isValidCityName(city)) {
      return Mono.error(new WeatherServiceException("Invalid city name: " + city));
    }

    return tryProviders(city, 0)
        .onErrorResume(e -> {

          log.error("All weather providers failed for {}: {}", city, e.getMessage());
          WeatherResponse staleData = getStaleDataFromCache(city);
          if (staleData != null) {
            log.info("Returning stale data for {}", city);
            return Mono.just(staleData);
          }
          return Mono.error(e);
        });
  }


  /**
   * Attempts to fetch weather data from the available providers in sequence.
   * If a provider fails, it falls back to the next provider.
   *
   * @param city The name of the city for which to fetch weather data.
   * @param providerIndex The index of the current provider to try.
   * @return A Mono emitting the weather data or an error if all providers fail.
   */
  private Mono<WeatherResponse> tryProviders(String city, int providerIndex) {
    var providers = weatherProviderFactory.getProviders();

    if (providerIndex >= providers.size()) {
      return Mono.error(new WeatherServiceException("No more providers available"));
    }

    var currentProvider = providers.get(providerIndex);
    return currentProvider.getWeatherData(city)
        .onErrorResume(e -> {
          log.warn("Failed to fetch weather from {} for {}: {}",currentProvider.getProviderName(), city, e.getMessage());
          if(e instanceof ExternalRequestException) {
            return tryProviders(city, providerIndex + 1);
          }
          return Mono.error(e);
        });
  }

  /**
   * Retrieves stale weather data from the cache for the specified city.
   *
   * @param city The name of the city for which to retrieve stale data.
   * @return The cached WeatherResponse or null if no data is available.
   */
  private WeatherResponse getStaleDataFromCache(String city) {
    Cache weatherCache = cacheManager.getCache("weatherCache");
    if (weatherCache != null) {
      Cache.ValueWrapper valueWrapper = weatherCache.get(city.toLowerCase());
      if (valueWrapper != null) {
        return (WeatherResponse) valueWrapper.get();
      }
    }
    return null;
  }

  /**
   * Validates the city name to ensure it is not null or empty.
   *
   * @param city The city name to validate.
   * @return True if the city name is valid, false otherwise.
   */
  private boolean isValidCityName(String city) {
    return city != null && !city.trim().isEmpty();
  }
}
