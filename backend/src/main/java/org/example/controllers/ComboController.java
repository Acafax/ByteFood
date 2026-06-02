package org.example.controllers;


import jakarta.validation.Valid;
import org.example.dtos.combo.*;
import org.example.services.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/combos")
@RestController
public class ComboController {


    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ComboDetailsDTO> getComboDetails(@PathVariable Long id) {
        ComboDetailsDTO comboDetailsById = comboService.getComboDetailsById(id);

        return ResponseEntity.ok(comboDetailsById);

    }

    @PostMapping("/")
    public ResponseEntity<ComboDetailsDTO> createCombo(@Valid  @RequestBody CreateComboDTO createComboDTO){
        ComboDetailsDTO createdCombo = comboService.createCombo(createComboDTO);
        return ResponseEntity.ok(createdCombo);
    }

    @PatchMapping("/{id}")
    ResponseEntity<ComboDetailsDTO> patchCombo(@Valid @RequestBody PatchComboDTO comboDTO, @PathVariable Long id){
        ComboDetailsDTO patchedCombo = comboService.patchCombo(comboDTO, id);
        return ResponseEntity.ok().body(patchedCombo);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<DeletedComboDto> deleteCombo(@PathVariable Long id){
        DeletedComboDto deletedComboDto = comboService.deleteCombo(id);
        return ResponseEntity.ok().body(deletedComboDto);
    }

}
