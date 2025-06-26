package com.t1tanic.homebrew.plex.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioFile extends MediaFile {
    private String artist;
    private String album;
}
