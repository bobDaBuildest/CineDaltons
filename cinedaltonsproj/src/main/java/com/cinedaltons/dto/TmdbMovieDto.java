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

    // --- Βασικές Πληροφορίες ---
    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath; // Μεγάλη εικόνα φόντου

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("runtime")
    private Integer runtime; // Διάρκεια σε λεπτά

    // --- Λίστες Δεδομένων ---

    @JsonProperty("genres")
    private List<TmdbGenreDto> genres; // Τα είδη (Action, Comedy κλπ)

    // Αυτά τα γεμίζουμε με ξεχωριστές κλήσεις στο Service:
    private List<TmdbCastDto> cast;     // Οι ηθοποιοί
    private List<TmdbReviewDto> reviews; // Οι κριτικές


    // --- Helper Methods για το HTML ---

    public String getFullPosterPath() {
        if (posterPath == null) return "https://via.placeholder.com/300x450?text=No+Poster";
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    public String getFullBackdropPath() {
        if (backdropPath == null) return "https://via.placeholder.com/1280x720?text=No+Backdrop";
        return "https://image.tmdb.org/t/p/original" + backdropPath;
    }

    // Επιστρέφει μόνο τους top 5 ηθοποιούς για να μην γεμίζει η σελίδα
    public List<TmdbCastDto> getTopCast() {
        if (cast != null && cast.size() > 5) {
            return cast.subList(0, 5);
        }
        return cast;
    }

    @JsonIgnore
    public String getJsonRepresentation() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return "{}";
        }
    }
}