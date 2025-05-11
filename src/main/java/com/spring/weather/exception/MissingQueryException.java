package com.spring.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a required query parameter is missing in the request.
 * This exception is mapped to the HTTP 400 Bad Request status code.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingQueryException extends RuntimeException {
  public MissingQueryException(String message) {
    super(message);
  }
}
