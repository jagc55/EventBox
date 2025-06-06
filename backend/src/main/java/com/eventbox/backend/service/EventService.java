package com.eventbox.backend.service;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<Event> getAll() {
        return repo.findAll();
    }

    public Optional<Event> getById(Long id) {
        return repo.findById(id);
    }

    public Event save(Event event) {
        return repo.save(event);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
