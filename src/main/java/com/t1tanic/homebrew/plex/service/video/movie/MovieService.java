package com.t1tanic.homebrew.plex.service.video.movie;

import com.t1tanic.homebrew.plex.dto.movie.MovieDTO;
import com.t1tanic.homebrew.plex.model.enums.Country;
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
     * Enriches missing metadata for movies using TMDB.
     * This method will fetch additional information from TMDB and update the movie records accordingly.
     */
    List<MovieDTO> getAllMoviesByDirector(String director);

    /**
     * Fetches all movies by a specific country.
     * @param country The country to filter movies by.
     * @return A list of MovieDTO objects filtered by the specified country.
     */
    List<MovieDTO> getAllMoviesByCountry(Country country);

    /**
     * Fetches all movies released in a specific year.
     * @param year The release year to filter movies by.
     * @return A list of MovieDTO objects filtered by the specified release year.
     */
    List<MovieDTO> getAllMoviesByReleaseYear(Integer year);
}
