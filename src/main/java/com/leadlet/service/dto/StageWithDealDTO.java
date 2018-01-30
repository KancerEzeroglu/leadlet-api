package com.leadlet.service.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the Stage entity.
 */
public class StageWithDealDTO implements Serializable {

    private Long id;

    private String name;

    private Long pipelineId;

    private String color;

    private List<DealDTO> dealList;

    long dealTotalCount;

    int dealPageCount;

    Double dealTotalPotential;

    public StageWithDealDTO(StageDTO stageDTO) {
        this.setColor(stageDTO.getColor());
        this.setId(stageDTO.getId());
        this.setName(stageDTO.getName());
        this.setPipelineId(stageDTO.getPipelineId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public StageWithDealDTO setDealList(List<DealDTO> dealList) {
        this.dealList = dealList;
        return this;
    }

    public List<DealDTO> getDealList() {
        return dealList;
    }

    public long getDealTotalCount() {
        return dealTotalCount;
    }

    public int getDealPageCount() {
        return dealPageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StageWithDealDTO stageDTO = (StageWithDealDTO) o;
        if(stageDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stageDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StageWithDealDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", pipelineId=" + pipelineId +
            ", color='" + color + '\'' +
            ", dealList=" + dealList +
            ", dealTotalCount=" + dealTotalCount +
            ", dealPageCount=" + dealPageCount +
            '}';
    }

    public void setDealTotalCount(long dealTotalCount) {
        this.dealTotalCount = dealTotalCount;
    }

    public void setDealPageCount(int dealPageCount) {
        this.dealPageCount = dealPageCount;
    }

    public void setDealTotalPotential(Double dealTotalPotential) {
        this.dealTotalPotential = dealTotalPotential;
    }

    public Double getDealTotalPotential() {
        return dealTotalPotential;
    }
}