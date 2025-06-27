package com.t1tanic.homebrew.plex.model.video;

import com.t1tanic.homebrew.plex.model.enums.Country;
import com.t1tanic.homebrew.plex.model.enums.Genre;
import com.t1tanic.homebrew.plex.model.enums.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private Genre genre; // e.g., "Action, Comedy"
    private Language language; // e.g., "English, Spanish"
    private Country country; // e.g., "USA, UK"
    @Column(length = 10000)
    private String plot; // Brief description of the movie's plot
    private String posterUrl; // URL to the movie's poster image
    private String backdropUrl; // URL to the movie's backdrop image
    private String imdbId; // IMDb identifier for the movie
    private String tmdbId; // TMDB identifier for the movie
}
