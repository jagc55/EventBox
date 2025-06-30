package com.eventbox.backend.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EstablishmentFollowRepositoryCustom {
    @Modifying
    @Transactional
    @Query("DELETE FROM EstablishmentFollow ef WHERE ef.establishment.id = :establishmentId")
    void deleteByEstablishmentId(@Param("establishmentId") Long establishmentId);
}
