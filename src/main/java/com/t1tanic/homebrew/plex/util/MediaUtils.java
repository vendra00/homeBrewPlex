package com.t1tanic.homebrew.plex.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MediaUtils {

    public static int extractYearFromDate(String releaseDate) {
        if (releaseDate != null && releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return Integer.parseInt(releaseDate.substring(0, 4));
        }
        return 0;
    }

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


    public static Integer extractYearFromFile(String fileName, String fullPath) {
        // Try from file name first
        Matcher matcher = Pattern.compile("(19|20)\\d{2}").matcher(fileName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        // If not found, try from the path
        matcher = Pattern.compile("(19|20)\\d{2}").matcher(fullPath);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return null;
    }

    public static String cleanTitleForTmdbSearch(String fileName) {
        String cleaned = fileName.toLowerCase()
                .replaceAll("(?i)(\\[.*?\\])", "")                          // Remove tudo entre colchetes
                .replaceAll("(?i)animeRG|yts|rarbg|pseudo|multi[- ]?audio|dual[- ]?audio", "")
                .replaceAll("(?i)(1080p|720p|2160p|4k|x265|x264|10bit|aac[25]?.?[01]?|opus|hdr|web|bd|hevc|bluray)", "")
                .replaceAll("(?i)\\d{3,4}p", "")                            // Remove resolução extra
                .replaceAll("(?i)\\.(mp4|mkv|avi)$", "")                    // Remove extensão
                .replaceAll("[^a-zA-Z0-9\\s]", " ")                         // Remove caracteres especiais
                .replaceAll("\\s{2,}", " ")                                 // Reduz espaços múltiplos
                .trim();

        // Capitalizar cada palavra (opcional)
        return Arrays.stream(cleaned.split(" "))
                .map(word -> word.isEmpty() ? "" :
                        Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
