package com.t1tanic.homebrew.plex.model.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieResult {
    private String title;
    private String overview;
    private String releaseDate; // format: yyyy-MM-dd
    private double voteAverage;
    private String posterPath;

    // Enrichment metadata fields
    private String director;
    private Integer runtime;          // in minutes
    private String genre;             // comma-separated genres
    private String language;          // ISO or English name
    private String country;           // production country name
    private String backdropUrl;
    private String imdbId;
    private Long tmdbId;

    public int getReleaseYear() {
        if (releaseDate != null && releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return Integer.parseInt(releaseDate.substring(0, 4));
        }
        return 0;
    }
}
