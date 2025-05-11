package com.spring.weather.exception;

    /**
     * Exception thrown when there is an issue with the weather service.
     * This is a custom runtime exception used to handle errors specific to the weather service.
     */
    public class WeatherServiceException extends RuntimeException {

      public WeatherServiceException(String message) {
        super(message);
      }

      public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
      }
    }