package com.t1tanic.homebrew.plex.dto.movie;

import com.t1tanic.homebrew.plex.dto.TitleDTO;

public record MovieTitleDTO(Long id, String title) implements TitleDTO {}
