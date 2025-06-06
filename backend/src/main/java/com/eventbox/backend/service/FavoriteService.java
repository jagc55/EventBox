package com.eventbox.backend.service;

import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.model.FavoriteId;
import com.eventbox.backend.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository repo;

    public FavoriteService(FavoriteRepository repo) {
        this.repo = repo;
    }

    public List<Favorite> getAll() {
        return repo.findAll();
    }

    public Optional<Favorite> getById(FavoriteId id) {
        return repo.findById(id);
    }

    public Favorite save(Favorite favorite) {
        return repo.save(favorite);
    }

    public void delete(FavoriteId id) {
        repo.deleteById(id);
    }
}
