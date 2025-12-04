package com.cinedaltons.controller;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.service.TmdbService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class TmdbViewController {

    private final TmdbService tmdbService;

    public TmdbViewController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<TmdbMovieDto> popularMovies = tmdbService.getPopularMovies();
        model.addAttribute("movies", popularMovies);
        return "index";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        // Αυτό θα συνεχίσει να είναι κόκκινο μέχρι να κάνουμε το Βήμα 2
        List<TmdbMovieDto> searchResults = tmdbService.searchMovies(query);

        if (searchResults != null && !searchResults.isEmpty()) {
            model.addAttribute("movies", searchResults);
        } else {
            model.addAttribute("error", "Δεν βρέθηκαν ταινίες.");
        }
        return "index";
    }
}