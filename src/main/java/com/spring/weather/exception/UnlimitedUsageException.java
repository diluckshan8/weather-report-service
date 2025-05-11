package com.spring.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an unauthorized attempt is made to access unlimited usage features.
 * This exception is mapped to the HTTP 401 Unauthorized status code.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnlimitedUsageException extends RuntimeException {
  public UnlimitedUsageException(String message) {
    super(message);
  }
}
