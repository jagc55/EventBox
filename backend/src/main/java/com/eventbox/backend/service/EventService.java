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
    private final EventSearchService eventSearchService;

    public EventService(EventRepository repo, 
                       FavoriteRepository favoriteRepository, 
                       CommentRepository commentRepository,
                       EventSearchService eventSearchService) {
        this.repo = repo;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
        this.eventSearchService = eventSearchService;
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
        Event savedEvent = repo.save(event);
        eventSearchService.indexEvent(savedEvent);
        return savedEvent;
    }

    @Transactional
    public void delete(Long id) {
        favoriteRepository.deleteByEventId(id);
        commentRepository.deleteByEventId(id);
        eventSearchService.deleteEventIndex(id.toString());
        repo.deleteById(id);
    }

    public List<Event> findByEstablishmentId(Long id) {
        return repo.findByEstablishmentId(id);
    }

    public Event update(Event event) {
        // Se asume que el evento ya tiene el ID correcto
        Event updatedEvent = repo.save(event);
        eventSearchService.indexEvent(updatedEvent);
        return updatedEvent;
    }

    public List<Event> getEventsByFollowedUsers(Long userId) {
        return repo.findEventsByFollowedUsersNotStarted(userId);
    }
}
