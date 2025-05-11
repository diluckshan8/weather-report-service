package com.spring.weather.provider;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.dto.WeatherStackResponse;
import com.spring.weather.exception.ExternalRequestException;
import com.spring.weather.exception.InvalidAccessKeyException;
import com.spring.weather.exception.MissingQueryException;
import com.spring.weather.exception.UnlimitedUsageException;
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
 * Weather provider implementation for WeatherStack.
 * Fetches weather data from the WeatherStack API and converts it into a standard format.
 */
@Component
public class WeatherStackProvider extends AbstractWeatherProvider {

  private static final String PROVIDER_NAME = "WeatherStack";
  private static final int PROVIDER_PRIORITY = 1; // Primary provider

  /**
   * Constructs a new WeatherStackProvider with the specified dependencies.
   *
   * @param webClient             The WebClient instance for making HTTP requests.
   * @param circuitBreakerFactory The factory for creating ReactiveCircuitBreaker instances.
   * @param apiKey                The API key for authenticating requests to the WeatherStack API.
   * @param baseUrl               The base URL of the WeatherStack API.
   */
  public WeatherStackProvider(WebClient webClient, ReactiveCircuitBreakerFactory circuitBreakerFactory,
      @Value("${weatherstack.api.key}") String apiKey, @Value("${weatherstack.api.url}") String baseUrl) {
    super(webClient, circuitBreakerFactory, apiKey, baseUrl, "weatherstack");
  }

  /**
   * Fetches weather data for the specified city from the WeatherStack API.
   *
   * @param city The name of the city for which to fetch weather data.
   * @return A Mono emitting the weather data in a standardized format.
   */
  @Override
  public Mono<WeatherResponse> getWeatherData(String city) {
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/current").queryParam("access_key", apiKey)
        .queryParam("query", city).build().toUriString();

    return executeRequest(url, WeatherStackResponse.class)
        .onErrorMap(this::mapWeatherStackError);
  }

  /**
   * Maps errors from the WeatherStack API to specific exceptions.
   *
   * @param error The error to map.
   * @return A Throwable representing the mapped exception.
   */
  private Throwable mapWeatherStackError(Throwable error) {
    if (error instanceof WeatherStackApiException apiError) {
      return switch (apiError.getCode()) {
        case 101 -> {
          if ("invalid_access_key".equals(apiError.getType())) {
            yield new InvalidAccessKeyException("Invalid API access key provided");
          } else {
            yield new InvalidAccessKeyException("Missing API access key");
          }
        }
        case 104 -> new UnlimitedUsageException("usage limit reached");
        case 601 -> new MissingQueryException("Missing or invalid location query");
        case 615 -> new ExternalRequestException("External Service Down error: Request failed");
        default -> new WeatherServiceException("Weather service error: " + apiError.getInfo());
      };
    }
    return new WeatherServiceException("Weather service error: " + error.getMessage());
  }

  /**
   * Converts the raw response from the WeatherStack API into a WeatherResponse object.
   *
   * @param response The raw response from the WeatherStack API.
   * @param <T>      The type of the raw response.
   * @return The converted WeatherResponse object.
   * @throws WeatherProviderException If the response is invalid or missing required data.
   */
  @Override
  protected <T> WeatherResponse convertToWeatherResponse(T response) {
    if (!(response instanceof WeatherStackResponse weatherStackResponse)) {
      throw new WeatherProviderException("Invalid response from WeatherStack");
    }

    // Check for error response
    if (weatherStackResponse.getError() != null) {
      throw new WeatherStackApiException(weatherStackResponse.getError().getCode(),
          weatherStackResponse.getError().getType(), weatherStackResponse.getError().getInfo());
    }

    if (weatherStackResponse.getCurrent() == null) {
      throw new WeatherProviderException("Invalid response from WeatherStack: missing current weather data");
    }

    return new WeatherResponse(weatherStackResponse.getCurrent().getWindSpeed(),
        weatherStackResponse.getCurrent().getTemperature());
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