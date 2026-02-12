package org.example.dtos.mapers;

import org.example.dtos.semiProducts.CreateSemiProductDto;
import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.models.SemiProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SemiProductsDtoMapper {

    public static SemiProduct mapToSemiProduct(CreateSemiProductDto createSemiProduct, Long restaurantId) {

        return new SemiProduct(
                createSemiProduct.name(),
                createSemiProduct.carbohydrate(),
                createSemiProduct.fat(),
                createSemiProduct.protein(),
                createSemiProduct.unit(),
                restaurantId
        );
    }

    public Set<SemiProductDTO> mapToSemiProductsDto(Set<SemiProduct> semiProducts) {
        return semiProducts.stream()
                .map(semiProduct -> new SemiProductDTO(
                        semiProduct.getId(),
                        semiProduct.getName(),
                        semiProduct.getCarbohydrate(),
                        semiProduct.getFat(),
                        semiProduct.getProtein(),
                        semiProduct.getUnit(),
                        semiProduct.getRestaurantId()
                ))
                .collect(Collectors.toSet());
    }

    public List<SemiProductDTO> mapToSemiProductsDtoList(List<SemiProduct> semiProducts) {
        return semiProducts.stream()
                .map(semiProduct -> new SemiProductDTO(
                        semiProduct.getId(),
                        semiProduct.getName(),
                        semiProduct.getCarbohydrate(),
                        semiProduct.getFat(),
                        semiProduct.getProtein(),
                        semiProduct.getUnit(),
                        semiProduct.getRestaurantId()
                ))
                .toList();
    }

    public SemiProductDTO mapToSemiProductDto(SemiProduct semiProduct) {
        return new SemiProductDTO(
                semiProduct.getId(),
                semiProduct.getName(),
                semiProduct.getCarbohydrate(),
                semiProduct.getFat(),
                semiProduct.getProtein(),
                semiProduct.getUnit(),
                semiProduct.getRestaurantId()
        );
    }
}
