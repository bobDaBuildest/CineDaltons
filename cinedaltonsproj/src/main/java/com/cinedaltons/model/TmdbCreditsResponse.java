package com.cinedaltons.model;

import com.cinedaltons.dto.TmdbCastDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor

public class TmdbCreditsResponse {
    @JsonProperty("cast")
    private List<TmdbCastDto> cast;
}
