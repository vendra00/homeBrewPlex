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
    @Column(length = 10)
    private String resolution;
    @Enumerated(EnumType.STRING)// e.g., 1080p, 4K
    private VideoFormat format; // e.g., MP4, MKV

    // You can also add duration, codec, etc. later
}
