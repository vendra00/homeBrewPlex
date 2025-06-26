package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.dto.MovieDto;
import com.t1tanic.homebrew.plex.dto.VideoTitleDto;
import com.t1tanic.homebrew.plex.model.VideoFile;

import java.util.List;

public interface VideoService extends MediaService<VideoFile> {
    List<VideoTitleDto> getAllSortedByTitle();
    List<MovieDto> getAllMovies();
}
