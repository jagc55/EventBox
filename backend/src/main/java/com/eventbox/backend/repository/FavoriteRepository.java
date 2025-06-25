package com.eventbox.backend.repository;

import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    List<Favorite> findByUserId(@Param("userId") Long userId);
}