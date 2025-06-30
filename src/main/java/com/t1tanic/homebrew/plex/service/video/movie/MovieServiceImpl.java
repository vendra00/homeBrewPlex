package com.t1tanic.homebrew.plex.service.video.movie;

import com.t1tanic.homebrew.plex.config.TmdbProperties;
import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.dto.TitleDTO;
import com.t1tanic.homebrew.plex.dto.UnmatchedVideoDTO;
import com.t1tanic.homebrew.plex.model.MediaFile;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieDetails;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieResult;
import com.t1tanic.homebrew.plex.model.enums.*;
import com.t1tanic.homebrew.plex.model.video.MovieFile;
import com.t1tanic.homebrew.plex.model.video.VideoFile;
import com.t1tanic.homebrew.plex.repository.MovieFileRepository;
import com.t1tanic.homebrew.plex.service.TmdbClient;
import com.t1tanic.homebrew.plex.util.EnrichMovieMetadataUtil;
import com.t1tanic.homebrew.plex.util.MediaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final TmdbClient tmdbClient;
    private final MovieFileRepository repository;
    private final TmdbProperties tmdbProperties;

    @Override
    public <T extends TitleDTO> List<T> getAllSortedByTitle(Function<MediaFile, T> mapper) {
        return repository.findAllByOrderByTitleAsc()
                .stream()
                .map(mapper)
                .toList();
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        return repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .map(video -> new MovieDTO(
                        video.getId(),
                        video.getTitle(),
                        video.getReleaseYear() != null ? video.getReleaseYear() : 0,
                        video.getFormat(),
                        video.getResolution(),
                        video.getAudioCodec(),
                        video.getDirector(),
                        video.getRuntime(),
                        video.getGenres(),
                        video.getLanguage(),
                        video.getCountry(),
                        video.getPlot(),
                        video.getPosterUrl(),
                        video.getBackdropUrl(),
                        video.getImdbId(),
                        video.getTmdbId()
                ))
                .toList();
    }

    @Override
    public void enrichMissingMetadata() {
        String baseUrl = tmdbProperties.getBaseImageUrl();
        List<MovieFile> filesToEnrich = repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .toList();

        for (MovieFile movieFile : filesToEnrich) {
            String searchTitle = EnrichMovieMetadataUtil.determineSearchTitle(movieFile);
            String cleanedTitle = MediaUtils.cleanTitleForTmdbSearch(searchTitle);

            TmdbMovieResult searchResult = EnrichMovieMetadataUtil.searchMovieOrMarkAsFailed(cleanedTitle, movieFile, tmdbClient, this::markAsFailed);
            if (searchResult == null) continue;

            TmdbMovieDetails details = tmdbClient.getMovieDetails(searchResult.getTmdbId()).block();
            if (details == null) {
                log.warn("⚠️ No detailed metadata found for TMDb ID: {}", searchResult.getTmdbId());
                markAsFailed(movieFile);
                continue;
            }

            log.info("🎬 Enriched metadata for '{}': {}", movieFile.getFileName(), details.getTitle());
            EnrichMovieMetadataUtil.applyTmdbMetadata(movieFile, details, baseUrl);
            repository.save(movieFile);
        }
    }

    private void markAsFailed(MovieFile movieFile) {
        movieFile.setTmdbMatchFailed(true);
        repository.save(movieFile);
    }

    @Override
    public List<UnmatchedVideoDTO> getAllTmdbUnmatchedVideoDTOs() {
        return repository.findAll().stream()
                .filter(video -> Boolean.TRUE.equals(video.getTmdbMatchFailed()))
                .map(video -> new UnmatchedVideoDTO(
                        video.getId(),
                        video.getFileName(),
                        video.getPath(),
                        video.getTitle()
                ))
                .toList();
    }

    @Override
    public void scanDirectory(String folderPath) {
        LibraryType libraryType = LibraryType.MOVIE;
        File root = new File(folderPath);
        if (!root.exists() || !root.isDirectory()) {
            log.warn("Invalid folder: {}", folderPath);
            return;
        }

        scanRecursively(root, libraryType);
    }

    @Override
    public List<VideoFile> findAll() {
        return List.of();
    }

    private void scanRecursively(File dir, LibraryType libraryType) {
        File[] files = dir.listFiles();
        if (files == null) return;

        Arrays.stream(files).forEach(file -> {
            if (file.isDirectory()) {
                scanRecursively(file, libraryType);
            } else if (isVideoFile(file)) {
                String fullPath = file.getAbsolutePath();

                if (repository.existsByPathAndLibraryType(fullPath, libraryType)) {
                    log.info("Skipped (already exists): {}", fullPath);
                    return;
                }

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
            }
        });
    }

    private boolean isVideoFile(File file) {
        return VideoFormat.isVideoExtension(file.getName());
    }

    @Override
    public List<MovieDTO> getAllMoviesByDirector() {
        return List.of();
    }

    @Override
    public List<MovieDTO> getAllMoviesByCountry() {
        return List.of();
    }

    @Override
    public List<MovieDTO> getAllMoviesByReleaseYear() {
        return List.of();
    }
}
