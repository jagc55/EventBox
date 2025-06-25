package com.eventbox.backend.service;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.model.FavoriteId;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.FavoriteRepository;
import com.eventbox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository repo;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public FavoriteService(FavoriteRepository repo, UserRepository userRepository, EventRepository eventRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
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

    public List<Favorite> getFavoritesByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public boolean isFavorite(Long userId, Long eventId) {
        FavoriteId id = new FavoriteId(userId, eventId);
        return repo.existsById(id);
    }

    public void addToFavorites(Long userId, Long eventId) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar que el evento existe
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        // Verificar que no esté ya en favoritos
        FavoriteId id = new FavoriteId(userId, eventId);
        if (repo.existsById(id)) {
            throw new IllegalArgumentException("El evento ya está en favoritos");
        }

        // Crear y guardar el favorito
        Favorite favorite = new Favorite(user, event, LocalDateTime.now());
        repo.save(favorite);
    }

    public void removeFromFavorites(Long userId, Long eventId) {
        FavoriteId id = new FavoriteId(userId, eventId);
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("El evento no está en favoritos");
        }
        repo.deleteById(id);
    }
}
