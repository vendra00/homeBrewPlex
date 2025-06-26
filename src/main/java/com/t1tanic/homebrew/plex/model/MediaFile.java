package com.t1tanic.homebrew.plex.model;

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

    private String fileName;

    @Column(length = 1024, nullable = false, unique = true)
    private String path;

    private long size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MediaType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LibraryType libraryType;
}
