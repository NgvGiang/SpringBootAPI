package com.usth.apiserver.controller;

import com.usth.apiserver.entity.Film;
import com.usth.apiserver.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public-api")
public class FilmController {
    private final FilmService filmService;


    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/find-film-by-title-like")
    public List<String> findFilmsByStoreAndTitleLike(@RequestParam Long storeId,@RequestParam String keyword) {

        return filmService.findFilmsByStoreAndTitleLike(storeId, keyword);
    }
}
