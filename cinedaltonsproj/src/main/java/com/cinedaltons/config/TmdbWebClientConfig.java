package com.cinedaltons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class to correctly initialize the WebClient
 * using the properties defined in application.properties.
 * This ensures @Value fields are injected before the WebClient is built.
 */

@Configuration
public class TmdbWebClientConfig {

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    /**
     * Creates a WebClient bean configured with the base URL for the TMDB API.
     * This bean can be injected into the TmdbService.
     * @param builder The standard WebClient.Builder provided by Spring.
     * @return A configured WebClient instance.
     */
    @Bean
    public WebClient tmdbWebClient(WebClient.Builder builder) {
        // The baseUrl is correctly injected here, as this is a @Configuration class.
        return builder.baseUrl(baseUrl).build();
    }
}