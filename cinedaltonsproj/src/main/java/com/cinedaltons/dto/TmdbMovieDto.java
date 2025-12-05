package com.cinedaltons.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TmdbMovieDto {

    private Long id;
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("vote_average")
    private double voteAverage;

    // Αυτό το πεδίο είναι απαραίτητο για το Thymeleaf να φτιάχνει τη σωστή διαδρομή
    public String getFullPosterPath() {
        if (posterPath == null) {
            return "https://via.placeholder.com/150x225?text=No+Image";
        }
        return "https://image.tmdb.org/t/p/w200" + posterPath;
    }

    // Αυτή η μέθοδος είναι απαραίτητη για να λειτουργήσει το κουμπί Watchlist του JavaScript
    @JsonIgnore
    public String getJsonRepresentation() {
        try {
            // Χρειάζεται το Jackson dependency (Spring WebFlux το φέρνει)
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "{}";
        }
    }
}