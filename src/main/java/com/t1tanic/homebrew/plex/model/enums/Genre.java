package com.t1tanic.homebrew.plex.model.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATION("Animation"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    HORROR("Horror"),
    MUSIC("Music"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    THRILLER("Thriller"),
    WAR("War"),
    WESTERN("Western");

    private final String tmdbName;

    Genre(String tmdbName) {
        this.tmdbName = tmdbName;
    }

    private static final Map<String, Genre> NAME_MAP = new HashMap<>();

    static {
        for (Genre genre : values()) {
            NAME_MAP.put(genre.tmdbName.toLowerCase(), genre);
        }
    }

    public static Genre fromTmdbName(String name) {
        if (name == null) return null;
        return NAME_MAP.get(name.toLowerCase());
    }
}
