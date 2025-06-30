package com.eventbox.backend.service;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.repository.CommentRepository;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository repo;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;

    public EventService(EventRepository repo, FavoriteRepository favoriteRepository, CommentRepository commentRepository) {
        this.repo = repo;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
    }

    public List<Event> getAll() {
        return repo.findAll();
    }

    public List<Event> getAllOrderedByDate() {
        return repo.findAllByOrderByDateAscTimeAsc();
    }

    public Optional<Event> getById(Long id) {
        return repo.findById(id);
    }

    public Event save(Event event) {
        return repo.save(event);
    }

    @Transactional
    public void delete(Long id) {
        favoriteRepository.deleteByEventId(id);
        commentRepository.deleteByEventId(id);
        repo.deleteById(id);
    }

    public List<Event> findByEstablishmentId(Long id) {
        return repo.findByEstablishmentId(id);
    }

    public Event update(Event event) {
        // Se asume que el evento ya tiene el ID correcto
        return repo.save(event);
    }

    public List<Event> getEventsByFollowedUsers(Long userId) {
        return repo.findEventsByFollowedUsersNotStarted(userId);
    }
}
