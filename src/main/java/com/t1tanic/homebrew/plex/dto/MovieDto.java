package com.t1tanic.homebrew.plex.dto;

import com.t1tanic.homebrew.plex.model.AudioCodec;
import com.t1tanic.homebrew.plex.model.VideoFormat;
import com.t1tanic.homebrew.plex.model.VideoResolution;

public record MovieDto(
        Long id,
        String title,
        int releaseYear,
        VideoFormat videoFormat,
        VideoResolution videoResolution,
        AudioCodec audioCodec)
{}
