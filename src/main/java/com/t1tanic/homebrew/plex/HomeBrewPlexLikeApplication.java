package com.t1tanic.homebrew.plex;

import com.t1tanic.homebrew.plex.config.DotenvApplicationContextInitializer;
import com.t1tanic.homebrew.plex.config.TmdbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(TmdbProperties.class)
public class HomeBrewPlexLikeApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HomeBrewPlexLikeApplication.class);
        app.addInitializers(new DotenvApplicationContextInitializer());
        app.run(args);
    }

}
