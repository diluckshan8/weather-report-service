package com.spring.weather.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the ApiKeyValidator class.
 * Verifies the behavior of the API key validation logic under various scenarios.
 */
@ExtendWith(SpringExtension.class)
class ApiKeyValidatorTest {

  private ApiKeyValidator apiKeyValidator;

  /**
   * Sets up the test environment by initializing the ApiKeyValidator instance.
   */
  @BeforeEach
  void setUp() {
    apiKeyValidator = new ApiKeyValidator();
  }

  /**
   * Tests that an exception is thrown when the WeatherStack API key is missing.
   * Uses ReflectionTestUtils to set the API key fields for testing.
   */
  @DisplayName("Throws exception when WeatherStack API key is missing")
  @org.junit.jupiter.api.Test
  void throwsExceptionWhenWeatherStackApiKeyIsMissing() {
    ReflectionTestUtils.setField(apiKeyValidator, "weatherStackKey", "");
    ReflectionTestUtils.setField(apiKeyValidator, "openWeatherMapKey", "validKey");
    assertThrows(IllegalStateException.class, () -> apiKeyValidator.validateKeys());
  }

  /**
   * Tests that an exception is thrown when the OpenWeatherMap API key is missing.
   * Uses ReflectionTestUtils to set the API key fields for testing.
   */
  @DisplayName("Throws exception when OpenWeatherMap API key is missing")
  @org.junit.jupiter.api.Test
  void throwsExceptionWhenOpenWeatherMapApiKeyIsMissing() {
    ReflectionTestUtils.setField(apiKeyValidator, "weatherStackKey", "validKey");
    ReflectionTestUtils.setField(apiKeyValidator, "openWeatherMapKey", "");
    assertThrows(IllegalStateException.class, () -> apiKeyValidator.validateKeys());
  }

  /**
   * Tests that the validation succeeds when both API keys are present.
   * Uses ReflectionTestUtils to set the API key fields for testing.
   */
  @DisplayName("Validates successfully when both API keys are present")
  @org.junit.jupiter.api.Test
  void validatesSuccessfullyWhenBothApiKeysArePresent() {
    ReflectionTestUtils.setField(apiKeyValidator, "weatherStackKey", "validKey");
    ReflectionTestUtils.setField(apiKeyValidator, "openWeatherMapKey", "validKey");
    apiKeyValidator.validateKeys();
  }
}