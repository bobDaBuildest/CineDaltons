package com.cinedaltons.dto;

import java.util.List;

public record EraResult(String bestEra, int confidence, List<EraScore> top5) {
    public record EraScore(String era, int score) {}
}
