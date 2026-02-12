package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.dtos.combo.*;
import org.example.dtos.mapers.ComboDtoMapper;
import org.example.models.Combo;
import org.example.models.ComboProduct;
import org.example.models.Product;
import org.example.models.SemiProduct;
import org.example.repositories.ComboRepository;
import org.example.repositories.projections.ProductProjection;
import org.example.security.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ComboService {

    private final ComboRepository comboRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final ComboDtoMapper comboDtoMapper;
    private final ProductService productService;


    public ComboService(ComboRepository comboRepository, CustomUserDetailsService customUserDetailsService, ComboDtoMapper comboDtoMapper, ProductService productService){
        this.comboRepository = comboRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.comboDtoMapper = comboDtoMapper;
        this.productService = productService;
    }


    @PreAuthorize("@securityService.isManager()")
    public ComboDetailsDTO getComboDetailsById(Long id){
        Combo combo = getById(id);
        customUserDetailsService.checkAccessToResource(combo.getRestaurantId());
        return comboDtoMapper.toComboDetailsDto(combo);
    }

    @PreAuthorize("@securityService.isManager()")
    public List<Combo> getCombosToMenu() {
        Long restaurantId = customUserDetailsService.getCurrentRestaurantId();
        return comboRepository.findAllByRestaurantId(restaurantId);
    }


    @PreAuthorize("@securityService.isManager()")
    @Transactional
    public ComboDetailsDTO createCombo(CreateComboDTO createComboDTO) {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();

        Combo combo = new Combo();
        combo.setName(createComboDTO.name());
        combo.setPrice(createComboDTO.price());
        combo.setRestaurantId(currentRestaurantId);

        Map<Long, Product> mapOfProducts = getMapOfProducts(createComboDTO);

        Set<ComboProduct> comboProducts = mapToComboProducts(createComboDTO, mapOfProducts);
        combo.setComboProducts(comboProducts);

        Combo savedCombo = comboRepository.save(combo);
        return comboDtoMapper.toComboDetailsDto(savedCombo);
    }

    @PreAuthorize("@securityService.isManager()")
    @Transactional
    public ComboDetailsDTO patchCombo(PatchComboDTO patchComboDTO, Long id) {
        Combo combo = getById(id);
        customUserDetailsService.checkAccessToResource(combo.getRestaurantId());

        if (patchComboDTO.name() != null) {
            combo.setName(patchComboDTO.name());
        }
        if (patchComboDTO.price() != null) {
            combo.setPrice(patchComboDTO.price());
        }

        if (patchComboDTO.components() != null){
            combo.getComboProducts().clear();
            List<Long> productIds = patchComboDTO.components().stream()
                    .map(CreateComboProductDTO::productId).toList();

            Map<Long, Product> productsById = productService.getMapOfProductsByIds(productIds);

            patchComboDTO.components().forEach(component ->{
                ComboProduct newComboProduct = new ComboProduct(
                        component.quantity(),
                        combo,
                        productsById.get(component.productId()),
                        combo.getRestaurantId()
                );
                combo.getComboProducts().add(newComboProduct);
            });

        }

        Combo save = comboRepository.save(combo);
        return comboDtoMapper.toComboDetailsDto(save);

    }

    @PreAuthorize("@securityService.isManager()")
    @Transactional
    public DeletedComboDto deleteCombo(Long id) {
        Combo combo = getById(id);
        customUserDetailsService.checkAccessToResource(combo.getRestaurantId());

        comboRepository.delete(combo);

        return comboDtoMapper.toDeletedComboDto(combo);
    }


    private Combo getById(Long id){
        return comboRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Combo with id "+id+" not found"));

    }

    private Map<Long,Product> getMapOfProducts(CreateComboDTO createComboDTO){
         List<Long> ids = createComboDTO.components().stream()
                .map(CreateComboProductDTO::productId)
                .distinct()
                .toList();

         return productService.getMapOfProductsByIds(ids);

    }

    private Set<ComboProduct> mapToComboProducts(CreateComboDTO createComboDTO, Map<Long,Product> productMap){
        return createComboDTO.components().stream()
                .map(createComboProductDTO -> {
                    ComboProduct comboProduct = new ComboProduct();
                    comboProduct.setCombo(comboProduct.getCombo());
                    comboProduct.setProduct(productMap.get(createComboProductDTO.productId()));
                    comboProduct.setQuantity(createComboProductDTO.quantity());

                    return comboProduct;
                }).collect(Collectors.toSet());
    }
}
