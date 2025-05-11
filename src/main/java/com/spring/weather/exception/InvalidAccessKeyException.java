package com.spring.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an invalid access key is provided.
 * This exception is mapped to the HTTP 401 Unauthorized status code.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidAccessKeyException extends RuntimeException {
  public InvalidAccessKeyException(String message) {
    super(message);
  }
}
