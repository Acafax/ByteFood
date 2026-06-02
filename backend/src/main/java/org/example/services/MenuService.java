package org.example.services;

import org.example.dtos.combo.ComboProductDTO;
import org.example.dtos.menu.MenuItemDto;
import org.example.dtos.menu.RestaurantMenuDto;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.product.ProductDto;
import org.example.models.Combo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MenuService {


    private final ProductService productService;
    private final ComboService comboService;
    private final ModificationTemplateService modificationTemplateService;

    public MenuService(ProductService productService, ComboService comboService, ModificationTemplateService modificationTemplateService) {
        this.productService = productService;
        this.comboService = comboService;
        this.modificationTemplateService = modificationTemplateService;
    }

    @PreAuthorize("@securityService.isEmployee()")
    public RestaurantMenuDto getMenu(){
        List<ProductDto> allProducts = productService.getAllProducts();
        Map<Long, ProductDto> productDtoMap = allProducts.stream()
                .collect(Collectors.toMap(
                        ProductDto::id,
                        productDto -> productDto
                ));

        List<Combo> combosToMenu = comboService.getCombosToMenu();

        List<MenuItemDto> menuItemDtoProducts = listProductsToMenuItems(allProducts);
        List<MenuItemDto> menuItemDtoCombos = listCombosToMenuItems(combosToMenu, productDtoMap);
        List<MenuItemDto> allMenuItems =  List.copyOf(
                Stream.concat(menuItemDtoProducts.stream(), menuItemDtoCombos.stream())
                        .collect(Collectors.toList())
        );

        List<ModificationTemplateDto> modificationTemplates = modificationTemplateService.getRestaurantModificationTemplates();

        return new RestaurantMenuDto(allMenuItems, modificationTemplates);
    }


    private List<MenuItemDto> listProductsToMenuItems(List<ProductDto> products){
        return products.stream()
            .map(product -> new MenuItemDto(
                product.id(),
                product.name(),
                product.category(),
                product.price(),
                "PRODUCT",
                List.of()
            )).toList();
    }

    private List<MenuItemDto> listCombosToMenuItems(List<Combo> combos, Map<Long, ProductDto> productDtoMap ){
       return combos.stream()
            .map(combo -> {
                return new MenuItemDto(
                    combo.getId(),
                    combo.getName(),
                    "combo",
                    combo.getPrice(),
                    "COMBO",
                    combo.getComboProducts().stream()
                        .map(component -> {
                            ProductDto productDto = productDtoMap.get(component.getProduct().getId());
                            return new ComboProductDTO(
                                    component.getQuantity(),
                                    productDto
                            );
                        })
                        .collect(Collectors.toList())
                );
            }).toList();
    }


}
