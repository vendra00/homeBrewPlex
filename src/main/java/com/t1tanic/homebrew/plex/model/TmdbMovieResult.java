package com.t1tanic.homebrew.plex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieResult {
    private String title;
    private String overview;
    private String releaseDate; // e.g. "1984-09-19"
    private double voteAverage;
    private String posterPath;

    public int getReleaseYear() {
        if (releaseDate != null && releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return Integer.parseInt(releaseDate.substring(0, 4));
        }
        return 0;
    }
}
