package com.t1tanic.homebrew.plex.model.enums;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("en", "English"),
    GERMAN("de", "German"),
    FRENCH("fr", "French"),
    SPANISH("es", "Spanish"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    CHINESE("zh", "Chinese"),
    RUSSIAN("ru", "Russian"),
    PORTUGUESE("pt", "Portuguese");

    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
