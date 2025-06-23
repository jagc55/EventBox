package com.eventbox.backend.service;

import com.eventbox.backend.model.Locality;
import com.eventbox.backend.repository.LocalityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalityService {

    private final LocalityRepository repo;

    public LocalityService(LocalityRepository repo) {
        this.repo = repo;
    }

    public List<Locality> getAll() {
        return repo.findAll();
    }

    public Optional<Locality> getById(Long id) {
        return repo.findById(id);
    }

    public Locality save(Locality locality) {
        return repo.save(locality);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Locality> getByProvinceName(String provinceName) {
        return repo.findByProvinceName(provinceName);
    }
}
