package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.dto.VideoTitleDto;
import com.t1tanic.homebrew.plex.model.VideoFile;

import java.util.List;

public interface VideoService extends MediaService<VideoFile> {
    // Add video-specific methods later if needed
    List<VideoTitleDto> getAllSortedByTitle();
}
