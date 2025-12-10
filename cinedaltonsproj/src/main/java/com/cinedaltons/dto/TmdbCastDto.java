package com.cinedaltons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TmdbCastDto {
    private String name;
    private String character;

    @JsonProperty("profile_path")
    private String profilePath;
}