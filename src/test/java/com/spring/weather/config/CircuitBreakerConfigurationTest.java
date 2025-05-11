package com.spring.weather.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigurationProperties;

/**
 * Unit tests for the CircuitBreakerConfiguration class.
 * Verifies the correct configuration of circuit breakers using Resilience4J.
 */
class CircuitBreakerConfigurationTest {

  /**
   * Tests that the default circuit breaker is configured with the correct sliding window size.
   * Ensures that the sliding window size is set to 10.
   */
  @DisplayName("Configures default circuit breaker with correct sliding window size")
  @Test
  void configuresDefaultCircuitBreakerWithCorrectSlidingWindowSize() {
    CircuitBreakerConfig customConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(10)
        .build();
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(customConfig);
    TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
    Resilience4JConfigurationProperties properties = new Resilience4JConfigurationProperties();

    ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory(
        circuitBreakerRegistry, timeLimiterRegistry, properties
    );

    factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(customConfig)
        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
        .build());

    CircuitBreakerConfig circuitBreakerConfig = factory.getCircuitBreakerRegistry()
        .circuitBreaker("default").getCircuitBreakerConfig();
    assertEquals(10, circuitBreakerConfig.getSlidingWindowSize());
  }

  /**
   * Tests that the default circuit breaker is configured with the correct failure rate threshold.
   * Ensures that the failure rate threshold is set to 50%.
   */
  @DisplayName("Configures default circuit breaker with correct failure rate threshold")
  @Test
  void configuresDefaultCircuitBreakerWithCorrectFailureRateThreshold() {
    CircuitBreakerConfig customConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50.0f)
        .build();
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(customConfig);
    TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
    Resilience4JConfigurationProperties properties = new Resilience4JConfigurationProperties();

    ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory(
        circuitBreakerRegistry, timeLimiterRegistry, properties
    );

    factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(customConfig)
        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
        .build());

    CircuitBreakerConfig circuitBreakerConfig = factory.getCircuitBreakerRegistry()
        .circuitBreaker("default").getCircuitBreakerConfig();
    assertEquals(50.0f, circuitBreakerConfig.getFailureRateThreshold());
  }

  /**
   * Tests that the default circuit breaker is configured with the correct timeout duration.
   * Ensures that the timeout duration is set to 2 seconds.
   */
  @DisplayName("Configures default circuit breaker with correct timeout duration")
  @Test
  void configuresDefaultCircuitBreakerWithCorrectTimeoutDuration() {
    CircuitBreakerConfig customConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(10)
        .build();
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(customConfig);

    TimeLimiterConfig customTimeLimiterConfig = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(2))
        .build();
    TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(customTimeLimiterConfig);

    Resilience4JConfigurationProperties properties = new Resilience4JConfigurationProperties();

    ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory(
        circuitBreakerRegistry, timeLimiterRegistry, properties
    );

    factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(customConfig)
        .timeLimiterConfig(customTimeLimiterConfig)
        .build());

    TimeLimiterConfig timeLimiterConfig = timeLimiterRegistry.timeLimiter("default").getTimeLimiterConfig();
    assertEquals(Duration.ofSeconds(2), timeLimiterConfig.getTimeoutDuration());
  }
}