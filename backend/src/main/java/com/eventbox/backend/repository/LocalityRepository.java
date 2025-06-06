package com.eventbox.backend.repository;

import com.eventbox.backend.model.Locality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalityRepository extends JpaRepository<Locality, Long> {
}