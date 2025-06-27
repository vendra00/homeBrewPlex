package com.t1tanic.homebrew.plex.dto.movie;

import com.t1tanic.homebrew.plex.model.enums.AudioCodec;
import com.t1tanic.homebrew.plex.model.enums.VideoFormat;
import com.t1tanic.homebrew.plex.model.enums.VideoResolution;

public record MovieDTO (
        Long id,
        String title,
        int releaseYear,
        VideoFormat videoFormat,
        VideoResolution videoResolution,
        AudioCodec audioCodec)
{}
