package com.t1tanic.homebrew.plex.repository;

import com.t1tanic.homebrew.plex.model.video.TVShowFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TVShowFileRepository extends JpaRepository<TVShowFile, Long> {}
