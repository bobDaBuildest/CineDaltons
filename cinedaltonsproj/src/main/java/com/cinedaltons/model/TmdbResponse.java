package com.cinedaltons.model;

import com.cinedaltons.dto.TmdbMovieDto; // <<< Χρησιμοποιεί DTOs από τον φάκελο 'dto'
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TmdbResponse {

    private int page;

    @JsonProperty("total_pages")
    private int totalPages;

    // Η λίστα με τα αντικείμενα βρίσκεται μέσα στο πεδίο "results"
    @JsonProperty("results")
    private List<TmdbMovieDto> results; // ΛΙΣΤΑ ΑΠΟ DTOs
}