/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tranquangduy.firebasewithspring.controller;

import com.tranquangduy.firebasewithspring.authority.MyUserDetail;
import com.tranquangduy.firebasewithspring.model.Film;
import com.tranquangduy.firebasewithspring.model.Genres;
import com.tranquangduy.firebasewithspring.model.LoginReponse;
import com.tranquangduy.firebasewithspring.model.User;
import com.tranquangduy.firebasewithspring.security.JwtProvider;
import com.tranquangduy.firebasewithspring.service.FirebaseFilleService;
import com.tranquangduy.firebasewithspring.service.FilmService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.tranquangduy.firebasewithspring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author UserName
 */
@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
    @Autowired
    private FirebaseFilleService firebaseFileService;
    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping("/")
    public ResponseEntity createPage() throws InterruptedException, ExecutionException {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World!");
    }


    @PostMapping("/signup")
    public ResponseEntity createUser(@RequestBody User user) {
        boolean check = userService.saveUser(user);

        if (check) {
            return ResponseEntity.status(HttpStatus.OK).body("Success full!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
    }

    @PostMapping("/auth")
    public ResponseEntity authUser(@RequestBody User user) throws Exception {
        Authentication authentication = authentication(user.getUsername(),user.getPassword());
        MyUserDetail myUserDetail = (MyUserDetail) authentication.getPrincipal();
        log.info("INFO AUTHENTICATION: " + myUserDetail.getUsername());

        final String token = jwtProvider.generateToken(myUserDetail);
        return ResponseEntity.ok(new LoginReponse(token));
    }

    //xác thực với user detail
    //UsernamePasswordAuthenticationToken -> AuthenticationManager -> AuthenticationProvider -> UserDetailService
    private Authentication authentication(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
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

        if (mGenres == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mGenres);
    }

    @GetMapping("/api/get/list_movie_genres/{id}")
    public ResponseEntity getListMovieGenres(@PathVariable String id) throws InterruptedException, ExecutionException {
        List<Film> mFilm = filmService.getListMovieGenres(id);

        if (mFilm == null) {
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

        if (mFilm == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @GetMapping("/api/get/highlights")
    public ResponseEntity getHighlights() throws InterruptedException, ExecutionException {

        List<Film> mFilm = filmService.getListHighlights();

        if (mFilm == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @GetMapping("/api/get/last_time_movie")
    public ResponseEntity getLastTimeMovie() throws InterruptedException, ExecutionException {

        List<Film> mFilm = filmService.getLastTimeUpdate();

        if (mFilm == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(mFilm);
    }

    @PutMapping("/api/put/update_movie")
    public ResponseEntity updateMovie(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "key") String key,
            @RequestParam(name = "data") String data){

        boolean check = filmService.updateMovie(id,key,data);

        if (!check) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update fail!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Update success!");
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
    public ResponseEntity createFilm(
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
            log.error("Upload file fail!");
            ex.printStackTrace();
        }

        if (check) {
            return ResponseEntity.status(HttpStatus.OK).body("Success full");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
    }

}
