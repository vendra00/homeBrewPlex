package com.t1tanic.homebrew.plex.controller;

import com.t1tanic.homebrew.plex.dto.MovieDto;
import com.t1tanic.homebrew.plex.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/media/movie")
@RequiredArgsConstructor
public class MovieController {
    private final VideoService videoService;

    @GetMapping
    public List<MovieDto> getAllMovies() {
        return videoService.getAllMovies();
    }
}
