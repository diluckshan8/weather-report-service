package com.spring.weather.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GlobalExceptionHandler class.
 * Verifies the behavior of exception handling methods for various exception types.
 */
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @Mock
  private WeatherServiceException weatherServiceException;

  @Mock
  private WeatherProviderException weatherProviderException;

  @Mock
  private Exception genericException;

  /**
   * Initializes mocks and sets up the GlobalExceptionHandler instance before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  /**
   * Tests the handling of WeatherServiceException.
   * Verifies that the response contains a SERVICE_UNAVAILABLE status and the correct error message.
   */
  @DisplayName("Handles WeatherServiceException and returns SERVICE_UNAVAILABLE status")
  @org.junit.jupiter.api.Test
  void handlesWeatherServiceExceptionAndReturnsServiceUnavailableStatus() {
    when(weatherServiceException.getMessage()).thenReturn("Service error");

    ResponseEntity<Object> response = globalExceptionHandler.handleWeatherServiceException(weatherServiceException);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("Service error", body.get("message"));
  }

  /**
   * Tests the handling of WeatherProviderException.
   * Verifies that the response contains a BAD_GATEWAY status and the correct error message.
   */
  @DisplayName("Handles WeatherProviderException and returns BAD_GATEWAY status")
  @org.junit.jupiter.api.Test
  void handlesWeatherProviderExceptionAndReturnsBadGatewayStatus() {
    when(weatherProviderException.getMessage()).thenReturn("Provider error");

    ResponseEntity<Object> response = globalExceptionHandler.handleWeatherProviderException(weatherProviderException);

    assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("Provider error", body.get("message"));
  }

  /**
   * Tests the handling of a generic Exception.
   * Verifies that the response contains an INTERNAL_SERVER_ERROR status and a default error message.
   */
  @DisplayName("Handles generic Exception and returns INTERNAL_SERVER_ERROR status")
  @org.junit.jupiter.api.Test
  void handlesGenericExceptionAndReturnsInternalServerErrorStatus() {
    when(genericException.getMessage()).thenReturn("Unexpected error");

    ResponseEntity<Object> response = globalExceptionHandler.handleGenericException(genericException);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("An unexpected error occurred", body.get("message"));
  }
}