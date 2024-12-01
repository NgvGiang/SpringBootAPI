package com.usth.apiserver.service;

import com.usth.apiserver.entity.Film;
import com.usth.apiserver.entity.User;
import com.usth.apiserver.repository.FilmRepository;
import com.usth.apiserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    // Inject FilmRepository thông qua constructor
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public Film getFilmById(Long id) {
        return filmRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<String> findFilmsByStoreAndTitleLike(Long storeId, String keyword) {
        return filmRepository.findFilmsByStoreAndTitleLike(storeId, keyword);
    }

}
