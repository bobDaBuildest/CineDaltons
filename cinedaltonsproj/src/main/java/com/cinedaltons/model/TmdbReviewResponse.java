package com.cinedaltons.model;

import com.cinedaltons.dto.TmdbReviewDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TmdbReviewResponse {
    @JsonProperty("results")
    private List<TmdbReviewDto> results;
}