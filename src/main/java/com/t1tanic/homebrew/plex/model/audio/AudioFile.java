package com.t1tanic.homebrew.plex.model.audio;

import com.t1tanic.homebrew.plex.model.MediaFile;
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
