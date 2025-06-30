package com.t1tanic.homebrew.plex.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tmdb")
@Data
public class TmdbProperties {
    private String apiKey;
    private String baseUrl;
    private String baseImageUrl;
}
