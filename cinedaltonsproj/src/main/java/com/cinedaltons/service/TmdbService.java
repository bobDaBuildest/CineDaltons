package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.model.TmdbCreditsResponse;
import com.cinedaltons.model.TmdbReviewResponse;
import com.cinedaltons.model.TmdbResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public TmdbService(WebClient webClient) {
        this.webClient = webClient;
    }

    // --- 1. Δημοφιλείς Ταινίες (Υπήρχε ήδη) ---
    public List<TmdbMovieDto> getPopularMovies() {
        String uri = String.format("/movie/popular?api_key=%s&language=el-GR", apiKey);
        return fetchMovieList(uri, "popular movies");
    }

    // --- 2. Αναζήτηση Ταινιών (Υπήρχε ήδη) ---
    public List<TmdbMovieDto> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String uri = String.format("/search/movie?api_key=%s&query=%s&language=el-GR", apiKey, query);
        return fetchMovieList(uri, "search results for: " + query);
    }

    // --- 3. ΝΕΑ ΜΕΘΟΔΟΣ: Λεπτομέρειες Ταινίας (Overview, Cast, Reviews) ---
    public TmdbMovieDto getMovieDetails(Long movieId) {
        try {
            // Βήμα Α: Ζητάμε τα βασικά στοιχεία (Περίληψη, Διάρκεια, Εικόνες)
            String movieUri = String.format("/movie/%d?api_key=%s&language=el-GR", movieId, apiKey);

            TmdbMovieDto movie = webClient.get()
                    .uri(movieUri)
                    .retrieve()
                    .bodyToMono(TmdbMovieDto.class)
                    .block(); // Περιμένουμε την απάντηση

            if (movie != null) {
                // Βήμα Β: Ζητάμε τους Ηθοποιούς (Credits)
                try {
                    String creditsUri = String.format("/movie/%d/credits?api_key=%s", movieId, apiKey);
                    TmdbCreditsResponse credits = webClient.get()
                            .uri(creditsUri)
                            .retrieve()
                            .bodyToMono(TmdbCreditsResponse.class)
                            .block();

                    if (credits != null) {
                        movie.setCast(credits.getCast());
                    }
                } catch (Exception e) {
                    System.err.println("Could not fetch cast for movie " + movieId);
                }

                // Βήμα Γ: Ζητάμε τις Κριτικές (Reviews) - Χωρίς γλώσσα el-GR συνήθως
                try {
                    String reviewsUri = String.format("/movie/%d/reviews?api_key=%s", movieId, apiKey);
                    TmdbReviewResponse reviews = webClient.get()
                            .uri(reviewsUri)
                            .retrieve()
                            .bodyToMono(TmdbReviewResponse.class)
                            .block();

                    if (reviews != null) {
                        movie.setReviews(reviews.getResults());
                    }
                } catch (Exception e) {
                    System.err.println("Could not fetch reviews for movie " + movieId);
                }
            }
            return movie;

        } catch (Exception e) {
            System.err.println("Error fetching movie details: " + e.getMessage());
            return null;
        }
    }

    // --- Βοηθητική μέθοδος για λίστες (Search & Popular) ---
    private List<TmdbMovieDto> fetchMovieList(String uri, String logMessage) {
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