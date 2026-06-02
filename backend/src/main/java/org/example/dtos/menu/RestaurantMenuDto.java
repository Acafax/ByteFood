package org.example.dtos.menu;

import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.product.ProductDto;

import java.util.List;

public record RestaurantMenuDto(List<MenuItemDto> menuItems, List<ModificationTemplateDto> modificationTemplates) {
}
