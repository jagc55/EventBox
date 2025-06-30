package com.eventbox.backend.repository;

import com.eventbox.backend.model.Establishment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    @Query("SELECT e FROM Establishment e WHERE e.user.id = :userId")
    List<Establishment> findByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Establishment e WHERE e.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}