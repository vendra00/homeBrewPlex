package com.t1tanic.homebrew.plex.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFile extends MediaFile {
    private int width;
    private int height;
}
