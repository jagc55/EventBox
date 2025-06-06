package com.eventbox.backend.repository;

import com.eventbox.backend.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
}