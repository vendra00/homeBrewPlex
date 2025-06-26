package com.t1tanic.homebrew.plex.model;

import lombok.Getter;

@Getter
public enum AudioCodec {
    AAC("AAC"),
    AC3("AC3"),
    EAC3("EAC3"),
    DTS("DTS"),
    DTS_HD("DTS-HD"),
    TRUE_HD("True-HD"),
    FLAC("FLAC"),
    OPUS("Opus"),
    MP3("MP3");

    private final String label;

    AudioCodec(String label) {
        this.label = label;
    }

    public static AudioCodec fromString(String input) {
        if (input == null) return null;
        String lower = input.toLowerCase();

        if (lower.contains("truehd")) return TRUE_HD;
        if (lower.contains("dts-hd")) return DTS_HD;
        if (lower.contains("dts")) return DTS;
        if (lower.contains("eac3")) return EAC3;
        if (lower.contains("ac3")) return AC3;
        if (lower.contains("aac")) return AAC;
        if (lower.contains("flac")) return FLAC;
        if (lower.contains("opus")) return OPUS;
        if (lower.contains("mp3")) return MP3;

        return null;
    }
}
