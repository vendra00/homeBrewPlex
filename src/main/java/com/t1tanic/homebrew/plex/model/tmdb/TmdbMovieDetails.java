package com.t1tanic.homebrew.plex.model.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbMovieDetails {

    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    private String overview;

    private Integer runtime;

    private String tagline;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("imdb_id")
    private String imdbId;

    private Integer id; // TMDb ID

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("spoken_languages")
    private List<SpokenLanguage> spokenLanguages;

    private List<Genre> genres;

    @JsonProperty("production_countries")
    private List<ProductionCountry> productionCountries;

    @Data
    public static class Genre {
        private Integer id;
        private String name;
    }

    @Data
    public static class SpokenLanguage {
        @JsonProperty("english_name")
        private String englishName;

        private String name;
    }

    @Data
    public static class ProductionCountry {
        @JsonProperty("iso_3166_1")
        private String code;

        private String name;
    }

    @JsonProperty("credits")
    private Credits credits;

    @Data
    public static class Credits {
        private List<Crew> crew;
    }

    @Data
    public static class Crew {
        private String job;
        private String name;
    }
}
