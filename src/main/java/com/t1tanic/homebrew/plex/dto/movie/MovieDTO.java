package com.t1tanic.homebrew.plex.dto.movie;

import com.t1tanic.homebrew.plex.model.enums.*;

public record MovieDTO (
        Long id,
        String title,
        int releaseYear,
        VideoFormat videoFormat,
        VideoResolution videoResolution,
        AudioCodec audioCodec,
        String director,
        Integer runtime,
        Genre genre,
        Language language,
        Country country,
        String plot,
        String posterUrl,
        String backdropUrl,
        String imdbId,
        String tmdbId)
{}
