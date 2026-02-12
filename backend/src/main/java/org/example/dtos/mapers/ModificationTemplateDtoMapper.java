package org.example.dtos.mapers;

import org.example.dtos.modification.CreateModificationTemplateDTO;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.models.ModificationTemplate;
import org.example.models.SemiProduct;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.springframework.stereotype.Component;

@Component
public class ModificationTemplateDtoMapper {


    public ModificationTemplateDto mapToDto(ModificationTemplate modificationTemplate, SemiProduct semiProduct){
        return new ModificationTemplateDto(
            modificationTemplate.getId(),
            modificationTemplate.getName(),
            modificationTemplate.getPrice(),
            modificationTemplate.getCategory(),
            semiProduct.getId(),
            modificationTemplate.getRestaurantId()
        );
    }

    public ModificationTemplateDto mapToDto(ModificationTemplate modificationTemplate){
        Long semiProductId = modificationTemplate.getSemiProduct() != null
                ? modificationTemplate.getSemiProduct().getId()
                : null;

        return new ModificationTemplateDto(
                modificationTemplate.getId(),
                modificationTemplate.getName(),
                modificationTemplate.getPrice(),
                modificationTemplate.getCategory(),
                semiProductId,
                modificationTemplate.getRestaurantId()
        );
    }

    public ModificationTemplateDto mapToDto(ModificationTemplateProjection modificationTemplate){
        Long semiProductId = modificationTemplate.getSemiProductId() != null
                ? modificationTemplate.getSemiProductId()
                : null;

        return new ModificationTemplateDto(
                modificationTemplate.getId(),
                modificationTemplate.getName(),
                modificationTemplate.getPrice(),
                modificationTemplate.getCategory(),
                semiProductId,
                modificationTemplate.getRestaurantId()
        );
    }


    public ModificationTemplate mapToEntity(CreateModificationTemplateDTO modificationTemplate) {
        return new ModificationTemplate(
                modificationTemplate.name(),
                modificationTemplate.price(),
                modificationTemplate.category()
        );


    }

}


