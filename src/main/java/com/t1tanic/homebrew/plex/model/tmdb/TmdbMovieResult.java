package com.t1tanic.homebrew.plex.model.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t1tanic.homebrew.plex.util.MediaUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieResult {

    private String title;

    private String overview;

    @JsonProperty("release_date")
    private String releaseDate; // format: yyyy-MM-dd

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

    // Enrichment metadata fields (custom - not part of TMDb API responses)
    private String director;
    private Integer runtime;
    private String genre;
    private String language;
    private String country;
    private String backdropUrl;
    private String imdbId;

    @JsonProperty("id")
    private Long tmdbId;

    public int getReleaseYear() {
        return MediaUtils.extractYearFromDate(releaseDate);
    }
}
