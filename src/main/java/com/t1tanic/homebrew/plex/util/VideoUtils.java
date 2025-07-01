package com.t1tanic.homebrew.plex.util;

import com.t1tanic.homebrew.plex.model.enums.*;
import com.t1tanic.homebrew.plex.model.video.MovieFile;
import com.t1tanic.homebrew.plex.repository.MovieFileRepository;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;

@Slf4j
@UtilityClass
public class VideoUtils {

    public void scanRecursively(File dir, LibraryType libraryType, MovieFileRepository repository) {
        File[] files = dir.listFiles();
        if (files == null) return;

        Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> VideoFormat.isVideoExtension(file.getName()))
                .filter(file -> {
                    String fullPath = file.getAbsolutePath();
                    boolean exists = repository.existsByPathAndLibraryType(fullPath, libraryType);
                    if (exists) {
                        log.info("Skipped (already exists): {}", fullPath);
                    }
                    return !exists;
                })
                .forEach(file -> {
                    String fullPath = file.getAbsolutePath();

                    MovieFile movie = MovieFile.builder()
                            .fileName(file.getName())
                            .path(fullPath)
                            .size(file.length())
                            .type(MediaType.VIDEO)
                            .libraryType(libraryType)
                            .resolution(VideoResolution.fromFileNameOrPath(fullPath))
                            .format(VideoFormat.fromFileName(file.getName()))
                            .title(MediaUtils.extractTitleFromFileName(file.getName()))
                            .releaseYear(MediaUtils.extractYearFromFile(file.getName(), fullPath))
                            .audioCodec(AudioCodec.fromString(file.getName() + " " + fullPath))
                            .build();

                    repository.save(movie);
                    log.info("Indexed: {}", movie.getPath());
                });

        Arrays.stream(files)
                .filter(File::isDirectory)
                .forEach(subDir -> scanRecursively(subDir, libraryType, repository));
    }
}
