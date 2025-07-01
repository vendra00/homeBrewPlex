package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.dto.TitleDTO;
import com.t1tanic.homebrew.plex.model.MediaFile;

import java.util.List;
import java.util.function.Function;

public interface MediaService<T extends MediaFile> {
    void scanDirectory(String folderPath);
    List<T> findAll();
    <T extends TitleDTO> List<T> getAllSortedByTitle(Function<MediaFile, T> mapper);
}
