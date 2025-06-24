package com.eventbox.backend.repository;

import com.eventbox.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEstablishmentId(Long establishmentId);
    List<Event> findAllByOrderByDateAscTimeAsc();
}