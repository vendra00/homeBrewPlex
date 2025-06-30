package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieDetails;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieResult;
import com.t1tanic.homebrew.plex.model.tmdb.TmdbMovieSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.themoviedb.org/3")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Value("${tmdb.api.key}")
    private String apiKey;

    public Mono<TmdbMovieResult> searchMovieByTitle(String title) {
        log.debug("🔍 Searching TMDb for title='{}'", title);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", title)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TmdbMovieSearchResponse.class)
                .mapNotNull(resp -> {
                    if (resp.getResults() == null || resp.getResults().isEmpty()) {
                        log.warn("❌ No TMDb results for '{}'", title);
                        return null;
                    }

                    log.info("✅ TMDb returned {} result(s) for '{}'", resp.getResults().size(), title);
                    resp.getResults().forEach(r ->
                            log.info("➡️ TMDb Result: '{}' ({}), id={}, popularity={}, overview={}",
                                    r.getTitle(),
                                    r.getReleaseDate(),
                                    r.getTmdbId(),
                                    r.getCountry(),
                                    r.getOverview() != null ? r.getOverview().substring(0, Math.min(120, r.getOverview().length())) + "..." : "N/A")
                    );

                    String normalizedInput = title.trim().toLowerCase();

                    return resp.getResults().stream()
                            .filter(r -> r.getTitle() != null && r.getTitle().trim().equalsIgnoreCase(title))
                            .findFirst()
                            .orElseGet(() -> {
                                if (!resp.getResults().isEmpty()) {
                                    TmdbMovieResult fallback = resp.getResults().getFirst();
                                    log.warn("⚠️ No good match found for '{}', using fallback '{}'", title, fallback.getTitle());
                                    return fallback;
                                } else {
                                    log.warn("⚠️ No good match and no fallback result found for '{}'", title);
                                    return null;
                                }
                            });

                })
                .onErrorResume(ex -> {
                    log.error("TMDb search failed for '{}': {}", title, ex.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TmdbMovieDetails> getMovieDetails(Long tmdbId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("append_to_response", "credits")
                        .build(tmdbId))
                .retrieve()
                .bodyToMono(TmdbMovieDetails.class)
                .doOnNext(details -> log.info("🎬 Fetched TMDb details for '{}', id={}", details.getTitle(), tmdbId))
                .doOnError(ex -> log.error("TMDb details fetch failed for ID {}: {}", tmdbId, ex.getMessage()));
    }
}
