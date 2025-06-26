package com.eventbox.backend.repository;

import com.eventbox.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEstablishmentId(Long establishmentId);
    List<Event> findAllByOrderByDateAscTimeAsc();
    
    @Query("SELECT e FROM Event e " +
           "JOIN UserFollow uf ON e.user.id = uf.followed.id " +
           "WHERE uf.follower.id = :userId " +
           "AND (e.date > CURRENT_DATE OR (e.date = CURRENT_DATE AND e.time > CURRENT_TIME)) " +
           "ORDER BY e.date ASC, e.time ASC")
    List<Event> findEventsByFollowedUsersNotStarted(@Param("userId") Long userId);
}