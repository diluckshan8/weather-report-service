package com.spring.weather.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

  /**
   * Configures the default behavior for all circuit breakers in the application.
   *
   * @param slidingWindowSize The size of the sliding window used to calculate the failure rate.
   * @param failureRateThreshold The failure rate threshold (in percentage) that triggers the circuit breaker to open.
   * @param waitDurationInOpenState The duration the circuit breaker stays in the open state before transitioning to half-open.
   * @param permittedCallsInHalfOpenState The number of calls permitted in the half-open state for testing recovery.
   * @param timeoutDuration The timeout duration for calls before they are considered failed.
   * @return A {@link Customizer} for the {@link ReactiveResilience4JCircuitBreakerFactory}.
   */
  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(
      @Value("${resilience4j.circuitbreaker.default.slidingWindowSize:10}") int slidingWindowSize,
      @Value("${resilience4j.circuitbreaker.default.failureRateThreshold:50}") float failureRateThreshold,
      @Value("${resilience4j.circuitbreaker.default.waitDurationInOpenState:10s}") Duration waitDurationInOpenState,
      @Value("${resilience4j.circuitbreaker.default.permittedCallsInHalfOpenState:5}") int permittedCallsInHalfOpenState,
      @Value("${resilience4j.timelimiter.default.timeoutDuration:2s}") Duration timeoutDuration) {

    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(CircuitBreakerConfig.custom()
            .slidingWindowSize(slidingWindowSize)
            .failureRateThreshold(failureRateThreshold)
            .waitDurationInOpenState(waitDurationInOpenState)
            .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpenState)
            .build())
        .timeLimiterConfig(TimeLimiterConfig.custom()
            .timeoutDuration(timeoutDuration)
            .build())
        .build());
  }
}
