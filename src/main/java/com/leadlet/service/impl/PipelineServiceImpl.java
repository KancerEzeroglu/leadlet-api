package com.leadlet.service.impl;

import com.leadlet.domain.AppAccount;
import com.leadlet.repository.StageRepository;
import com.leadlet.security.SecurityUtils;
import com.leadlet.service.PipelineService;
import com.leadlet.domain.Pipeline;
import com.leadlet.repository.PipelineRepository;
import com.leadlet.service.dto.PipelineDTO;
import com.leadlet.service.mapper.PipelineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


/**
 * Service Implementation for managing Pipeline.
 */
@Service
@Transactional
public class PipelineServiceImpl implements PipelineService {

    private final Logger log = LoggerFactory.getLogger(PipelineServiceImpl.class);

    private final PipelineRepository pipelineRepository;

    private final StageRepository stageRepository;


    private final PipelineMapper pipelineMapper;

    public PipelineServiceImpl(PipelineRepository pipelineRepository, StageRepository stageRepository, PipelineMapper pipelineMapper) {
        this.pipelineRepository = pipelineRepository;
        this.pipelineMapper = pipelineMapper;
        this.stageRepository = stageRepository;
    }

    /**
     * Save a pipeline.
     *
     * @param pipelineDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PipelineDTO save(PipelineDTO pipelineDTO) {
        log.debug("Request to save Pipeline : {}", pipelineDTO);
        Pipeline pipeline = pipelineMapper.toEntity(pipelineDTO);
        pipeline.setAppAccount(SecurityUtils.getCurrentUserAppAccountReference());
        pipeline = pipelineRepository.save(pipeline);
        return pipelineMapper.toDto(pipeline);
    }

    /**
     * Update a pipeline.
     *
     * @param pipelineDTO the entity to update
     * @return the persisted entity
     */
    @Override
    public PipelineDTO update(PipelineDTO pipelineDTO) {
        log.debug("Request to update Pipeline : {}", pipelineDTO);

        Pipeline pipeline = pipelineMapper.toEntity(pipelineDTO);
        Pipeline pipelineFromDb = pipelineRepository.findOneByIdAndAppAccount_Id(pipeline.getId(), SecurityUtils.getCurrentUserAppAccountId());
        if (pipelineFromDb != null) {
            //TODO: appaccount'u eklemek dogru fakat appaccount olmadan da kayit hatasi almaliydik.
            pipeline.setAppAccount(SecurityUtils.getCurrentUserAppAccountReference());
            pipeline = pipelineRepository.save(pipeline);
            return pipelineMapper.toDto(pipeline);
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Get all the pipelines.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PipelineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pipelines");
        return pipelineRepository.findByAppAccount_Id(SecurityUtils.getCurrentUserAppAccountId(), pageable)
            .map(pipelineMapper::toDto);
    }

    /**
     * Get one pipeline by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PipelineDTO findOne(Long id) {
        log.debug("Request to get Pipeline : {}", id);
        Pipeline pipeline = pipelineRepository.findOneByIdAndAppAccount_Id(id, SecurityUtils.getCurrentUserAppAccountId());
        return pipelineMapper.toDto(pipeline);
    }

    /**
     * Delete the  pipeline by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("pipeline_delete: request to delete pipeline : {}", id);

        log.debug("pipeline_delete: deleting stages for pipeine: {} started", id);
        stageRepository.deleteByAppAccount_IdAndPipeline_Id(SecurityUtils.getCurrentUserAppAccountId(), id);
        log.debug("pipeline_delete: deleting stages for pipeine: {} finished", id);

        Pipeline pipelineFormDb = pipelineRepository.findOneByIdAndAppAccount_Id(id, SecurityUtils.getCurrentUserAppAccountId());
        if (pipelineFormDb != null) {
            pipelineRepository.delete(id);
        } else {
            throw new EntityNotFoundException();
        }
    }
}
