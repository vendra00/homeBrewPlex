package com.t1tanic.homebrew.plex.controller;

import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.dto.movie.MovieTitleDTO;
import com.t1tanic.homebrew.plex.dto.UnmatchedVideoDTO;
import com.t1tanic.homebrew.plex.model.enums.LibraryType;
import com.t1tanic.homebrew.plex.service.video.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService service;

    @GetMapping
    public List<MovieDTO> getAllMovies() {
        return service.getAllMovies();
    }

    @GetMapping("/enrich")
    public ResponseEntity<String> enrichMissingMetadata() {
        service.enrichMissingMetadata();
        return ResponseEntity.ok("Metadata enrichment completed.");
    }

    @GetMapping("/unmatched")
    public ResponseEntity<List<UnmatchedVideoDTO>> getAllUnmatchedVideos() {
        return ResponseEntity.ok(service.getAllTmdbUnmatchedVideoDTOs());
    }

    @GetMapping("/sorted-by-title")
    public ResponseEntity<List<MovieTitleDTO>> getAllMoviesOrderByTitle() {
        List<MovieTitleDTO> sortedMovies = service.getAllSortedByTitle(movie -> new MovieTitleDTO(movie.getId(), movie.getTitle()));
        return ResponseEntity.ok(sortedMovies);
    }

    @PostMapping("/scan")
    public ResponseEntity<String> scanFolder(@RequestParam String path) {
        service.scanDirectory(path);
        return ResponseEntity.ok("Scan completed.");
    }


}
