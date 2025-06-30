package com.t1tanic.homebrew.plex.util;

import com.t1tanic.homebrew.plex.model.enums.*;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieDetails;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieResult;
import com.t1tanic.homebrew.plex.model.video.MovieFile;
import com.t1tanic.homebrew.plex.service.TmdbClient;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class EnrichMovieMetadataUtil {

    public void applyTmdbMetadata(MovieFile movieFile, TmdbMovieDetails details, String baseUrl) {
        logMetadataInfo(details);

        setIfDifferent(movieFile::setTitle, movieFile.getTitle(), details.getTitle());
        setIfDifferent(movieFile::setPlot, movieFile.getPlot(), details.getOverview());

        Optional.ofNullable(details.getRuntime())
                .filter(runtime -> runtime > 0)
                .ifPresent(movieFile::setRuntime);

        extractDirector(details).ifPresent(movieFile::setDirector);
        extractGenres(details).ifPresent(movieFile::setGenres);

        applyOptional(details.getSpokenLanguages(), EnrichMovieMetadataUtil::extractLanguage)
                .ifPresentOrElse(movieFile::setLanguage, () -> warnMissingLanguage(details));

        applyOptional(details.getProductionCountries(), EnrichMovieMetadataUtil::extractCountry)
                .ifPresentOrElse(movieFile::setCountry, () -> warnMissingCountry(details));

        Optional.ofNullable(details.getPosterPath()).ifPresent(path -> movieFile.setPosterUrl(baseUrl + path));
        Optional.ofNullable(details.getBackdropPath()).ifPresent(path -> movieFile.setBackdropUrl(baseUrl + path));
        Optional.ofNullable(details.getImdbId()).ifPresent(movieFile::setImdbId);

        movieFile.setTmdbId(String.valueOf(details.getId()));
        movieFile.setTmdbMatchFailed(false);
    }

    private void setIfDifferent(Consumer<String> setter, String currentValue, String newValue) {
        if (newValue != null && (currentValue == null || !currentValue.equalsIgnoreCase(newValue))) {
            setter.accept(newValue);
        }
    }

    private void logMetadataInfo(TmdbMovieDetails details) {
        String genres = Optional.ofNullable(details.getGenres()).orElse(List.of()).stream()
                .map(TmdbMovieDetails.Genre::getName)
                .collect(Collectors.joining(", "));

        log.info("📝 TMDb Movie found: [title='{}', year={}, id={}, imdbId={}, genres={}]",
                details.getTitle(),
                details.getReleaseDate(),
                details.getId(),
                details.getImdbId(),
                genres
        );
    }

    private Optional<String> extractDirector(TmdbMovieDetails details) {
        return Optional.ofNullable(details.getCredits())
                .flatMap(credits -> credits.getCrew().stream()
                        .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
                        .map(TmdbMovieDetails.Crew::getName)
                        .findFirst());
    }

    private Optional<Set<Genre>> extractGenres(TmdbMovieDetails details) {
        return Optional.ofNullable(details.getGenres()).stream()
                .flatMap(Collection::stream)
                .map(TmdbMovieDetails.Genre::getName)
                .map(Genre::fromTmdbName)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), genres ->
                        genres.isEmpty() ? Optional.empty() : Optional.of(genres)));
    }

    private Optional<Language> extractLanguage(List<TmdbMovieDetails.SpokenLanguage> languages) {
        return languages.stream()
                .map(TmdbMovieDetails.SpokenLanguage::getEnglishName)
                .map(Language::fromEnglishName)
                .filter(Objects::nonNull)
                .findFirst();
    }

    private Optional<Country> extractCountry(List<TmdbMovieDetails.ProductionCountry> countries) {
        return countries.stream()
                .map(TmdbMovieDetails.ProductionCountry::getName)
                .map(Country::fromFullName)
                .filter(Objects::nonNull)
                .findFirst();
    }

    private void warnMissingLanguage(TmdbMovieDetails details) {
        List<TmdbMovieDetails.SpokenLanguage> langs = details.getSpokenLanguages();
        if (!langs.isEmpty()) {
            log.warn("⚠️ Unrecognized language in '{}'", langs.getFirst().getEnglishName());
        } else {
            log.warn("⚠️ No spoken languages found for TMDb ID {}", details.getId());
        }
    }

    private void warnMissingCountry(TmdbMovieDetails details) {
        List<TmdbMovieDetails.ProductionCountry> countries = details.getProductionCountries();
        if (!countries.isEmpty()) {
            log.warn("⚠️ Unrecognized country in '{}'", countries.getFirst().getName());
        } else {
            log.warn("⚠️ No production countries found for TMDb ID {}", details.getId());
        }
    }

    public static String determineSearchTitle(MovieFile movieFile) {
        return !"Unknown Title".equalsIgnoreCase(movieFile.getTitle())
                ? movieFile.getTitle()
                : movieFile.getFileName();
    }

    public static TmdbMovieResult searchMovieOrMarkAsFailed(String cleanedTitle, MovieFile movieFile, TmdbClient tmdbClient, Consumer<MovieFile> markAsFailed) {
        TmdbMovieResult result = tmdbClient.searchMovieByTitle(cleanedTitle).block();

        if (result == null) {
            log.warn("❌ No match found on TMDb for '{}'", cleanedTitle);
            markAsFailed.accept(movieFile);
        }

        return result;
    }

    // Utility to apply extractors with null safety
    private <T, R> Optional<R> applyOptional(List<T> source, Function<List<T>, Optional<R>> extractor) {
        return Optional.ofNullable(source)
                .filter(list -> !list.isEmpty())
                .flatMap(extractor);
    }
}

