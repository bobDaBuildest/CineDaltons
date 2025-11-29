package com.cinedaltons.repository;
public interface MovieRepository {
    Optional<Movie> findByTitle(String title);
}