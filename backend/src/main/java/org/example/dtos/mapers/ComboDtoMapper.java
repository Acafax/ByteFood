package org.example.dtos.mapers;

import org.example.dtos.combo.ComboDetailsDTO;
import org.example.dtos.combo.ComboProductDTO;
import org.example.dtos.combo.CreateComboDTO;
import org.example.dtos.combo.DeletedComboDto;
import org.example.dtos.product.ProductDto;
import org.example.models.Combo;
import org.example.models.ComboProduct;
import org.springframework.stereotype.Component;


@Component
public class ComboDtoMapper {


    private final ProductDtoMapper productDtoMapper;

    public ComboDtoMapper(ProductDtoMapper productDtoMapper) {
        this.productDtoMapper = productDtoMapper;
    }

    public ComboDetailsDTO toComboDetailsDto(Combo combo) {
        return new ComboDetailsDTO(
                combo.getName(),
                combo.getPrice(),
                combo.getComboProducts().stream()
                        .map(this::mapToComboProductDto).toList()

        );
    }

    private ComboProductDTO mapToComboProductDto(ComboProduct comboProduct) {
        ProductDto productDto = productDtoMapper.mapToProductDto(comboProduct.getProduct());
        return new ComboProductDTO(comboProduct.getQuantity(), productDto);
    }

    public Combo toCombo(CreateComboDTO createComboDTO) {
        Combo combo = new Combo();
        combo.setName(createComboDTO.name());
        combo.setPrice(createComboDTO.price());
        combo.setName(createComboDTO.name());
        return combo;
    }

    public DeletedComboDto toDeletedComboDto(Combo combo) {
        return new DeletedComboDto(
                combo.getName(),
                combo.getPrice()
        );
    }
}
