package com.t1tanic.homebrew.plex.service.video.movie;

import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.service.video.VideoService;

import java.util.List;

/**
 * Service interface for managing movies in the Home Brew Plex Like media server.
 * This interface extends VideoService to provide additional methods specific to movies.
 */
public interface MovieService extends VideoService {

    /**
     * Fetches all movies from the database.
     * @return A list of MovieDTO objects representing all movies.
     */
    List<MovieDTO> getAllMovies();

    /**
     * Fetches all movies sorted by title.
     * @return A list of MovieDTO objects filtered by director..
     */
    List<MovieDTO> getAllMoviesByDirector();

    /**
     * Fetches all movies sorted by country.
     * @return A list of MovieDTO objects filtered by country.
     */
    List<MovieDTO> getAllMoviesByCountry();

    /**
     * Fetches all movies sorted by release year.
     * @return A list of MovieDTO objects filtered by release year.
     */
    List<MovieDTO> getAllMoviesByReleaseYear();
}
