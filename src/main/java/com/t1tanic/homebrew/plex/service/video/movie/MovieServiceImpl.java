package com.t1tanic.homebrew.plex.service.video.movie;

import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.dto.TitleDTO;
import com.t1tanic.homebrew.plex.dto.UnmatchedVideoDTO;
import com.t1tanic.homebrew.plex.model.MediaFile;
import com.t1tanic.homebrew.plex.model.TmdbMovieResult;
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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
                        video.getAudioCodec()
                ))
                .toList();
    }

    @Override
    public void enrichMissingMetadata() {
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

            TmdbMovieResult tmdb = tmdbClient.searchMovieByTitleAndYear(cleanedTitle, searchYear).block();

            if (tmdb != null) {
                log.info("Enriched metadata for '{}': {}", movieFile.getFileName(), tmdb.getTitle());

                String tmdbTitle = tmdb.getTitle();
                if (movieFile.getTitle() == null ||
                        "Unknown Title".equalsIgnoreCase(movieFile.getTitle()) ||
                        !tmdbTitle.equalsIgnoreCase(movieFile.getTitle())) {

                    log.info("Updating title for '{}': '{}' → '{}'",
                            movieFile.getFileName(),
                            movieFile.getTitle(),
                            tmdbTitle);
                    movieFile.setTitle(tmdbTitle);
                }

                int tmdbYear = tmdb.getReleaseYear();
                if (tmdbYear > 0 && !Integer.valueOf(tmdbYear).equals(movieFile.getReleaseYear())) {
                    log.info("Updating release year for '{}': {} → {}", movieFile.getTitle(), movieFile.getReleaseYear(), tmdbYear);
                    movieFile.setReleaseYear(tmdbYear);
                }

                movieFile.setTmdbMatchFailed(false); // ✅ matched successfully
                repository.save(movieFile);

            } else {
                log.warn("No match found on TMDb for '{}'", cleanedTitle);
                movieFile.setTmdbMatchFailed(true); // ✅ mark as failed to match
                repository.save(movieFile);
            }
        }
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
