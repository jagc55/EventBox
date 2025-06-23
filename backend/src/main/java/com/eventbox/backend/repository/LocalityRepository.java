package com.eventbox.backend.repository;

import com.eventbox.backend.model.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalityRepository extends JpaRepository<Locality, Long> {
    @Query("SELECT l FROM Locality l WHERE l.province.name = :provinceName")
    List<Locality> findByProvinceName(@Param("provinceName") String provinceName);
}