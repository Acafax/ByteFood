package org.example.controllers;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.example.dtos.semiProducts.CreateSemiProductDto;
import org.example.dtos.semiProducts.PatchSemiProductDTO;
import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.services.SemiProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/semi-products")
public class SemiProductController {


    private final SemiProductService semiProductService;

    public SemiProductController(SemiProductService semiProductService) {
        this.semiProductService = semiProductService;
    }



    @GetMapping()
    public ResponseEntity<List<SemiProductDTO>> getRestaurantSemiProducts(){
        List<SemiProductDTO> semiProducts = semiProductService.getRestaurantSemiProducts();
        return ResponseEntity.ok().body(semiProducts);
    }

    @PostMapping()
    public ResponseEntity<SemiProductDTO> createSemiProduct(@RequestBody @Valid CreateSemiProductDto createSemiProduct){

        SemiProductDTO semiProduct = semiProductService.createSemiProduct(createSemiProduct);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{Id}")
                .buildAndExpand(semiProduct.id())
                .toUri();

        return ResponseEntity.created(uri).body(semiProduct);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SemiProductDTO> patchSemiProduct(@Valid @RequestBody PatchSemiProductDTO patchSemiProductDTO, @PathVariable Long id){
        SemiProductDTO semiProductDTO = semiProductService.patchSemiProduct(id, patchSemiProductDTO);
        return ResponseEntity.ok().body(semiProductDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SemiProductDTO> deleteSemiProduct(@PathVariable Long id){
        SemiProductDTO delete = semiProductService.delete(id);
        return ResponseEntity.ok().body(delete);
    }
}
