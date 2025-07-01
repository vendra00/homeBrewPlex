package com.t1tanic.homebrew.plex.mapper;

import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.model.video.MovieFile;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MovieMapper {

    public static MovieDTO toDTO(MovieFile video) {
        return new MovieDTO(
                video.getTitle(),
                video.getReleaseYear() != null ? video.getReleaseYear() : 0,
                video.getFormat(),
                video.getResolution(),
                video.getAudioCodec(),
                video.getDirector(),
                video.getRuntime(),
                video.getGenres(),
                video.getLanguage(),
                video.getCountry(),
                video.getPlot(),
                video.getPosterUrl(),
                video.getBackdropUrl()
        );
    }
}
