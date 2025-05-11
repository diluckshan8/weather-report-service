package com.spring.weather.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application.
 * Handles specific exceptions and provides appropriate HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles exceptions of type {@link WeatherServiceException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(WeatherServiceException.class)
  public ResponseEntity<Object> handleWeatherServiceException(WeatherServiceException ex) {
    log.error("Weather service error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handles exceptions of type {@link WeatherProviderException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(WeatherProviderException.class)
  public ResponseEntity<Object> handleWeatherProviderException(WeatherProviderException ex) {
    log.error("Weather provider error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_GATEWAY);
  }

  /**
   * Handles exceptions of type {@link ExternalRequestException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(ExternalRequestException.class)
  public ResponseEntity<Object> handleCityNotFoundException(ExternalRequestException ex) {
    log.error("External Service Down error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /**
   * Handles exceptions of type {@link MissingQueryException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(MissingQueryException.class)
  public ResponseEntity<Object> handleMissingQueryException(MissingQueryException ex) {
    log.error("Missing Query Exception: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions of type {@link UnlimitedUsageException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(UnlimitedUsageException.class)
  public ResponseEntity<Object> handleUnlimitedUsageException(UnlimitedUsageException ex) {
    log.error("Unlimited Usage Exception error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles exceptions of type {@link WeatherStackApiException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(WeatherStackApiException.class)
  public ResponseEntity<Object> handleWeatherStackApiException(WeatherStackApiException ex) {
    log.error("Weather Stack Api Exception error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles exceptions of type {@link InvalidAccessKeyException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(InvalidAccessKeyException.class)
  public ResponseEntity<Object> handleInvalidAccessKeyException(InvalidAccessKeyException ex) {
    log.error("Invalid AccessKey Exception error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles exceptions of type {@link NoResourceFoundException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.error("No resource found: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /**
   * Handles exceptions of type {@link ConstraintViolationException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("Validation failed: {}", ex.getMessage());
    return buildErrorResponse("Validation error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions of type {@link MissingServletRequestParameterException}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
    log.error("Missing request parameter: {}", ex.getMessage());
    return buildErrorResponse("Missing required parameter: " + ex.getParameterName(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles generic exceptions of type {@link Exception}.
   *
   * @param ex The exception to handle.
   * @return A {@link ResponseEntity} containing the error details and HTTP status.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Builds a structured error response.
   *
   * @param message The error message to include in the response.
   * @param status The HTTP status to set in the response.
   * @return A {@link ResponseEntity} containing the error details.
   */
  private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }

}
