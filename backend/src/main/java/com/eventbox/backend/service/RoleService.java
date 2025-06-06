package com.eventbox.backend.service;

import com.eventbox.backend.model.Role;
import com.eventbox.backend.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository repo;

    public RoleService(RoleRepository repo) {
        this.repo = repo;
    }

    public List<Role> getAll() {
        return repo.findAll();
    }

    public Optional<Role> getById(Long id) {
        return repo.findById(id);
    }

    public Role save(Role role) {
        return repo.save(role);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
