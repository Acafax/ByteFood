package org.example.dtos.mapers;

import org.example.dtos.combo.ComboDto;
import org.example.dtos.combo.ComboProductDTO;
import org.example.dtos.combo.OrderComboDTO;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.order.OrderDto;
import org.example.dtos.order.OrderItemModificationDTO;
import org.example.dtos.order.OrderProductDTO;
import org.example.dtos.product.ProductDto;
import org.example.models.*;
import org.example.repositories.projections.ComboProjection;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.example.repositories.projections.ProductProjection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class OrderDTOMapper {

    public OrderDto mapToOrderDto(Order order, Map<Long, ProductProjection> mapOfProducts, Map<Long, ComboProjection> mapOfCombos, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        return new OrderDto(
                order.getId(),
                order.getOrderTime(),
                order.getPreparationTime(),
                order.getPrice(),
                mapToOrderProductsDto(order, mapOfProducts, mapOfModifications),
                mapToOrderCombosDto(order.getCombos(), mapOfCombos, mapOfProducts, mapOfModifications));
    }



    private List<OrderProductDTO> mapToOrderProductsDto(Order order, Map<Long, ProductProjection> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return List.of();
        }

        return order.getOrderItems().stream()
                .filter(orderProduct -> orderProduct.getPartOfCombo() == null) // tylko produkty niezwiązane z combo
                .map(orderProduct -> mapToSingleOrderProductDTO(orderProduct, mapOfProducts, mapOfModifications))
                .filter(dto -> dto != null)
                .toList();
    }

    private OrderProductDTO mapToSingleOrderProductDTO(OrderProduct orderProduct, Map<Long, ProductProjection> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (orderProduct == null) {
            return null;
        }

        ProductDto productDto = null;
        if (orderProduct.getProduct() != null && orderProduct.getProduct().getId() != null) {
            ProductProjection productProjection = mapOfProducts.get(orderProduct.getProduct().getId());
            productDto = productProjectionMapper(productProjection);
        }

        return new OrderProductDTO(
                orderProduct.getQuantity(),
                orderProduct.getPrice(),
                orderProduct.getDescription(),
                productDto,
                mapToModificationsDto(mapOfModifications, orderProduct)
        );
    }

    private List<OrderComboDTO> mapToOrderCombosDto(Set<OrderCombo> combos, Map<Long, ComboProjection> mapOfCombos, Map<Long, ProductProjection> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (combos == null || combos.isEmpty()) {
            return List.of();
        }

        return combos.stream()
                .filter(Objects::nonNull)
                .map(orderCombo -> {
                    ComboDto comboDto = mapToComboDto(orderCombo, mapOfCombos, mapOfProducts, mapOfModifications);
                    return new OrderComboDTO(
                            orderCombo.getQuantity(),
                            orderCombo.getComboPrice(),
                            comboDto
                    );
                })
                .toList();
    }

    private ComboDto mapToComboDto(OrderCombo orderCombo, Map<Long, ComboProjection> mapOfCombos, Map<Long, ProductProjection> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (orderCombo == null || orderCombo.getCombo() == null) {
            return null;
        }

        Combo combo = orderCombo.getCombo();
        List<OrderProductDTO> comboProducts = mapComboProductsToOrderProductDTOs(orderCombo.getProductsInCombo(), mapOfProducts, mapOfModifications);

        return new ComboDto(
                combo.getName(),
                combo.getPrice(),
                comboProducts
        );
    }

    private List<OrderProductDTO> mapComboProductsToOrderProductDTOs(List<OrderProduct> productsInCombo, Map<Long, ProductProjection> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (productsInCombo == null || productsInCombo.isEmpty()) {
            return List.of();
        }

        return productsInCombo.stream()
                .filter(orderProduct -> orderProduct != null)
                .map(orderProduct -> mapToSingleOrderProductDTO(orderProduct, mapOfProducts, mapOfModifications))
                .filter(dto -> dto != null)
                .toList();
    }


    private List<OrderItemModificationDTO> mapToModificationsDto(Map<Long, ModificationTemplateProjection> mapOfModifications, OrderProduct orderProduct) {
        if (orderProduct == null || orderProduct.getModifications() == null || orderProduct.getModifications().isEmpty()) {
            return List.of();
        }

        return orderProduct.getModifications().stream()
                .filter(modification -> modification != null && modification.getModificationTemplate() != null && modification.getModificationTemplate().getId() != null)
                .map(modification -> {
                    ModificationTemplateProjection projection = mapOfModifications.get(modification.getModificationTemplate().getId());
                    if (projection == null) {
                        return null;
                    }
                    return new OrderItemModificationDTO(
                            modification.getQuantity(),
                            modification.getPrice(),
                            mapToModificationTemplateDto(projection)
                    );
                })
                .filter(dto -> dto != null)
                .toList();
    }

    private ProductDto productProjectionMapper(ProductProjection productProjection) {
        if (productProjection == null) {
            return null;
        }
        return new ProductDto(
                productProjection.getId(),
                productProjection.getName(),
                productProjection.getCategory(),
                productProjection.getPrice()
        );
    }


    private ModificationTemplateDto mapToModificationTemplateDto(ModificationTemplateProjection modificationTemplate) {
        if (modificationTemplate == null) {
            return null;
        }
        return new ModificationTemplateDto(
                modificationTemplate.getId(),
                modificationTemplate.getName(),
                modificationTemplate.getPrice(),
                modificationTemplate.getCategory(),
                modificationTemplate.getSemiProductId(),
                modificationTemplate.getRestaurantId()
        );
    }




}