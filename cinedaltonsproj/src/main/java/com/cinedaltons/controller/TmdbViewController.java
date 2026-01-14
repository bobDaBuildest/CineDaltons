package com.cinedaltons.controller;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.service.MoodFilterService; // Πρόσθεσε αυτό
import com.cinedaltons.service.TmdbService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TmdbViewController {

    private final TmdbService tmdbService;
    private final MoodFilterService moodFilterService;

    // Constructor Injection του Service
    public TmdbViewController(TmdbService tmdbService, MoodFilterService moodFilterService) {

        this.tmdbService = tmdbService;
        this.moodFilterService = moodFilterService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<TmdbMovieDto> popularMovies = tmdbService.getPopularMovies();
        model.addAttribute("movies", popularMovies);
        return "index";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        List<TmdbMovieDto> searchResults = tmdbService.searchMovies(query);

        if (searchResults != null && !searchResults.isEmpty()) {
            model.addAttribute("movies", searchResults);
        } else {
            model.addAttribute("error", "Δεν βρέθηκαν ταινίες.");
        }
        return "index";
    }
    @GetMapping("/search/mood")
    public String searchByMood(@RequestParam("mood") String mood, Model model) {
        // Καλούμε το νέο service
        List<TmdbMovieDto> moodMovies = moodFilterService.findMoviesByMood(mood);

        if (moodMovies != null && !moodMovies.isEmpty()) {
            model.addAttribute("movies", moodMovies);
            model.addAttribute("searchTitle", "Movies making you feel: " + mood);
        } else {
            model.addAttribute("error", "Δεν βρέθηκαν ταινίες για το συναίσθημα: " + mood);
        }
        return "index";
    }

    @GetMapping("/movie/{id}")
    public String getMovieDetails(@PathVariable("id") Long id, Model model) {


        TmdbMovieDto movie = tmdbService.getMovieDetails(id);

        model.addAttribute("movie", movie);

        return "details";

    }
}
