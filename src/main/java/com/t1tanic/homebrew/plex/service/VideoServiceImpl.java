package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.dto.VideoTitleDto;
import com.t1tanic.homebrew.plex.model.*;
import com.t1tanic.homebrew.plex.repository.VideoFileRepository;
import com.t1tanic.homebrew.plex.util.MediaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoFileRepository repository;

    @Override
    public void scanDirectory(String folderPath, LibraryType libraryType) {
        File root = new File(folderPath);
        if (!root.exists() || !root.isDirectory()) {
            log.warn("Invalid folder: {}", folderPath);
            return;
        }

        scanRecursively(root, libraryType);
    }

    private void scanRecursively(File dir, LibraryType libraryType) {
        File[] files = dir.listFiles();
        if (files == null) return;

        Arrays.stream(files).forEach(file -> {
            if (file.isDirectory()) {
                scanRecursively(file, libraryType);
            } else if (isVideoFile(file)) {
                String fullPath = file.getAbsolutePath();
                String format = getExtension(file);

                if (repository.existsByPathAndLibraryType(fullPath, libraryType)) {
                    log.info("Skipped (already exists): {}", fullPath);
                    return;
                }

                VideoFile media = VideoFile.builder()
                        .fileName(file.getName())
                        .path(fullPath)
                        .size(file.length())
                        .type(MediaType.VIDEO)
                        .libraryType(libraryType)
                        .resolution(VideoResolution.fromFileNameOrPath(fullPath))
                        .format(VideoFormat.fromFileName(file.getName()))
                        .title(MediaUtils.extractTitleFromFileName(file.getName()))
                        .releaseYear(MediaUtils.extractYearFromFile(file.getName(), fullPath))
                        .build();

                repository.save(media);
                log.info("Indexed: {}", media.getPath());
            }
        });
    }

    private boolean isVideoFile(File file) {
        return VideoFormat.isVideoExtension(file.getName());
    }

    @Override
    public List<VideoFile> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsByPathAndLibraryType(String path, LibraryType libraryType) {
        return repository.existsByPathAndLibraryType(path, libraryType);
    }

    private String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot != -1 && lastDot < name.length() - 1) {
            return name.substring(lastDot + 1).toLowerCase();
        }
        return "unknown";
    }

    @Override
    public List<VideoTitleDto> getAllSortedByTitle() {
        return repository.findAllByOrderByTitleAsc()
                .stream()
                .map(video -> new VideoTitleDto(video.getId(), video.getTitle()))
                .toList();
    }
}

