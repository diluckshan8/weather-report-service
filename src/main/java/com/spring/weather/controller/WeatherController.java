package com.spring.weather.controller;


import com.spring.weather.dto.WeatherResponse;
import com.spring.weather.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller for handling weather-related requests.
 * Provides endpoints to retrieve weather data for a specified city.
 */
@RestController
@RequestMapping("/v1/weather")
@RequiredArgsConstructor
@Validated
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Retrieves weather data for a specified city.
     *
     * @param city The name of the city for which weather data is requested.
     *
     * @return A {@link Mono} emitting a {@link ResponseEntity} containing the {@link WeatherResponse}.
     */
    @GetMapping
    public Mono<ResponseEntity<WeatherResponse>> getWeather(
        @RequestParam(value = "city") @NotBlank String city) {
        log.info("Received request for weather data for city: {}", city);
        return weatherService.getWeatherData(city)
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.info("Successfully returned weather data for city: {}", city))
            .doOnError(error -> log.error("Error retrieving weather data for city: {}", city, error));
    }
}
