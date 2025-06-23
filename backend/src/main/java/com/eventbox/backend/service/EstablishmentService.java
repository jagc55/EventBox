package com.eventbox.backend.service;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.repository.EstablishmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository repo;

    public EstablishmentService(EstablishmentRepository repo) {
        this.repo = repo;
    }

    public List<Establishment> getAll() {
        return repo.findAll();
    }

    public Optional<Establishment> getById(Long id) {
        return repo.findById(id);
    }

    public Establishment save(Establishment establishment) {
        return repo.save(establishment);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Establishment> getByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
}
