package com.t1tanic.homebrew.plex.model.video;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TVShowFile extends VideoFile {
    // Add TV-show specific fields (e.g., season, episode number)
    private Integer season;
    private Integer episode;
    private String episodeTitle;
}
