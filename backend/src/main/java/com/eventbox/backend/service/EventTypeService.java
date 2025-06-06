package com.eventbox.backend.service;

import com.eventbox.backend.model.EventType;
import com.eventbox.backend.repository.EventTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventTypeService {

    private final EventTypeRepository repo;

    public EventTypeService(EventTypeRepository repo) {
        this.repo = repo;
    }

    public List<EventType> getAll() {
        return repo.findAll();
    }

    public Optional<EventType> getById(Long id) {
        return repo.findById(id);
    }

    public EventType save(EventType eventType) {
        return repo.save(eventType);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
