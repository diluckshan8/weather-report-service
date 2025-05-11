package com.spring.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when external request is failed.
 * This exception is mapped to the HTTP 404 Not Found status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExternalRequestException extends RuntimeException {
  public ExternalRequestException(String message) {
    super(message);
  }
}
