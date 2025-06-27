package com.t1tanic.homebrew.plex.model.image;

import com.t1tanic.homebrew.plex.model.MediaFile;
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
