package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.modification.CreateModificationTemplateDTO;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.modification.PatchModificationTemplateDTO;
import org.example.services.ModificationTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/modifications")
public class ModificationController {


    private final ModificationTemplateService modificationTemplateService;

    public ModificationController(ModificationTemplateService modificationTemplateService) {
        this.modificationTemplateService = modificationTemplateService;
    }

    @GetMapping("/{id}")
    ResponseEntity<ModificationTemplateDto> getModificationById(@PathVariable Long id){
        ModificationTemplateDto modificationTemplateById = modificationTemplateService
                .getModificationTemplateById(id);
        return ResponseEntity.ok().body(modificationTemplateById);
    }

    @PostMapping("")
    ResponseEntity<ModificationTemplateDto> createModification(@Valid  @RequestBody CreateModificationTemplateDTO createModificationTemplate){
        ModificationTemplateDto modificationTemplate = modificationTemplateService.createModificationTemplate(createModificationTemplate);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{Id}")
                .buildAndExpand(modificationTemplate.id())
                .toUri();

        return ResponseEntity.created(uri).body(modificationTemplate);
    }

    @PatchMapping("/{id}")
    ResponseEntity<ModificationTemplateDto> patchModification(@PathVariable Long id, @Valid @RequestBody PatchModificationTemplateDTO modificationTemplate){
        ModificationTemplateDto updatedModification = modificationTemplateService.patchModificationTemplate(id, modificationTemplate);
        return ResponseEntity.ok().body(updatedModification);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ModificationTemplateDto> deleteModificationById(@PathVariable Long id){
        ModificationTemplateDto deletedModification = modificationTemplateService.deleteModificationTemplateById(id);
        return ResponseEntity.ok().body(deletedModification);
    }






}
