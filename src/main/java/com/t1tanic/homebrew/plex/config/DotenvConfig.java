package com.t1tanic.homebrew.plex.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class DotenvConfig {

    private static final List<String> REQUIRED_KEYS = List.of(
            "DB_URL", "DB_USERNAME", "DB_PASSWORD", "TMDB_API_KEY"
    );

    @PostConstruct
    public void init() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.isBlank()) {
                    log.warn("⚠️ Environment variable '{}' is present but has no value.", key);
                } else {
                    System.setProperty(key, value);
                    log.debug("✅ Loaded environment variable '{}'", key);
                }
            });

            for (String key : REQUIRED_KEYS) {
                if (System.getProperty(key) == null || System.getProperty(key).isBlank()) {
                    log.error("❌ Required environment variable '{}' is missing or empty", key);
                    throw new IllegalStateException("Missing required environment variable: " + key);
                }
            }

            log.info("✅ All required environment variables loaded successfully.");
        } catch (Exception e) {
            log.error("❌ Error loading .env variables: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load environment variables", e);
        }
    }
}
