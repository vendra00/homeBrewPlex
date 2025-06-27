package com.t1tanic.homebrew.plex.service;

import com.t1tanic.homebrew.plex.model.TmdbMovieResult;
import com.t1tanic.homebrew.plex.model.TmdbMovieSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TmdbClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.themoviedb.org/3")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Value("${tmdb.api.key}")
    private String apiKey;

    public Mono<TmdbMovieResult> searchMovieByTitleAndYear(String title, int year) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", title)
                        .queryParam("year", year)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TmdbMovieSearchResponse.class)
                .mapNotNull(resp -> resp.getResults().stream().findFirst().orElse(null));
    }
}
