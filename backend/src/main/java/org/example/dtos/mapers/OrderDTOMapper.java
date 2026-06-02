package org.example.dtos.mapers;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.combo.ComboDto;
import org.example.dtos.combo.OrderComboDTO;
import org.example.dtos.modification.ModificationTemplateDto;
import org.example.dtos.order.OrderDto;
import org.example.dtos.order.OrderItemModificationDTO;
import org.example.dtos.order.OrderProductDTO;
import org.example.dtos.product.ProductWithSemiProductIdDto;
import org.example.models.*;
import org.example.repositories.projections.ComboProjection;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderDTOMapper {

    public OrderDto mapToOrderDto(Order order, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ComboProjection> mapOfCombos, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        return new OrderDto(
                order.getId(),
                order.getOrderTime(),
                order.getPreparationTime(),
                order.getPrice(),
                mapToOrderProductsDto(order, mapOfProducts, mapOfModifications),
                mapToOrderCombosDto(order.getCombos(), mapOfCombos, mapOfProducts, mapOfModifications));
    }

    public Map<Long, BigDecimal> getSemiProductsFromOrderDto(OrderDto orderDto){
        List<OrderProductDTO> orderProductDTOsFromCombos = orderDto.combos().stream()
                .flatMap(orderComboDTO -> orderComboDTO.combo().comboProduct().stream())
                .toList();

        List<OrderProductDTO> productDTOList = new ArrayList<>(orderDto.products());
        productDTOList.addAll(orderProductDTOsFromCombos);


        Map<Long, BigDecimal> modificationsMap = semiProductsIdWithQuantityFromModifications(productDTOList);
        Map<Long, BigDecimal> semiProductIdWithQuantity = getSemiProductIdWithQuantity(productDTOList);

        modificationsMap.forEach((key, quantity) -> semiProductIdWithQuantity.merge(key, quantity, BigDecimal::add));

        return semiProductIdWithQuantity;
    }


    public Map<Long, BigDecimal> getSemiProductIdWithQuantity(List<OrderProductDTO> products){
        return products.stream()
                .flatMap(orderProduct -> orderProduct.product().productSemiProductIdQuantity()
                    .entrySet().stream()
                    .map(entry -> Map.entry(
                            entry.getKey(),
                            entry.getValue().multiply(BigDecimal.valueOf(orderProduct.quantity())))))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    BigDecimal::add
                ));
    }

    /**
     * Quantity is defined without multiplication - return {@code 2} instead of {@code 2.00}
     * as opposed to {@link #getSemiProductIdWithQuantity(List)}
     * */
    public Map<Long, BigDecimal> semiProductsIdWithQuantityFromModifications(List<OrderProductDTO> products){
        return products.stream()
                .flatMap(orderProductDTO -> orderProductDTO.modifications().stream())
                .collect(Collectors.toMap(
                        OrderItemModificationDTO::getSemiProductId,
                        OrderItemModificationDTO::quantity,
                        BigDecimal::add
                ));
    }



    private List<OrderProductDTO> mapToOrderProductsDto(Order order, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return List.of();
        }

        return order.getOrderItems().stream()
                .filter(orderProduct -> orderProduct.getPartOfCombo() == null) // tylko produkty niezwiązane z combo
                .map(orderProduct -> mapToSingleOrderProductDTO(orderProduct, mapOfProducts, mapOfModifications))
                .filter(dto -> dto != null)
                .toList();
    }

    private OrderProductDTO mapToSingleOrderProductDTO(OrderProduct orderProduct, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
        if (orderProduct == null) {
            return null;
        }

        ProductWithSemiProductIdDto productDto = null;
        if (orderProduct.getProduct() != null && orderProduct.getProduct().getId() != null) {
            productDto = mapOfProducts.get(orderProduct.getProduct().getId());
        }

        return new OrderProductDTO(
                orderProduct.getQuantity(),
                orderProduct.getPrice(),
                orderProduct.getDescription(),
                productDto,
                mapToModificationsDto(mapOfModifications, orderProduct)
        );
    }

    private List<OrderComboDTO> mapToOrderCombosDto(Set<OrderCombo> combos, Map<Long, ComboProjection> mapOfCombos, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
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

    private ComboDto mapToComboDto(OrderCombo orderCombo, Map<Long, ComboProjection> mapOfCombos, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
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

    private List<OrderProductDTO> mapComboProductsToOrderProductDTOs(List<OrderProduct> productsInCombo, Map<Long, ProductWithSemiProductIdDto> mapOfProducts, Map<Long, ModificationTemplateProjection> mapOfModifications) {
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



    private ModificationTemplateDto mapToModificationTemplateDto(ModificationTemplateProjection modificationTemplate) {
        if (modificationTemplate == null) {
            return null;
        }
        return new ModificationTemplateDto(
                modificationTemplate.getId(),
                modificationTemplate.getName(),
                modificationTemplate.getPrice(),
                modificationTemplate.getSubcategory(),
                modificationTemplate.getSemiProductId(),
                modificationTemplate.getRestaurantId()
        );
    }




}