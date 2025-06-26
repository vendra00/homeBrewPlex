package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.model.LibraryType;
import com.t1tanic.homebrew.plex.model.MediaFile;

import java.util.List;

public interface MediaService<T extends MediaFile> {
    void scanDirectory(String folderPath, LibraryType libraryType);
    List<T> findAll();
    boolean existsByPathAndLibraryType(String path, LibraryType libraryType);
}
