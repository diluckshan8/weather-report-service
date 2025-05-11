package com.spring.weather.provider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.spring.weather.dto.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit tests for the AbstractWeatherProvider class.
 * Verifies the behavior of the abstract provider implementation and its request handling.
 */
@ExtendWith(MockitoExtension.class)
class AbstractWeatherProviderTest {

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private ReactiveCircuitBreakerFactory circuitBreakerFactory;

  @Mock
  private ReactiveCircuitBreaker circuitBreaker;

  private TestWeatherProvider provider;

  /**
   * Sets up the test environment by initializing mocks and creating a test provider instance.
   */
  @BeforeEach
  void setUp() {
    when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

    provider = new TestWeatherProvider(webClient, circuitBreakerFactory);
  }

  /**
   * Tests the scenario where a request is successfully handled.
   * Verifies that the response is correctly converted to a WeatherResponse object.
   */
  @Test
  @DisplayName("Should handle successful request")
  void shouldHandleSuccessfulRequest() {
    // Given
    TestResponse testResponse = new TestResponse(20.0, 29.0);
    when(responseSpec.bodyToMono(TestResponse.class)).thenReturn(Mono.just(testResponse));
    when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(inv -> inv.getArgument(0));

    // When & Then
    StepVerifier.create(provider.getWeatherData("Melbourne"))
        .expectNext(new WeatherResponse(20.0, 29.0))
        .verifyComplete();
  }

  /**
   * Test implementation of AbstractWeatherProvider for unit testing purposes.
   */
  private static class TestWeatherProvider extends AbstractWeatherProvider {
    public TestWeatherProvider(WebClient webClient, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
      super(webClient, circuitBreakerFactory, "test-api-key", "http://test.api", "test");
    }

    @Override
    public Mono<WeatherResponse> getWeatherData(String city) {
      return executeRequest("http://test.api/weather?city=" + city, TestResponse.class);
    }

    @Override
    protected <T> WeatherResponse convertToWeatherResponse(T response) {
      TestResponse testResponse = (TestResponse) response;
      return new WeatherResponse(testResponse.getWindSpeed(), testResponse.getTemperature());
    }

    @Override
    public String getProviderName() {
      return "TestProvider";
    }

    @Override
    public int getPriority() {
      return 1;
    }
  }

  /**
   * Test response class to simulate API responses.
   */
  private static class TestResponse {
    private final double windSpeed;
    private final double temperature;

    public TestResponse(double windSpeed, double temperature) {
      this.windSpeed = windSpeed;
      this.temperature = temperature;
    }

    public double getWindSpeed() {
      return windSpeed;
    }

    public double getTemperature() {
      return temperature;
    }
  }
}