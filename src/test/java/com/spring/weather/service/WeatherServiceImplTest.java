package com.spring.weather.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.exception.ExternalRequestException;
import com.spring.weather.exception.WeatherProviderException;
import com.spring.weather.exception.WeatherServiceException;
import com.spring.weather.factory.WeatherProviderFactory;
import com.spring.weather.provider.WeatherProvider;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit tests for the WeatherServiceImpl class.
 * Verifies the behavior of the weather service under various scenarios, including failover and caching.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

  @Mock
  private WeatherProviderFactory weatherProviderFactory;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @Mock
  private Cache.ValueWrapper valueWrapper;

  @Mock
  private WeatherProvider primaryProvider;

  @Mock
  private WeatherProvider secondaryProvider;

  private WeatherServiceImpl weatherService;
  private final WeatherResponse weatherResponse = new WeatherResponse(20.0, 29.0);

  /**
   * Sets up the test environment by initializing mocks and creating the WeatherServiceImpl instance.
   */
  @BeforeEach
  void setUp() {
    lenient().when(cacheManager.getCache("weatherCache")).thenReturn(cache);
    lenient().when(weatherProviderFactory.getProviders())
        .thenReturn(Arrays.asList(primaryProvider, secondaryProvider));

    weatherService = new WeatherServiceImpl(weatherProviderFactory, cacheManager);
  }

  /**
   * Tests that the service retrieves data from the primary provider when it is available.
   */
  @Test
  @DisplayName("Should return data from primary provider")
  void shouldReturnDataFromPrimaryProvider() {
    // Given
    when(primaryProvider.getWeatherData(anyString()))
        .thenReturn(Mono.just(weatherResponse));

    // When & Then
    StepVerifier.create(weatherService.getWeatherData("Melbourne"))
        .expectNext(weatherResponse)
        .verifyComplete();

    verify(primaryProvider).getWeatherData("Melbourne");
    verifyNoInteractions(secondaryProvider);
  }

  /**
   * Tests that the service fails over to the secondary provider when the primary provider fails.
   */
  @Test
  @DisplayName("Should failover to secondary provider on WeatherServiceException")
  void shouldFailoverToSecondaryProviderOnWeatherServiceException() {
    // Given
    when(primaryProvider.getWeatherData(anyString()))
        .thenReturn(Mono.error(new ExternalRequestException("Primary failure")));
    when(secondaryProvider.getWeatherData(anyString()))
        .thenReturn(Mono.just(weatherResponse));

    // When & Then
    StepVerifier.create(weatherService.getWeatherData("Melbourne"))
        .expectNext(weatherResponse)
        .verifyComplete();

    verify(primaryProvider).getWeatherData("Melbourne");
    verify(secondaryProvider).getWeatherData("Melbourne");
  }

  /**
   * Tests that the service returns stale data from the cache when all providers fail with non-failover errors.
   */
  @Test
  @DisplayName("Should return stale data when all providers fail with non-failover error")
  void shouldReturnStaleDataWhenAllProvidersFailWithNonFailoverError() {
    // Given
    when(primaryProvider.getWeatherData(anyString()))
        .thenReturn(Mono.error(new WeatherProviderException("Primary provider error")));
    when(cache.get(anyString())).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(weatherResponse);

    // When & Then
    StepVerifier.create(weatherService.getWeatherData("Melbourne"))
        .expectNext(weatherResponse)
        .verifyComplete();

    verify(primaryProvider).getWeatherData("Melbourne");
    verifyNoInteractions(secondaryProvider);
    verify(cache).get("melbourne");
  }

  /**
   * Tests that the service fails when all providers fail and no stale data is available in the cache.
   */
  @Test
  @DisplayName("Should fail when all providers fail and no stale data")
  void shouldFailWhenAllProvidersFailAndNoStaleData() {
    //Given
    when(primaryProvider.getWeatherData(anyString()))
        .thenReturn(Mono.error(new WeatherProviderException("Primary error")));
    when(cache.get(anyString())).thenReturn(null);

    // When & Then
    StepVerifier.create(weatherService.getWeatherData("Melbourne"))
        .expectError(WeatherProviderException.class)
        .verify();

    verify(primaryProvider).getWeatherData("Melbourne");
    verifyNoInteractions(secondaryProvider);
    verify(cache).get("melbourne");
  }

  /**
   * Tests that the service handles the case where no providers are available.
   */
  @Test
  @DisplayName("Should handle case when no providers available")
  void shouldHandleCaseWhenNoProvidersAvailable() {
    // Given
    when(weatherProviderFactory.getProviders()).thenReturn(List.of());

    // When & Then
    StepVerifier.create(weatherService.getWeatherData("Melbourne"))
        .expectError(WeatherServiceException.class)
        .verify();
  }
}