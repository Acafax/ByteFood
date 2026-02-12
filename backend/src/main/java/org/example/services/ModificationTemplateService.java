package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.mapers.ModificationTemplateDtoMapper;
import org.example.dtos.modification.CreateModificationTemplateDTO;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.modification.PatchModificationTemplateDTO;
import org.example.models.ModificationTemplate;
import org.example.models.SemiProduct;
import org.example.repositories.ModificationTemplateRepository;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.example.security.CustomUserDetailsService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModificationTemplateService {


    private final ModificationTemplateDtoMapper modificationTemplateDtoMapper;
    private final ModificationTemplateRepository modificationTemplateRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final SemiProductService semiProductService;

    public ModificationTemplateService(ModificationTemplateDtoMapper modificationTemplateDtoMapper, ModificationTemplateRepository modificationTemplateRepository, CustomUserDetailsService customUserDetailsService, SemiProductService semiProductService) {
        this.modificationTemplateDtoMapper = modificationTemplateDtoMapper;
        this.modificationTemplateRepository = modificationTemplateRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.semiProductService = semiProductService;
    }

    public List<ModificationTemplateDto> getRestaurantModificationTemplates() {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        List<ModificationTemplate> allByRestaurantId = modificationTemplateRepository
                .findAllWithSemiProductByRestaurantId(currentRestaurantId);

        return allByRestaurantId.stream()
                .map(modificationTemplateDtoMapper::mapToDto)
                .toList();
    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public ModificationTemplateDto getModificationTemplateById(Long id) {
        ModificationTemplateProjection modificationTemplate = modificationTemplateRepository.findModificationTemplateById(id)
                .orElseThrow(()-> new EntityNotFoundException("Modification Template with ID" +id +" not found "));
        return modificationTemplateDtoMapper.mapToDto(modificationTemplate);
    }

    @PreAuthorize("@securityService.isManager()")
    public ModificationTemplateDto createModificationTemplate(CreateModificationTemplateDTO modificationTemplate) {
        ModificationTemplate modificationToCreate = modificationTemplateDtoMapper.mapToEntity(modificationTemplate);

        SemiProduct semiProduct;
        if (modificationTemplate.semiProductId() != null){
            semiProduct = semiProductService.getById(modificationTemplate.semiProductId());
            modificationToCreate.setSemiProduct(semiProduct);
        }

        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        modificationToCreate.setRestaurantId(currentRestaurantId);

        ModificationTemplate save = modificationTemplateRepository.save(modificationToCreate);


        return modificationTemplateDtoMapper.mapToDto(save);

    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public ModificationTemplateDto patchModificationTemplate(Long id, PatchModificationTemplateDTO modificationTemplate) {
        ModificationTemplate modificationTemplateById = modificationTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modification Template with ID" +id +" not found "));

        if (modificationTemplate.name() != null){
            modificationTemplateById.setName(modificationTemplate.name());
        }
        if (modificationTemplate.price() != null){
            modificationTemplateById.setPrice(modificationTemplate.price());
        }
        if (modificationTemplate.category() != null){
            modificationTemplateById.setCategory(modificationTemplate.category());
        }
        if (modificationTemplate.semiProductId() != null){
            SemiProduct semiProduct = semiProductService.getById(modificationTemplate.semiProductId());
            modificationTemplateById.setSemiProduct(semiProduct);
        }

        ModificationTemplate save = modificationTemplateRepository.save(modificationTemplateById);
        return modificationTemplateDtoMapper.mapToDto(save);


    }


    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public ModificationTemplateDto deleteModificationTemplateById(Long id) {
        ModificationTemplate modificationTemplate = modificationTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modification Template with ID " + id + " not found"));

        ModificationTemplateDto dto = modificationTemplateDtoMapper.mapToDto(modificationTemplate);
        modificationTemplateRepository.delete(modificationTemplate);

        return dto;
    }
}
