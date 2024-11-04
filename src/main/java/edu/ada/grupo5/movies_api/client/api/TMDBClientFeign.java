package edu.ada.grupo5.movies_api.client.api;

import edu.ada.grupo5.movies_api.dto.MovieResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tmdbClient", url = "${tmdb.api.url}", configuration = TMDBClientFeignConfig.class)
public interface TMDBClientFeign {

    @GetMapping("/trending/movie/{timeWindow}")
    MovieResponseDTO getTrendingMovies(@PathVariable("timeWindow") String timeWindow,
                                       @RequestParam("language") String language);

}
