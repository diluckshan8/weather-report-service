package com.spring.weather.provider;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.dto.OpenWeatherMapResponse;
import com.spring.weather.exception.ExternalRequestException;
import com.spring.weather.exception.InvalidAccessKeyException;
import com.spring.weather.exception.MissingQueryException;
import com.spring.weather.exception.WeatherProviderException;
import com.spring.weather.exception.WeatherServiceException;
import com.spring.weather.exception.WeatherStackApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * Weather provider implementation for OpenWeatherMap.
 * Fetches weather data from the OpenWeatherMap API and converts it into a standard format.
 */
@Component
public class OpenWeatherMapProvider extends AbstractWeatherProvider {

  private static final String PROVIDER_NAME = "OpenWeatherMap";
  private static final int PROVIDER_PRIORITY = 2; // Secondary provider
  private static final String COUNTRY_CODE = "AU";
  private static final String PROVIDER_METRIC = "metric";

  /**
   * Constructs a new OpenWeatherMapProvider with the specified dependencies.
   *
   * @param webClient             The WebClient instance for making HTTP requests.
   * @param circuitBreakerFactory The factory for creating ReactiveCircuitBreaker instances.
   * @param apiKey                The API key for authenticating requests to the OpenWeatherMap API.
   * @param baseUrl               The base URL of the OpenWeatherMap API.
   */
  public OpenWeatherMapProvider(
      WebClient webClient,
      ReactiveCircuitBreakerFactory circuitBreakerFactory,
      @Value("${openweathermap.api.key}") String apiKey,
      @Value("${openweathermap.api.url}") String baseUrl) {
    super(webClient, circuitBreakerFactory, apiKey, baseUrl, "openweathermap");
  }

  /**
   * Fetches weather data for the specified city from the OpenWeatherMap API.
   *
   * @param city The name of the city for which to fetch weather data.
   * @return A Mono emitting the weather data in a standardized format.
   */
  @Override
  public Mono<WeatherResponse> getWeatherData(String city) {
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
        .queryParam("q", city + ","+ COUNTRY_CODE)
        .queryParam("appid", apiKey)
        .queryParam("units", PROVIDER_METRIC)
        .build()
        .toUriString();

    return executeRequest(url, OpenWeatherMapResponse.class).onErrorMap(this::mapWeatherStackError);
  }

  /**
   * Maps errors from the WeatherStack API to specific exceptions.
   *
   * @param error The error to map.
   * @return A Throwable representing the mapped exception.
   */
  private Throwable mapWeatherStackError(Throwable error) {
    return new WeatherServiceException("Weather service error: " + error.getMessage());
  }

  /**
   * Converts the raw response from the OpenWeatherMap API into a WeatherResponse object.
   *
   * @param response The raw response from the OpenWeatherMap API.
   * @param <T>      The type of the raw response.
   * @return The converted WeatherResponse object.
   * @throws WeatherProviderException If the response is invalid or missing required data.
   */
  @Override
  protected <T> WeatherResponse convertToWeatherResponse(T response) {
    if (!(response instanceof OpenWeatherMapResponse openWeatherMapResponse)) {
      throw new WeatherProviderException("Invalid response from OpenWeatherMap");
    }

    if (openWeatherMapResponse.getError() != null) {
      throw new WeatherStackApiException(
          openWeatherMapResponse.getError().getCode(),
          openWeatherMapResponse.getError().getType(),
          openWeatherMapResponse.getError().getInfo()
      );
    }

    if (openWeatherMapResponse.getMain() == null) {
      throw new WeatherProviderException("Invalid response from OpenWeatherMap: missing current weather data");
    }

    return new WeatherResponse(
        openWeatherMapResponse.getWind().getSpeed(),
        openWeatherMapResponse.getMain().getTemp()
    );
  }

  /**
   * Returns the name of the weather provider.
   *
   * @return The name of the provider.
   */
  @Override
  public String getProviderName() {
    return PROVIDER_NAME;
  }

  /**
   * Returns the priority of the weather provider.
   *
   * @return The priority of the provider.
   */
  @Override
  public int getPriority() {
    return PROVIDER_PRIORITY;
  }
}