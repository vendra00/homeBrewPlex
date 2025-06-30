package com.t1tanic.homebrew.plex.model.video;

import com.t1tanic.homebrew.plex.model.enums.Country;
import com.t1tanic.homebrew.plex.model.enums.Genre;
import com.t1tanic.homebrew.plex.model.enums.Language;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovieFile extends VideoFile {
    // Add movie-specific attributes here if needed (e.g., director, runtime)
    private String director;
    private Integer runtime; // in minutes
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>(); // e.g., "Action, Comedy"
    @Enumerated(EnumType.STRING)
    private Language language; // e.g., "English, Spanish"
    @Enumerated(EnumType.STRING)
    private Country country; // e.g., "USA, UK"
    @Column(length = 10000)
    private String plot; // Brief description of the movie's plot
    private String posterUrl; // URL to the movie's poster image
    private String backdropUrl; // URL to the movie's backdrop image
    private String imdbId; // IMDb identifier for the movie
    private String tmdbId; // TMDB identifier for the movie
}
