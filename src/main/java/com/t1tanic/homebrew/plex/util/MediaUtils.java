package com.t1tanic.homebrew.plex.util;

public class MediaUtils {

    public static String extractTitleFromFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "Unknown Title";
        }

        // Remove extension
        String name = fileName.replaceAll("\\.[^.]+$", "");

        // Replace dots/underscores with spaces
        name = name.replaceAll("[._]", " ");

        // Remove known bracketed/parenthesis metadata (pseudo, multi-audio, resolutions, etc.)
        name = name.replaceAll("(?i)[\\[\\(](.*?)(2160p|1080p|720p|HDR|x265|x264|AV1|BluRay|WEBRip|Opus|DTS|AAC|FLAC|DV|HDR10\\+?|pseudo|multi-audio|multi-sub).*?[\\]\\)]", "");

        // Remove anything that is a metadata group name or junk
        name = name.replaceAll("(?i)-\\s*(YTS|RARBG|Tigole|r00t|CiNEPHiLES|Dust|Mx|NTb|FGT|TRiToN).*", "");

        // Normalize and clean
        name = name.replaceAll("\\s+", " ").trim();

        // Stop at first metadata tag if still present
        String[] parts = name.split(" ");
        StringBuilder titleBuilder = new StringBuilder();

        for (String part : parts) {
            if (part.matches("(?i)(19\\d{2}|20\\d{2}|1080p|720p|2160p|4K|BluRay|WEBRip|WEB|HDRip|DVDRip|HDR|UHD|Remux|AV1|HEVC|x265|x264|H264|AAC\\d\\.\\d|DDP\\d\\.\\d|FLAC|Opus|DTS)")) {
                break;
            }
            titleBuilder.append(part).append(" ");
        }

        String title = titleBuilder.toString().trim();

        // Fallback if we failed to extract a valid title
        if (title.isEmpty() || title.matches("(?i)^\\[.*?\\]$") || title.equalsIgnoreCase("pseudo")) {
            return "Unknown Title";
        }

        return title;
    }


}
