package com.leadlet.service.mapper;

import com.leadlet.domain.*;
import com.leadlet.service.dto.DealDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Deal and its DTO DealDTO.
 */
@Mapper(componentModel = "spring", uses = {PipelineStageMapper.class, ContactMapper.class, AppAccountMapper.class, UserMapper.class, })
public interface DealMapper extends EntityMapper <DealDTO, Deal> {

    @Mapping(source = "pipelineStage.id", target = "pipelineStageId")

    @Mapping(source = "person.id", target = "personId")

    @Mapping(source = "organization.id", target = "organizationId")

    @Mapping(source = "appAccount.id", target = "appAccountId")

    @Mapping(source = "user.id", target = "userId")
    DealDTO toDto(Deal deal);

    @Mapping(source = "pipelineStageId", target = "pipelineStage")

    @Mapping(source = "personId", target = "person")

    @Mapping(source = "organizationId", target = "organization")

    @Mapping(source = "appAccountId", target = "appAccount")

    @Mapping(source = "userId", target = "user")
    Deal toEntity(DealDTO dealDTO);
    default Deal fromId(Long id) {
        if (id == null) {
            return null;
        }
        Deal deal = new Deal();
        deal.setId(id);
        return deal;
    }
}