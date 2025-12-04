package com.cinedaltons.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TmdbGenreDto {

    // Το TMDB επιστρέφει το ID και το Name για κάθε είδος (genre)
    private Long id;
    private String name;
}