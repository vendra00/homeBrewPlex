package com.t1tanic.homebrew.plex.model;

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
    // Example additional field
    @Column(length = 255)
    private String title;
    @Column
    private Integer releaseYear;
    @Enumerated(EnumType.STRING)// e.g., 1080p, 4K
    private VideoFormat format; // e.g., MP4, MKV
    @Enumerated(EnumType.STRING)
    private VideoResolution resolution;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AudioCodec audioCodec;

    // You can also add duration, codec, etc. later
}
