package com.t1tanic.homebrew.plex.model.video;

import com.t1tanic.homebrew.plex.model.MediaFile;
import com.t1tanic.homebrew.plex.model.enums.AudioCodec;
import com.t1tanic.homebrew.plex.model.enums.VideoFormat;
import com.t1tanic.homebrew.plex.model.enums.VideoResolution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VideoFile extends MediaFile {

    @Column
    private Integer releaseYear;
    @Enumerated(EnumType.STRING)
    private VideoFormat format;
    @Enumerated(EnumType.STRING)
    private VideoResolution resolution;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AudioCodec audioCodec;
    @Column(name = "tmdb_match_failed")
    private Boolean tmdbMatchFailed = false;

}
