package com.leadlet.repository;

import com.leadlet.domain.Timeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimelineRepository extends JpaRepository<Timeline,Long> {
    Page<Timeline> findByAppAccount_Id(Long appAccountId, Pageable page);
    Page<Timeline> findByContact_IdAndAppAccount_Id(Long contactId, Long appAccountId, Pageable page);
    Page<Timeline> findByUser_IdAndAppAccount_Id(Long userId, Long appAccountId, Pageable page);
}
