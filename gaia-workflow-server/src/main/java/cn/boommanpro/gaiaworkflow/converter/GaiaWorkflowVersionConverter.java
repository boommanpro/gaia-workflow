package cn.boommanpro.gaiaworkflow.converter;

import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowVersionDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowVersion;

public class GaiaWorkflowVersionConverter {

    public static GaiaWorkflowVersionDto convertToDto(GaiaWorkflowVersion entity) {
        if (entity == null) {
            return null;
        }
        
        GaiaWorkflowVersionDto dto = new GaiaWorkflowVersionDto();
        dto.setId(entity.getId());
        dto.setWorkflowCode(entity.getWorkflowCode());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setVersionDesc(entity.getVersionDesc());
        dto.setWorkflowData(entity.getWorkflowData());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setIsCurrent(entity.getIsCurrent());
        
        return dto;
    }
    
    public static GaiaWorkflowVersion convertToEntity(GaiaWorkflowVersionDto dto) {
        if (dto == null) {
            return null;
        }
        
        GaiaWorkflowVersion entity = new GaiaWorkflowVersion();
        entity.setId(dto.getId());
        entity.setWorkflowCode(dto.getWorkflowCode());
        entity.setVersionNumber(dto.getVersionNumber());
        entity.setVersionDesc(dto.getVersionDesc());
        entity.setWorkflowData(dto.getWorkflowData());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setIsCurrent(dto.getIsCurrent());
        
        return entity;
    }
}