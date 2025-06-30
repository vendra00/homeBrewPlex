package com.t1tanic.homebrew.plex.model;

import com.t1tanic.homebrew.plex.model.enums.LibraryType;
import com.t1tanic.homebrew.plex.model.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    private String fileName;

    @Column(length = 2048, nullable = false)
    private String path;

    private long size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MediaType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LibraryType libraryType;
}
