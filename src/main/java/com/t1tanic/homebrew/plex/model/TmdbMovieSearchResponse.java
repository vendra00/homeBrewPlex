package com.t1tanic.homebrew.plex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieSearchResponse {
    private List<TmdbMovieResult> results;
}
