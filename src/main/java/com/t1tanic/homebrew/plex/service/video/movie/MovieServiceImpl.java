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
                        video.getAudioCodec(),
                        video.getDirector(),
                        video.getRuntime(),
                        video.getGenre(),
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

            TmdbMovieResult searchResult = tmdbClient.searchMovieByTitleAndYear(cleanedTitle, searchYear).block();

            if (searchResult != null && searchResult.getTmdbId() != null) {
                TmdbMovieDetails details = tmdbClient.getMovieDetails(searchResult.getTmdbId()).block();

                if (details != null) {
                    log.info("Enriched metadata for '{}': {}", movieFile.getFileName(), details.getTitle());

                    if (isDifferent(movieFile.getTitle(), details.getTitle())) {
                        movieFile.setTitle(details.getTitle());
                    }

                    int tmdbYear = MediaUtils.extractYearFromDate(details.getReleaseDate());
                    if (tmdbYear > 0 && !Integer.valueOf(tmdbYear).equals(movieFile.getReleaseYear())) {
                        movieFile.setReleaseYear(tmdbYear);
                    }

                    // Director from credits
                    String director = details.getCredits() != null ? details.getCredits().getCrew().stream()
                            .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
                            .map(TmdbMovieDetails.Crew::getName)
                            .findFirst()
                            .orElse(null) : null;

                    if (isDifferent(movieFile.getDirector(), director)) {
                        movieFile.setDirector(director);
                    }

                    if (details.getRuntime() != null && !details.getRuntime().equals(movieFile.getRuntime())) {
                        movieFile.setRuntime(details.getRuntime());
                    }

                    if (details.getGenres() != null && !details.getGenres().isEmpty()) {
                        String genreStr = details.getGenres().getFirst().getName();
                        try {
                            Genre parsedGenre = Genre.valueOf(genreStr.toUpperCase().replace(" ", "_"));
                            if (!parsedGenre.equals(movieFile.getGenre())) {
                                movieFile.setGenre(parsedGenre);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown genre '{}' for '{}'", genreStr, movieFile.getFileName());
                        }
                    }

                    if (details.getOriginalLanguage() != null) {
                        try {
                            Language parsedLanguage = Language.valueOf(details.getOriginalLanguage().toUpperCase());
                            if (!parsedLanguage.equals(movieFile.getLanguage())) {
                                movieFile.setLanguage(parsedLanguage);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown language '{}' for '{}'", details.getOriginalLanguage(), movieFile.getFileName());
                        }
                    }

                    if (details.getProductionCountries() != null && !details.getProductionCountries().isEmpty()) {
                        String countryStr = details.getProductionCountries().getFirst().getName();
                        try {
                            Country parsedCountry = Country.valueOf(countryStr.toUpperCase().replace(" ", "_"));
                            if (!parsedCountry.equals(movieFile.getCountry())) {
                                movieFile.setCountry(parsedCountry);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown country '{}' for '{}'", countryStr, movieFile.getFileName());
                        }
                    }

                    if (isDifferent(movieFile.getPlot(), details.getOverview())) {
                        movieFile.setPlot(details.getOverview());
                    }

                    if (isDifferent(movieFile.getPosterUrl(), details.getPosterPath())) {
                        movieFile.setPosterUrl(details.getPosterPath());
                    }

                    if (isDifferent(movieFile.getBackdropUrl(), details.getBackdropPath())) {
                        movieFile.setBackdropUrl(details.getBackdropPath());
                    }

                    if (isDifferent(movieFile.getImdbId(), details.getImdbId())) {
                        movieFile.setImdbId(details.getImdbId());
                    }

                    if (details.getId() != null && !String.valueOf(details.getId()).equals(movieFile.getTmdbId())) {
                        movieFile.setTmdbId(String.valueOf(details.getId()));
                    }

                    movieFile.setTmdbMatchFailed(false);
                    repository.save(movieFile);
                } else {
                    log.warn("No detailed metadata found for TMDb ID: {}", searchResult.getTmdbId());
                    movieFile.setTmdbMatchFailed(true);
                    repository.save(movieFile);
                }
            } else {
                log.warn("No match found on TMDb for '{}'", cleanedTitle);
                movieFile.setTmdbMatchFailed(true);
                repository.save(movieFile);
            }
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
