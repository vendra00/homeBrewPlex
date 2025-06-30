package com.t1tanic.homebrew.plex.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.util.List;

@Slf4j
public class DotenvApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final List<String> REQUIRED_KEYS = List.of(
            "DB_URL", "DB_USERNAME", "DB_PASSWORD", "TMDB_API_KEY"
    );

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        log.info("✅ Loaded .env variables early in app lifecycle");

        // List loaded keys
        log.debug("📦 Loaded environment keys:");
        dotenv.entries().forEach(entry -> log.debug("  - {}", entry.getKey()));

        // Check for required variables
        boolean allPresent = true;
        for (String key : REQUIRED_KEYS) {
            String value = System.getProperty(key);
            if (value == null || value.isBlank()) {
                log.error("❌ Missing or empty required environment variable: {}", key);
                allPresent = false;
            }
        }

        if (!allPresent) {
            throw new IllegalStateException("Environment not properly configured — required variables missing.");
        }
    }
}
