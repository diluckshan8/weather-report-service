package com.spring.weather.exception;

  /**
   * Exception thrown when there is an issue with the weather provider.
   * This is a custom runtime exception used to handle errors specific to weather provider services.
   */
  public class WeatherProviderException extends RuntimeException {

    public WeatherProviderException(String message) {
      super(message);
    }

    public WeatherProviderException(String message, Throwable cause) {
      super(message, cause);
    }
  }