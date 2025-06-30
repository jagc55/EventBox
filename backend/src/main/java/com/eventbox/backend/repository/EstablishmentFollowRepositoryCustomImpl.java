package com.eventbox.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EstablishmentFollowRepositoryCustomImpl implements EstablishmentFollowRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void deleteByEstablishmentId(Long establishmentId) {
        entityManager.createQuery("DELETE FROM EstablishmentFollow ef WHERE ef.establishment.id = :establishmentId")
                .setParameter("establishmentId", establishmentId)
                .executeUpdate();
    }
}
