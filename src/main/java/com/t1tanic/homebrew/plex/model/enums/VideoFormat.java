package com.t1tanic.homebrew.plex.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum VideoFormat {
    MP4(".mp4"),
    MKV(".mkv"),
    AVI(".avi"),
    MOV(".mov"),
    FLV(".flv"),
    WMV(".wmv");

    private final String extension;

    VideoFormat(String extension) {
        this.extension = extension;
    }

    public static boolean isVideoExtension(String fileName) {
        String lower = fileName.toLowerCase();
        return Arrays.stream(values()).anyMatch(type -> lower.endsWith(type.extension));
    }

    public static VideoFormat fromFileName(String fileName) {
        String lower = fileName.toLowerCase();
        return Arrays.stream(values())
                .filter(type -> lower.endsWith(type.extension))
                .findFirst()
                .orElse(null); // or throw an exception if preferred
    }
}
