package com.spring.weather.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.spring.weather.provider.WeatherProvider;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the WeatherProviderFactory class.
 * Verifies the behavior of the factory when sorting and retrieving weather providers.
 */
@ExtendWith(MockitoExtension.class)
class WeatherProviderFactoryTest {

  @Mock
  private WeatherProvider primaryProvider;

  @Mock
  private WeatherProvider secondaryProvider;

  /**
   * Tests that the factory correctly sorts providers by their priority.
   * Ensures that providers with lower priority values are listed first.
   */
  @Test
  @DisplayName("Should sort providers by priority")
  void shouldSortProvidersByPriority() {
    // Given
    when(primaryProvider.getPriority()).thenReturn(1);
    when(secondaryProvider.getPriority()).thenReturn(2);

    List<WeatherProvider> unsortedProviders = Arrays.asList(secondaryProvider, primaryProvider);
    WeatherProviderFactory factory = new WeatherProviderFactory(unsortedProviders);

    // When
    List<WeatherProvider> providers = factory.getProviders();

    // Then
    assertThat(providers).hasSize(2);
    assertThat(providers.get(0)).isEqualTo(primaryProvider);
    assertThat(providers.get(1)).isEqualTo(secondaryProvider);
  }

  /**
   * Tests that the factory maintains the original order of providers
   * when they have the same priority.
   */
  @Test
  @DisplayName("Should maintain order when providers have the same priority")
  void shouldMaintainOrderWhenSamePriority() {
    // Given
    when(primaryProvider.getPriority()).thenReturn(1);
    when(secondaryProvider.getPriority()).thenReturn(1);

    List<WeatherProvider> originalProviders = Arrays.asList(primaryProvider, secondaryProvider);
    WeatherProviderFactory factory = new WeatherProviderFactory(originalProviders);

    // When
    List<WeatherProvider> providers = factory.getProviders();

    // Then
    assertThat(providers).hasSize(2);
    assertThat(providers).containsExactly(primaryProvider, secondaryProvider);
  }

  /**
   * Tests that the factory handles an empty list of providers gracefully.
   * Ensures that the returned list is empty.
   */
  @Test
  @DisplayName("Should handle empty providers list")
  void shouldHandleEmptyProvidersList() {
    // Given
    WeatherProviderFactory factory = new WeatherProviderFactory(List.of());

    // When
    List<WeatherProvider> providers = factory.getProviders();

    // Then
    assertThat(providers).isEmpty();
  }
}
