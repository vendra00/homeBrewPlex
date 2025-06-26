package com.t1tanic.homebrew.plex.repository;

import com.t1tanic.homebrew.plex.model.VideoFile;
import com.t1tanic.homebrew.plex.model.LibraryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoFileRepository extends JpaRepository<VideoFile, Long> {
    boolean existsByPathAndLibraryType(String path, LibraryType libraryType);
    List<VideoFile> findAllByOrderByTitleAsc();

}
