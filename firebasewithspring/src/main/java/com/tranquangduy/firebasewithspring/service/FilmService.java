/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tranquangduy.firebasewithspring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.tranquangduy.firebasewithspring.model.Film;
import com.tranquangduy.firebasewithspring.model.Genres;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author UserName
 */
@Service
public class FilmService {

    @Autowired
    private FirebaseInitializer db;
    private static final String COLLECTION_NAME = "Film";

    String VIETNAMESE_DIACRITIC_CHARACTERS
            = "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ";

    public boolean saveFilm(Film film) {

        DocumentReference docRef = db.getFirebase().collection(COLLECTION_NAME).document();
        String id = docRef.getId();
        film.setId(id);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", film.getId());
        map.put("title", film.getTitle());
        map.put("imageURL", film.getImageURL());
        map.put("description", film.getDescription());
        map.put("cast", film.getCast());
        map.put("genres", film.getGenres());
        map.put("videoURL", film.getVideoURL());
        map.put("likeCount", film.getLikeCount());

        ApiFuture<WriteResult> result = docRef.set(map);

        try {
            result.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(FilmService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;

    }

    public boolean saveGenres(String genres) {
        DocumentReference docRef = db.getFirebase().collection("Genres").document();
        String id = docRef.getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", genres);
        ApiFuture<WriteResult> result = docRef.set(map);

        try {
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger(FilmService.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }

        return true;
    }

    public Film getDetailMovie(String id) throws InterruptedException, ExecutionException {

        List<String> mNameGenres = new ArrayList<>();
        ApiFuture<DocumentSnapshot> future = db.getFirebase().collection(COLLECTION_NAME).document(id).get();
        ApiFuture<QuerySnapshot> future1 = db.getFirebase().collection("Genres").get();

        DocumentSnapshot snapshot = future.get();
        Film film = snapshot.toObject(Film.class);

        if (film == null) {
            return null;
        }

        for (DocumentSnapshot document : future1.get().getDocuments()) {
            Genres genres = document.toObject(Genres.class);
            if (genres == null) {
                return null;
            }
            if (film.getGenres().contains(genres.getId())) {
                mNameGenres.add(genres.getName());
            }
        }
        film.setGenres(mNameGenres);

        return film;
    }

    public List<Film> getListFilter(String name) throws InterruptedException, ExecutionException {
        List<Film> mFilm = new ArrayList<>();
        String result = StringUtils.removeAccent(name.toLowerCase());
        String t[] = result.split(" ");
        String result1;

        boolean check = false;

        Query query = db.getFirebase().collection("Film").orderBy("title").limit(20);

        ApiFuture<QuerySnapshot> future = query.get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Film film = doc.toObject(Film.class);
            if (film == null) {
                return null;
            }

            result1 = StringUtils.removeAccent(film.getTitle().toLowerCase());

            for (String a : t) {
                if (result1.contains(a)) {
                    check = true;
                }
            }

            if (check) {
                Film temp = getDetailMovie(film.getId());
                mFilm.add(temp);
                check = false;
            }

        }

        return mFilm;
    }

    public List getListMovieGenres(String id) throws InterruptedException, ExecutionException {
        List<Film> mFilm = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.getFirebase().collection(COLLECTION_NAME).get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Film film = doc.toObject(Film.class);

            if (film == null) {
                return null;
            }

            if (film.getGenres().contains(id)) {
                Film t = getDetailMovie(film.getId());
                if (t != null) {
                    mFilm.add(t);
                }
            }
        }

        return mFilm;
    }

    public List getAllGenres() throws InterruptedException, ExecutionException {
        List<Genres> mGenres = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.getFirebase().collection("Genres").get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Genres genres = doc.toObject(Genres.class);
            if (genres == null) {
                return null;
            }

            mGenres.add(genres);
        }

        return mGenres;
    }

    public List getAllMovie() throws InterruptedException, ExecutionException {
        List<Film> mFilm = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.getFirebase().collection(COLLECTION_NAME).get();
        ApiFuture<QuerySnapshot> future1 = db.getFirebase().collection("Genres").get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Film film = doc.toObject(Film.class);
            List<String> mNameGenres = new ArrayList<>();

            if (film == null) {
                return null;
            }

            for (DocumentSnapshot document : future1.get().getDocuments()) {
                Genres genres = document.toObject(Genres.class);

                if (genres == null) {
                    return null;
                }

                if (film.getGenres().contains(genres.getId())) {
                    mNameGenres.add(genres.getName());
                }
            }
            film.setGenres(mNameGenres);
            mFilm.add(film);
        }

        return mFilm;
    }

}
