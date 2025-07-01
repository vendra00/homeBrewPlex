package com.t1tanic.homebrew.plex.service.video.movie;

import com.t1tanic.homebrew.plex.config.TmdbProperties;
import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.dto.TitleDTO;
import com.t1tanic.homebrew.plex.dto.UnmatchedVideoDTO;
import com.t1tanic.homebrew.plex.mapper.MovieMapper;
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
import com.t1tanic.homebrew.plex.util.VideoUtils;
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
    public List<MovieDTO> getAllMoviesByDirector(String director) {
        return repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .filter(v -> v.getDirector() != null && v.getDirector().equalsIgnoreCase(director))
                .map(MovieMapper::toDTO)
                .toList();
    }

    @Override
    public List<MovieDTO> getAllMoviesByCountry(Country country) {
        return repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .filter(v -> v.getCountry() != null && v.getCountry().equals(country))
                .map(MovieMapper::toDTO)
                .toList();
    }

    @Override
    public List<MovieDTO> getAllMoviesByReleaseYear(Integer year) {
        return repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .filter(v -> v.getReleaseYear() != null && v.getReleaseYear().equals(year))
                .map(MovieMapper::toDTO)
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

        VideoUtils.scanRecursively(root, libraryType, repository);
    }

    @Override
    public List<VideoFile> findAll() {
        return List.of();
    }

}
