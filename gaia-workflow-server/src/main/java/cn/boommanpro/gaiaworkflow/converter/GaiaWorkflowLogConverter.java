package cn.boommanpro.gaiaworkflow.converter;

import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowLogDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowLog;

public class GaiaWorkflowLogConverter {

    public static GaiaWorkflowLogDto convertToDto(GaiaWorkflowLog entity) {
        if (entity == null) {
            return null;
        }
        
        GaiaWorkflowLogDto dto = new GaiaWorkflowLogDto();
        dto.setId(entity.getId());
        dto.setWorkflowCode(entity.getWorkflowCode());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setExecutionId(entity.getExecutionId());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setStatus(entity.getStatus());
        dto.setInputParams(entity.getInputParams());
        dto.setOutputParams(entity.getOutputParams());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setExecutionDuration(entity.getExecutionDuration());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }
    
    public static GaiaWorkflowLog convertToEntity(GaiaWorkflowLogDto dto) {
        if (dto == null) {
            return null;
        }
        
        GaiaWorkflowLog entity = new GaiaWorkflowLog();
        entity.setId(dto.getId());
        entity.setWorkflowCode(dto.getWorkflowCode());
        entity.setVersionNumber(dto.getVersionNumber());
        entity.setExecutionId(dto.getExecutionId());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setStatus(dto.getStatus());
        entity.setInputParams(dto.getInputParams());
        entity.setOutputParams(dto.getOutputParams());
        entity.setErrorMessage(dto.getErrorMessage());
        entity.setExecutionDuration(dto.getExecutionDuration());
        entity.setCreatedAt(dto.getCreatedAt());
        
        return entity;
    }
}