package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.model.TmdbCreditsResponse;
import com.cinedaltons.model.TmdbReviewResponse;
import com.cinedaltons.model.TmdbResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TmdbServiceTest {

    @Mock
    private WebClient webClient;

    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private TmdbService tmdbService;

    private final String TEST_API_KEY = "TEST_API_KEY_123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tmdbService, "apiKey", TEST_API_KEY);
    }

    // --- TEST 1: Popular Movies ---
    @Test
    void testGetPopularMovies_IncludesApiKeyInUri() {
        TmdbResponse mockResponse = new TmdbResponse();
        mockResponse.setResults(Collections.emptyList());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TmdbResponse.class)).thenReturn(Mono.just(mockResponse));

        tmdbService.getPopularMovies();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());

        String capturedUri = uriCaptor.getValue();
        System.out.println("Test 1 URI: " + capturedUri);

        assertTrue(capturedUri.contains("api_key=" + TEST_API_KEY));
    }

    // --- TEST 2: Movie Details (The Boss Level) ---
    @Test
    void testGetMovieDetails_CallsAllEndpointsWithApiKey() {
        Long movieId = 550L;

        TmdbMovieDto mockMovie = new TmdbMovieDto();
        mockMovie.setTitle("Fight Club");
        TmdbCreditsResponse mockCredits = new TmdbCreditsResponse();
        mockCredits.setCast(Collections.emptyList());
        TmdbReviewResponse mockReviews = new TmdbReviewResponse();
        mockReviews.setResults(Collections.emptyList());

        // Reset mocks to be clean for this test
        reset(webClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);

        // Chain Setup
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Return different objects based on class type
        when(responseSpec.bodyToMono(TmdbMovieDto.class)).thenReturn(Mono.just(mockMovie));
        when(responseSpec.bodyToMono(TmdbCreditsResponse.class)).thenReturn(Mono.just(mockCredits));
        when(responseSpec.bodyToMono(TmdbReviewResponse.class)).thenReturn(Mono.just(mockReviews));

        // Execution
        tmdbService.getMovieDetails(movieId);

        // Verification
        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        // Expecting 3 calls
        verify(requestHeadersUriSpec, times(3)).uri(uriCaptor.capture());

        List<String> capturedUris = uriCaptor.getAllValues();
        System.out.println("Test 2 URIs: " + capturedUris);

        for (String uri : capturedUris) {
            assertTrue(uri.contains("api_key=" + TEST_API_KEY), "Missing API Key in: " + uri);
        }

        // Check specifics
        boolean foundMovie = capturedUris.stream().anyMatch(u -> u.contains("/movie/" + movieId) && !u.contains("credits") && !u.contains("reviews"));
        boolean foundCredits = capturedUris.stream().anyMatch(u -> u.contains("/credits"));
        boolean foundReviews = capturedUris.stream().anyMatch(u -> u.contains("/reviews"));

        assertTrue(foundMovie, "Movie Details Call Missing");
        assertTrue(foundCredits, "Credits Call Missing");
        assertTrue(foundReviews, "Reviews Call Missing");
    }
}