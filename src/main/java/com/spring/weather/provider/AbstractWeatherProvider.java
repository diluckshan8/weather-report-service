package com.spring.weather.provider;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.exception.WeatherProviderException;
import com.spring.weather.exception.WeatherStackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Abstract base class for weather providers.
 * Provides common functionality for executing HTTP requests and handling errors.
 */
@Slf4j
public abstract class AbstractWeatherProvider implements WeatherProvider {
  protected final WebClient webClient;
  protected final ReactiveCircuitBreaker circuitBreaker;
  protected final String apiKey;
  protected final String baseUrl;

  /**
   * Constructs a new AbstractWeatherProvider with the specified dependencies.
   *
   * @param webClient The WebClient instance for making HTTP requests.
   * @param circuitBreakerFactory The factory for creating ReactiveCircuitBreaker instances.
   * @param apiKey The API key for authenticating requests.
   * @param baseUrl The base URL of the weather provider's API.
   * @param providerName The name of the weather provider, used for circuit breaker configuration.
   */
  protected AbstractWeatherProvider(
      WebClient webClient,
      ReactiveCircuitBreakerFactory circuitBreakerFactory,
      String apiKey,
      String baseUrl,
      String providerName) {
    this.webClient = webClient;
    this.circuitBreaker = circuitBreakerFactory.create(providerName);
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  /**
   * Executes an HTTP GET request to the specified URL and processes the response.
   * Uses a circuit breaker to handle failures gracefully.
   *
   * @param url The URL to send the request to.
   * @param responseType The expected type of the response body.
   * @param <T> The type of the response body.
   * @return A Mono emitting the converted WeatherResponse object or an error.
   */
  protected <T> Mono<WeatherResponse> executeRequest(String url, Class<T> responseType) {
    return circuitBreaker.run(
        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(responseType)
            .doOnSuccess(response -> log.info("Successfully fetched data from {} for current request", getProviderName()))
            .map(this::convertToWeatherResponse),
        throwable -> {
          log.error("Circuit breaker triggered for {}: {}", getProviderName(), throwable.getMessage());
          if (throwable instanceof WeatherStackApiException weatherStackApiException) {
            return  Mono.error(new WeatherStackApiException(weatherStackApiException.getCode(), weatherStackApiException.getType(),weatherStackApiException.getInfo()));
          }
          return Mono.error(
              new WeatherProviderException(getProviderName() + " service is currently unavailable", throwable));
        }
    );
  }


  /**
   * Converts the raw response from the weather provider into a WeatherResponse object.
   * This method must be implemented by subclasses.
   *
   * @param response The raw response from the weather provider.
   * @param <T> The type of the raw response.
   * @return The converted WeatherResponse object.
   */
  protected abstract <T> WeatherResponse convertToWeatherResponse(T response);


}
