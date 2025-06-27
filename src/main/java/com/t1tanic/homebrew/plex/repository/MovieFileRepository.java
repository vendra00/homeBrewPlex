package com.t1tanic.homebrew.plex.repository;

import com.t1tanic.homebrew.plex.model.enums.LibraryType;
import com.t1tanic.homebrew.plex.model.video.MovieFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieFileRepository extends JpaRepository<MovieFile, Long> {
    boolean existsByPathAndLibraryType(String path, LibraryType libraryType);
    List<MovieFile> findAllByOrderByTitleAsc();
}
