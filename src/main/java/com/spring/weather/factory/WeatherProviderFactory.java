package com.spring.weather.factory;

import com.spring.weather.provider.WeatherProvider;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Factory class for managing and providing a sorted list of weather providers.
 * This class ensures that the list of weather providers is sorted by their priority.
 */
@Getter
@Component
public class WeatherProviderFactory {

  /**
   * A list of weather providers sorted by their priority.
   */
  private final List<WeatherProvider> providers;

  /**
   * Constructs a new WeatherProviderFactory with the given list of weather providers.
   * The providers are sorted by their priority in ascending order.
   *
   * @param providers The list of weather providers to be managed by the factory.
   */
  public WeatherProviderFactory(List<WeatherProvider> providers) {
    this.providers = providers.stream()
        .sorted(Comparator.comparing(WeatherProvider::getPriority))
        .toList();
  }

}