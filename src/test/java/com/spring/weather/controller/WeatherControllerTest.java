package com.spring.weather.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.exception.ExternalRequestException;
import com.spring.weather.exception.GlobalExceptionHandler;
import com.spring.weather.exception.InvalidAccessKeyException;
import com.spring.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

/**
 * Unit tests for the WeatherController class.
 * Verifies the behavior of the WeatherController endpoints under various scenarios.
 */
@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

  @Mock
  private WeatherService weatherService;

  @InjectMocks
  private WeatherController weatherController;

  private WebTestClient webTestClient;

  /**
   * Sets up the WebTestClient with the WeatherController and GlobalExceptionHandler.
   * This method is executed before each test.
   */
  @BeforeEach
  public void setup() {
    webTestClient = WebTestClient.bindToController(weatherController)
        .controllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  /**
   * Tests the scenario where weather data is successfully retrieved for a valid city.
   * Verifies that the response contains the correct weather data.
   */
  @Test
  @DisplayName("Should return weather data for a valid city")
  public void shouldReturnWeatherData() {
    // Given
    WeatherResponse weatherResponse = new WeatherResponse(20.0, 29.0);
    when(weatherService.getWeatherData(anyString())).thenReturn(Mono.just(weatherResponse));

    // When & Then
    webTestClient.get()
        .uri("/v1/weather?city=Melbourne")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.wind_speed").isEqualTo(20.0)
        .jsonPath("$.temperature_degrees").isEqualTo(29.0);
  }

  /**
   * Tests the scenario where an invalid API key is used.
   * Verifies that a 401 status and appropriate error message are returned.
   */
  @Test
  @DisplayName("Should return 401 for an invalid API key")
  public void shouldReturn401ForInvalidApiKey() {
    when(weatherService.getWeatherData("melbourne"))
        .thenReturn(Mono.error(new InvalidAccessKeyException("Invalid API access key provided")));

    webTestClient.get()
        .uri("/v1/weather?city=melbourne")
        .exchange()
        .expectStatus().isUnauthorized()
        .expectBody()
        .jsonPath("$.status").isEqualTo(401)
        .jsonPath("$.message").isEqualTo("Invalid API access key provided");
  }

  /**
   * Tests the scenario where an invalid API version is requested.
   * Verifies that a 404 status is returned due to no route mapping.
   */
  @Test
  @DisplayName("Should return 404 for an invalid API version")
  public void shouldReturn404ForInvalidApiVersion() {
    WebTestClient client = WebTestClient.bindToController(weatherController).build();

    client.get()
        .uri("/v2/weather?city=melbourne")
        .exchange()
        .expectStatus().isNotFound(); // no route mapping
  }
}
