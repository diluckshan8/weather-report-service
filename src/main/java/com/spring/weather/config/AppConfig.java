package com.spring.weather.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for application wide beans and settings.
 * This Configuration class defines beans for caching and web client functionality.
 */
@Configuration
public class AppConfig {

  /**
   * The cache expiry time in seconds, configurable via the property
   * `weather.cache.expiry.seconds`. Defaults to 3 seconds if not specified.
   */
  @Value("${weather.cache.expiry.seconds:3}")
  private int cacheExpirySeconds;

  /**
   * Creates a {@link WebClient} bean for making reactive HTTP requests.
   *
   * @return a configured {@link WebClient} instance
   */
  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  /**
   * Creates a {@link CacheManager} bean using Caffeine for caching.
   * The cache manager is configured with a cache named "weatherCache".
   *
   * @return a configured {@link CacheManager} instance
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("weatherCache");
    cacheManager.setCaffeine(caffeineCacheBuilder());
    return cacheManager;
  }

  /**
   * Configures the Caffeine cache builder with an expiry time and statistics recording.
   *
   * @return a {@link Caffeine} builder instance
   */
  private Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(cacheExpirySeconds))
        .recordStats();
  }
}