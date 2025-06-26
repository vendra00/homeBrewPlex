package com.t1tanic.homebrew.plex.controller;

import com.t1tanic.homebrew.plex.dto.VideoTitleDto;
import com.t1tanic.homebrew.plex.model.LibraryType;
import com.t1tanic.homebrew.plex.model.MediaFile;
import com.t1tanic.homebrew.plex.model.VideoFile;
import com.t1tanic.homebrew.plex.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final VideoService videoService;

    @GetMapping
    public List<VideoFile> getAll() {
        return videoService.findAll();
    }

    @PostMapping("/scan")
    public String scanFolder(@RequestParam String path, @RequestParam LibraryType type) {
        videoService.scanDirectory(path, type);
        return "Scan completed.";
    }

    @GetMapping("/videos/sorted")
    public List<VideoTitleDto> getAllVideoTitlesSorted() {
        return videoService.getAllSortedByTitle();
    }
}
