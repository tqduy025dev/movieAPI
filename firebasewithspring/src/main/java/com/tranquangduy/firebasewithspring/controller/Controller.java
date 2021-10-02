/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tranquangduy.firebasewithspring.controller;

import com.tranquangduy.firebasewithspring.model.Film;
import com.tranquangduy.firebasewithspring.model.Genres;
import com.tranquangduy.firebasewithspring.service.FirebaseFilleService;
import com.tranquangduy.firebasewithspring.service.FilmService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author UserName
 */
@RestController
public class Controller {

    @Autowired
    private FirebaseFilleService firebaseFileService;
    @Autowired
    private FilmService filmService;

    @GetMapping("/")
    public ResponseEntity createPage() throws InterruptedException, ExecutionException {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World!");
    }

    @GetMapping("/api/get/all_movie")
    public ResponseEntity getAllFilm() throws InterruptedException, ExecutionException {

        List<Film> mFilm = filmService.getAllMovie();

        if (mFilm == null || mFilm.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @GetMapping("/api/get/all_genres")
    public ResponseEntity getAllGenres() throws InterruptedException, ExecutionException {

        List<Genres> mGenres = filmService.getAllGenres();

        if (mGenres == null || mGenres.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mGenres);
    }

    @GetMapping("/api/get/list_movie_genres/{id}")
    public ResponseEntity getListMovieGenres(@PathVariable String id) throws InterruptedException, ExecutionException {
        List<Film> mFilm = filmService.getListMovieGenres(id);

        if (mFilm == null || mFilm.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @GetMapping("/api/get/detail_movie/{id}")
    public ResponseEntity getDetail(@PathVariable String id) throws InterruptedException, ExecutionException {

        Film film = filmService.getDetailMovie(id);

        if (film == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    @GetMapping("/api/get/search_name/{name}")
    public ResponseEntity getFilter(@PathVariable String name) throws InterruptedException, ExecutionException {

        List<Film> mFilm = filmService.getListFilter(name);

        if (mFilm == null || mFilm.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @PostMapping("/api/post/genres")
    public ResponseEntity createGenres(@RequestParam(name = "Genres") String genres) {
        if (genres.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Input parameter cannot be empty!");
        }

        boolean check = filmService.saveGenres(genres);

        if (check) {
            return ResponseEntity.status(HttpStatus.OK).body("Success full!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
    }

    @PostMapping("/api/post/movie")
    public ResponseEntity createFIlm(
            @RequestParam(name = "Video") MultipartFile fVideo,
            @RequestParam(name = "Image") MultipartFile fImage,
            @RequestParam(name = "Description") String bio,
            @RequestParam(name = "Title") String title,
            @RequestParam(name = "Cast") String cast,
            @RequestParam(name = "Genres") List<String> genres,
            @RequestParam(name = "LikeCount") int likeCount) {

        if (fVideo.isEmpty() || fImage.isEmpty() || title.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Input parameter cannot be empty!");
        }

        boolean check = false;

        try {
            String videoURL = firebaseFileService.upLoadFile(fVideo);
            String imageURL = firebaseFileService.upLoadFile(fImage);

            Film film = new Film();
            film.setId("");
            film.setCast(cast);
            film.setDescription(bio);
            film.setGenres(genres);
            film.setImageURL(imageURL);
            film.setVideoURL(videoURL);
            film.setTitle(title);
            film.setLikeCount(likeCount);

            check = filmService.saveFilm(film);

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (check) {
            return ResponseEntity.status(HttpStatus.OK).body("Success full");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
    }

}
