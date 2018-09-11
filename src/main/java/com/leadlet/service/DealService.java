package com.leadlet.service;

import com.leadlet.service.dto.DealDTO;
import com.leadlet.service.dto.DealDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

/**
 * Service Interface for managing Deal.
 */
public interface DealService {

    /**
     * Save a deal.
     *
     * @param dealDTO the entity to save
     * @return the persisted entity
     */
    DealDetailDTO save(DealDTO dealDTO) throws IOException;

    /**
     * Update a deal.
     *
     * @param dealDTO the entity to update
     * @return the persisted entity
     */
    DealDetailDTO update(DealDTO dealDTO) throws IOException;

    DealDetailDTO patch(Long id, Integer priority, Long stageId) throws IOException;


    /**
     * Get all the deals.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DealDTO> findAll(Pageable pageable);

    /**
     * Get the "id" deal.
     *
     * @param id the id of the entity
     * @return the entity
     */
    DealDetailDTO findOne(Long id);

    /**
     * Delete the "id" deal.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    Page<DealDTO> findAllByStageId(Long stageId, Pageable pageable);

    Page<DealDetailDTO> findAllByPersonId(Long personId, Pageable pageable);

    Double getDealTotalByStage(Long stageId);

    Page<DealDTO> search(String filter, Pageable pageable);

    Page<DealDTO> query(String searchQuery, Pageable pageable) throws IOException;
}
