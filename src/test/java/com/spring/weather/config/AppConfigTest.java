package com.spring.weather.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Unit tests for the AppConfig class.
 * Verifies the correct creation and configuration of beans defined in the application configuration.
 */
class AppConfigTest {

  /**
   * Tests that the WebClient bean is created successfully.
   * Ensures that the returned WebClient instance is not null.
   */
  @DisplayName("Creates WebClient bean successfully")
  @Test
  void createsWebClientBeanSuccessfully() {
    AppConfig appConfig = new AppConfig();
    WebClient webClient = appConfig.webClient();
    assertNotNull(webClient);
  }

  /**
   * Tests that the CacheManager bean is created with the correct configuration.
   * Verifies that the CacheManager is of type CaffeineCacheManager and has the expected cache name.
   */
  @DisplayName("Creates CacheManager bean with correct cache name")
  @Test
  void createsCacheManagerBeanWithCorrectCacheName() {
    AppConfig appConfig = new AppConfig();
    CacheManager cacheManager = appConfig.cacheManager();
    assertNotNull(cacheManager);
    assertEquals(CaffeineCacheManager.class, cacheManager.getClass());
    assertEquals("weatherCache", ((CaffeineCacheManager) cacheManager).getCacheNames().iterator().next());
  }

}
