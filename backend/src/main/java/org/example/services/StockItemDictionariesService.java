package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.mapers.StockItemDictionariesDTOMapper;
import org.example.dtos.stockItemDictionary.CreateStockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.PatchStockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryWithoutSemiProductDTO;
import org.example.models.SemiProduct;
import org.example.models.StockItemDictionary;
import org.example.repositories.StockItemDictionariesRepository;
import org.example.repositories.projections.StockItemDictionaryProjection;
import org.example.security.CustomUserDetailsService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class StockItemDictionariesService {


    private final StockItemDictionariesRepository stockItemDictionariesRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final StockItemDictionariesDTOMapper stockItemDictionariesDTOMapper;
    private final SemiProductService semiProductService;

    public StockItemDictionariesService(StockItemDictionariesRepository stockItemDictionariesRepository,
                                        CustomUserDetailsService customUserDetailsService,
                                        StockItemDictionariesDTOMapper stockItemDictionariesDTOMapper,
                                        SemiProductService semiProductService) {
        this.stockItemDictionariesRepository = stockItemDictionariesRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.stockItemDictionariesDTOMapper = stockItemDictionariesDTOMapper;
        this.semiProductService = semiProductService;
    }

    private StockItemDictionary getById(Long id) {
        return stockItemDictionariesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockItemDictionary with id " + id + " not found"));
    }

    @PreAuthorize("@securityService.isManager()")
    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    public StockItemDictionaryWithoutSemiProductDTO getStockItemDictionaryDTO(Long id) {
        StockItemDictionary entity = getById(id);
        return stockItemDictionariesDTOMapper.mapToWithoutSemiProductDTO(entity);
    }

    @PreAuthorize("@securityService.isManager()")
    public List<StockItemDictionaryDTO> getRestaurantStockItemDictionariesDTO() {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        List<StockItemDictionaryProjection> stockItemDictionariesByRestaurantId = stockItemDictionariesRepository.getStockItemDictionariesByRestaurantId(currentRestaurantId);

        return stockItemDictionariesDTOMapper.mapToStockItemDictionary(stockItemDictionariesByRestaurantId);
    }

    @PreAuthorize("@securityService.isManager()")
    public StockItemDictionaryWithoutSemiProductDTO createStockItemDictionary(CreateStockItemDictionaryDTO createDTO) {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();

        SemiProduct semiProduct = semiProductService.getById(createDTO.semiProductID());

        StockItemDictionary entity = new StockItemDictionary();
        entity.setName(createDTO.name());
        entity.setPrice(createDTO.price());
        entity.setUnit(createDTO.unit());
        entity.setMultipleOfSemiProduct(createDTO.multipleOfSemiProduct());
        entity.setRestaurantId(currentRestaurantId);
        entity.setSemiProduct(semiProduct);

        StockItemDictionary saved = stockItemDictionariesRepository.save(entity);

        return stockItemDictionariesDTOMapper.mapToWithoutSemiProductDTO(saved);
    }

    @PostAuthorize("@customUserDetailsService.checkAccessToResource(returnObject.restaurantId())")
    @PreAuthorize("@securityService.isManager()")
    public StockItemDictionaryWithoutSemiProductDTO patchStockItemDictionary(Long id, PatchStockItemDictionaryDTO patchDTO) {
        StockItemDictionary entity = getById(id);

        customUserDetailsService.checkAccessToResource(entity.getRestaurantId());

        if (patchDTO.name() != null) {
            entity.setName(patchDTO.name());
        }
        if (patchDTO.price() != null) {
            entity.setPrice(patchDTO.price());
        }
        if (patchDTO.unit() != null) {
            entity.setUnit(patchDTO.unit());
        }
        if (patchDTO.multipleOfSemiProduct() != null) {
            entity.setMultipleOfSemiProduct(patchDTO.multipleOfSemiProduct());
        }
        if (patchDTO.semiProductID() != null) {
            SemiProduct semiProduct = semiProductService.getById(patchDTO.semiProductID());
            entity.setSemiProduct(semiProduct);
        }

        StockItemDictionary saved = stockItemDictionariesRepository.save(entity);

        return stockItemDictionariesDTOMapper.mapToWithoutSemiProductDTO(saved);
    }

    @PreAuthorize("@securityService.isManager()")
    public StockItemDictionaryWithoutSemiProductDTO deleteStockItemDictionary(Long id) {
        StockItemDictionary entity = getById(id);
        customUserDetailsService.checkAccessToResource(entity.getRestaurantId());

        stockItemDictionariesRepository.delete(entity);
        return stockItemDictionariesDTOMapper.mapToWithoutSemiProductDTO(entity);
    }
}
