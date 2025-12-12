package cn.boommanpro.gaiaworkflow.converter;

import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowTemplateDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowTemplate;

import java.time.LocalDateTime;

public class GaiaWorkflowTemplateConverter {

    public static GaiaWorkflowTemplateDto convertToDto(GaiaWorkflowTemplate entity) {
        if (entity == null) {
            return null;
        }

        GaiaWorkflowTemplateDto dto = new GaiaWorkflowTemplateDto();
        dto.setId(entity.getId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setTemplateName(entity.getTemplateName());
        dto.setTemplateDesc(entity.getTemplateDesc());
        dto.setTemplateData(entity.getTemplateData());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public static GaiaWorkflowTemplate convertToEntity(GaiaWorkflowTemplateDto dto) {
        if (dto == null) {
            return null;
        }

        GaiaWorkflowTemplate entity = new GaiaWorkflowTemplate();
        entity.setId(dto.getId());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setTemplateName(dto.getTemplateName());
        entity.setTemplateDesc(dto.getTemplateDesc());
        entity.setTemplateData(dto.getTemplateData());
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }else {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
