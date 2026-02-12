package org.example.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.mapers.SemiProductsDtoMapper;
import org.example.dtos.semiProducts.CreateSemiProductDto;
import org.example.dtos.semiProducts.PatchSemiProductDTO;
import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.models.SemiProduct;
import org.example.models.UnitType;
import org.example.repositories.SemiProductRepository;
import org.example.security.CustomUserDetailsService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SemiProductService {


    private final SemiProductRepository semiProductRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final SemiProductsDtoMapper semiProductsDtoMapper;

    public SemiProductService(SemiProductRepository semiProductRepository, CustomUserDetailsService customUserDetailsService, SemiProductsDtoMapper semiProductsDtoMapper) {
        this.semiProductRepository = semiProductRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.semiProductsDtoMapper = semiProductsDtoMapper;
    }

    public boolean existsById(Long id){
        return semiProductRepository.existsById(id);
    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.getRestaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public SemiProduct getById(Long id){
        return semiProductRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("SemiProduct with id " + id + " not found"));
    }

    @PreAuthorize("@securityService.isManager()")
    public SemiProductDTO createSemiProduct(CreateSemiProductDto createSemiProduct){
        checkIfSemiProductIsUnique(createSemiProduct);

        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        SemiProduct semiProduct = SemiProductsDtoMapper.mapToSemiProduct(createSemiProduct, currentRestaurantId);

        SemiProduct save = semiProductRepository.save(semiProduct);

        return semiProductsDtoMapper.mapToSemiProductDto(save);
    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public SemiProductDTO delete(Long id){
        SemiProduct semiProduct = getById(id);
        semiProductRepository.delete(semiProduct);
        return semiProductsDtoMapper.mapToSemiProductDto(semiProduct);
    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager() ")
    public SemiProductDTO patchSemiProduct(Long id, PatchSemiProductDTO patchSemiProductDTO){
        SemiProduct semiProduct = getById(id);

        if(patchSemiProductDTO.name()!= null){
            semiProduct.setName(patchSemiProductDTO.name());
        }
        if(patchSemiProductDTO.carbohydrate()!= null){
            semiProduct.setCarbohydrate(patchSemiProductDTO.carbohydrate());
        }
        if(patchSemiProductDTO.fat()!= null){
            semiProduct.setFat(patchSemiProductDTO.fat());
        }
        if(patchSemiProductDTO.protein()!= null){
            semiProduct.setProtein(patchSemiProductDTO.protein());
        }
        if(patchSemiProductDTO.unit()!= null){
            semiProduct.setUnit(patchSemiProductDTO.unit());
        }

        SemiProduct save = semiProductRepository.save(semiProduct);

        return semiProductsDtoMapper.mapToSemiProductDto(save);
    }

    @PreAuthorize("@securityService.isManager()")
    public List<SemiProductDTO> getRestaurantSemiProducts() {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        List<SemiProduct> semiProducts = semiProductRepository.getSemiProducts(currentRestaurantId);

        return semiProductsDtoMapper.mapToSemiProductsDtoList(semiProducts);
    }

    private void checkIfSemiProductIsUnique(CreateSemiProductDto createSemiProduct){
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();

        if (semiProductRepository.existsSemiProductByNameAndUnitAndRestaurantId(
                createSemiProduct.name(),
                createSemiProduct.unit(),
                currentRestaurantId)){
            throw new EntityExistsException(
                    String.format("SemiProduct with name '%s', unit '%s' already exists in restaurant",
                            createSemiProduct.name(), createSemiProduct.unit())
            );
        }
    }

    @PreAuthorize("@securityService.isManager()")
    public List<UnitType> getSemiProductsUnits() {
        Long restaurantId = customUserDetailsService.getCurrentRestaurantId();
        return semiProductRepository.getSemiProductsUnits(restaurantId);
    }


    public Set<SemiProduct> getByIds(Set<Long> longs) {
        return semiProductRepository.getSemiProductsByIds(longs);

    }
}
