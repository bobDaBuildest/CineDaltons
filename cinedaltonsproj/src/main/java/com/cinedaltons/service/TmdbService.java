package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.model.TmdbResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;

/**
 * Service for fetching movie data from The Movie Database (TMDB) API.
 */
@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public TmdbService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Μέθοδος για δημοφιλείς ταινίες (Υπήρχε ήδη)
    public List<TmdbMovieDto> getPopularMovies() {
        String uri = String.format("/movie/popular?api_key=%s&language=el-GR", apiKey);
        return fetchMovies(uri, "popular movies");
    }

    // --- ΝΕΑ ΜΕΘΟΔΟΣ: Αναζήτηση Ταινιών ---
    public List<TmdbMovieDto> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Κατασκευή του URL για αναζήτηση
        // Προσθέτουμε το query και τη γλώσσα
        String uri = String.format("/search/movie?api_key=%s&query=%s&language=el-GR", apiKey, query);

        return fetchMovies(uri, "search results for: " + query);
    }

    // Βοηθητική μέθοδος για να μην γράφουμε τον ίδιο κώδικα WebClient δύο φορές
    private List<TmdbMovieDto> fetchMovies(String uri, String logMessage) {
        try {
            TmdbResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("TMDB API Error: " + clientResponse.statusCode())))
                    .bodyToMono(TmdbResponse.class)
                    .block();

            return response != null ? response.getResults() : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error fetching " + logMessage + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}