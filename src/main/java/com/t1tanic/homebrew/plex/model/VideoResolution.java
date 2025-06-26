package com.t1tanic.homebrew.plex.model;

import lombok.Getter;

@Getter
public enum VideoResolution {
    UHD_4K("2160p"),
    FULL_HD("1080p"),
    HD("720p"),
    SD("480p");

    private final String keyword;

    VideoResolution(String keyword) {
        this.keyword = keyword;
    }

    public static VideoResolution fromFileNameOrPath(String nameOrPath) {
        String lower = nameOrPath.toLowerCase();
        if (lower.contains("2160p")) return UHD_4K;
        if (lower.contains("1080p")) return FULL_HD;
        if (lower.contains("720p")) return HD;
        if (lower.contains("480p")) return SD;
        return null;
    }
}
