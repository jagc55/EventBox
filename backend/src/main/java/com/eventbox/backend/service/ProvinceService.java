package com.eventbox.backend.service;

import com.eventbox.backend.model.Province;
import com.eventbox.backend.repository.ProvinceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinceService {

    private final ProvinceRepository repo;

    public ProvinceService(ProvinceRepository repo) {
        this.repo = repo;
    }

    public List<Province> getAll() {
        return repo.findAll();
    }

    public Optional<Province> getById(Long id) {
        return repo.findById(id);
    }

    public Province save(Province province) {
        return repo.save(province);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
