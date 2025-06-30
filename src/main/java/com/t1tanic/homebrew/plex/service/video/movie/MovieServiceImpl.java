package com.t1tanic.homebrew.plex.service.video.movie;

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
import com.t1tanic.homebrew.plex.util.MediaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final TmdbClient tmdbClient;
    private final MovieFileRepository repository;

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
        final String baseImageUrl = "https://image.tmdb.org/t/p/original";

        List<MovieFile> filesToEnrich = repository.findAll().stream()
                .filter(v -> v.getLibraryType() == LibraryType.MOVIE)
                .toList();

        for (MovieFile movieFile : filesToEnrich) {
            String baseForSearch = !"Unknown Title".equalsIgnoreCase(movieFile.getTitle())
                    ? movieFile.getTitle()
                    : movieFile.getFileName();

            String cleanedTitle = MediaUtils.cleanTitleForTmdbSearch(baseForSearch);
            Integer extractedYear = MediaUtils.extractYearFromFile(movieFile.getFileName(), movieFile.getPath());
            int searchYear = extractedYear != null ? extractedYear :
                    movieFile.getReleaseYear() != null ? movieFile.getReleaseYear() : 0;

            TmdbMovieResult searchResult = tmdbClient.searchMovieByTitle(cleanedTitle).block();

            if (searchResult == null && searchYear > 0) {
                log.warn("🔄 Retrying TMDb search for '{}' without year", cleanedTitle);
                searchResult = tmdbClient.searchMovieByTitle(cleanedTitle).block();
            }

            if (searchResult == null || searchResult.getTmdbId() == null) {
                log.warn("❌ No match found on TMDb for '{}'", cleanedTitle);
                movieFile.setTmdbMatchFailed(true);
                repository.save(movieFile);
                continue;
            }

            TmdbMovieDetails details = tmdbClient.getMovieDetails(searchResult.getTmdbId()).block();

            if (details == null) {
                log.warn("⚠️ No detailed metadata found for TMDb ID: {}", searchResult.getTmdbId());
                movieFile.setTmdbMatchFailed(true);
                repository.save(movieFile);
                continue;
            }

            log.info("📝 TMDb Movie found: [title='{}', year={}, id={}, imdbId={}, genres={}]",
                    details.getTitle(),
                    details.getReleaseDate(),
                    details.getId(),
                    details.getImdbId(),
                    details.getGenres().stream().map(TmdbMovieDetails.Genre::getName).collect(Collectors.joining(", "))
            );

            log.info("🎬 Enriched metadata for '{}': {}", movieFile.getFileName(), details.getTitle());

            // Title
            if (isDifferent(movieFile.getTitle(), details.getTitle())) {
                movieFile.setTitle(details.getTitle());
            }

            // Plot / Overview
            if (isDifferent(movieFile.getPlot(), details.getOverview())) {
                movieFile.setPlot(details.getOverview());
            }

            // Runtime
            Optional.ofNullable(details.getRuntime())
                    .filter(runtime -> runtime > 0)
                    .ifPresent(movieFile::setRuntime);

            // Director
            if (details.getCredits() != null) {
                details.getCredits().getCrew().stream()
                        .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
                        .map(TmdbMovieDetails.Crew::getName)
                        .findFirst()
                        .ifPresent(movieFile::setDirector);
            }

            // Genre
            if (details.getGenres() != null && !details.getGenres().isEmpty()) {
                Set<Genre> genres = details.getGenres().stream()
                        .map(TmdbMovieDetails.Genre::getName)
                        .map(Genre::fromTmdbName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                movieFile.setGenres(genres);
            }

            // Language
            details.getSpokenLanguages().stream()
                    .map(TmdbMovieDetails.SpokenLanguage::getEnglishName)
                    .map(Language::fromEnglishName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresentOrElse(
                            movieFile::setLanguage,
                            () -> log.warn("⚠️ Unrecognized language in '{}'", details.getSpokenLanguages().getFirst().getEnglishName())
                    );

            // Country
            details.getProductionCountries().stream()
                    .map(TmdbMovieDetails.ProductionCountry::getName)
                    .map(Country::fromFullName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresentOrElse(
                            movieFile::setCountry,
                            () -> {
                                if (!details.getProductionCountries().isEmpty()) {
                                    log.warn("⚠️ Unrecognized country in '{}'", details.getProductionCountries().getFirst().getName());
                                } else {
                                    log.warn("⚠️ No production countries found for TMDb ID {}", details.getId());
                                }
                            }
                    );

            // Poster & Backdrop
            Optional.ofNullable(details.getPosterPath()).ifPresent(path -> movieFile.setPosterUrl(baseImageUrl + path));
            Optional.ofNullable(details.getBackdropPath()).ifPresent(path -> movieFile.setBackdropUrl(baseImageUrl + path));

            // IMDb ID
            Optional.ofNullable(details.getImdbId()).ifPresent(movieFile::setImdbId);

            // TMDb ID
            movieFile.setTmdbId(String.valueOf(details.getId()));

            movieFile.setTmdbMatchFailed(false);
            repository.save(movieFile);
        }
    }


    private boolean isDifferent(String current, String updated) {
        return updated != null && (current == null || !current.equalsIgnoreCase(updated));
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
