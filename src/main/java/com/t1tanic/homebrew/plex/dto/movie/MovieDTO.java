package com.t1tanic.homebrew.plex.dto.movie;

import com.t1tanic.homebrew.plex.model.enums.*;

import java.util.Set;

public record MovieDTO (
        Long id,
        String title,
        int releaseYear,
        VideoFormat videoFormat,
        VideoResolution videoResolution,
        AudioCodec audioCodec,
        String director,
        Integer runtime,
        Set<Genre> genres,
        Language language,
        Country country,
        String plot,
        String posterUrl,
        String backdropUrl,
        String imdbId,
        String tmdbId)
{}
