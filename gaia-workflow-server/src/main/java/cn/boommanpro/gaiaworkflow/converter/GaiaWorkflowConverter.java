package cn.boommanpro.gaiaworkflow.converter;

import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflow;

public class GaiaWorkflowConverter {

    public static GaiaWorkflowDto convertToDto(GaiaWorkflow entity) {
        if (entity == null) {
            return null;
        }
        
        GaiaWorkflowDto dto = new GaiaWorkflowDto();
        dto.setId(entity.getId());
        dto.setWorkflowCode(entity.getWorkflowCode());
        dto.setWorkflowName(entity.getWorkflowName());
        dto.setWorkflowDesc(entity.getWorkflowDesc());
        dto.setCurrentVersionId(entity.getCurrentVersionId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
    
    public static GaiaWorkflow convertToEntity(GaiaWorkflowDto dto) {
        if (dto == null) {
            return null;
        }
        
        GaiaWorkflow entity = new GaiaWorkflow();
        entity.setId(dto.getId());
        entity.setWorkflowCode(dto.getWorkflowCode());
        entity.setWorkflowName(dto.getWorkflowName());
        entity.setWorkflowDesc(dto.getWorkflowDesc());
        entity.setCurrentVersionId(dto.getCurrentVersionId());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        return entity;
    }
}