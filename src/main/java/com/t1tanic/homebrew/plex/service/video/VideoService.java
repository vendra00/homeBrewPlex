package com.t1tanic.homebrew.plex.service.video;

import com.t1tanic.homebrew.plex.dto.UnmatchedVideoDTO;
import com.t1tanic.homebrew.plex.model.video.VideoFile;
import com.t1tanic.homebrew.plex.service.MediaService;

import java.util.List;

public interface VideoService extends MediaService<VideoFile> {
    void enrichMissingMetadata();
    List<UnmatchedVideoDTO> getAllTmdbUnmatchedVideoDTOs();

}
