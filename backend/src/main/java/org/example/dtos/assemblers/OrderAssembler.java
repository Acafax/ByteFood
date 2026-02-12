package org.example.dtos.assemblers;


import org.example.models.OrderCombo;
import org.example.services.ProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.order.OrderDto;
import org.example.models.Order;
import org.example.models.OrderItemModification;
import org.example.repositories.ComboRepository;
import org.example.repositories.ModificationTemplateRepository;
import org.example.repositories.ProductRepository;
import org.example.repositories.projections.ComboProjection;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.example.repositories.projections.ProductProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderAssembler {

    private final ModificationTemplateRepository modificationTemplateRepository;
    private final ComboRepository comboRepository;
    private final ProductRepository productRepository;
    private final OrderDTOMapper orderDTOMapper;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public OrderDto assembleOrder(Order order) {
        Set<Long> productIds = getProductsId(order);
        Set<Long> combosIds = getComboId(order);
        Set<Long> modificationsTemplateIds = getModificationId(order);

        Map<Long, ProductProjection> MapOfProducts = productService.findProductByIds(productIds)
                .stream()
                .collect(Collectors.toMap(ProductProjection::getId, Function.identity()));

        Map<Long, ComboProjection> MapOfCombos= comboRepository.findCombosByIds(combosIds)
                .stream()
                .collect(Collectors.toMap(ComboProjection::getId, Function.identity()));

        Map<Long, ModificationTemplateProjection> MapOfModifications = modificationTemplateRepository.findModificationTemplateByIds(modificationsTemplateIds)
                .stream()
                .collect(Collectors.toMap(ModificationTemplateProjection::getId, Function.identity()));

        return orderDTOMapper.mapToOrderDto(order, MapOfProducts, MapOfCombos, MapOfModifications);

    }

    private Set<Long> getProductsId(Order order) {

        Set<Long> looseProductsIds = order.getOrderItems()
                .stream()
                .map(orderProduct -> orderProduct.getProduct().getId())
                .collect(Collectors.toSet());

        Set<Long> comboProductsIds = order.getCombos().stream()
                .flatMap(combo -> combo.getProductsInCombo().stream())
                .map(orderProduct -> orderProduct.getProduct().getId())
                .collect(Collectors.toSet());

        looseProductsIds.addAll(comboProductsIds);
        return looseProductsIds;
    }


    private Set<Long> getComboId(Order order) {
        if (order.getCombos() == null) return Set.of();
        return order.getCombos().stream()
                .map(OrderCombo::getId).collect(Collectors.toSet());
    }
    private Set<Long> getModificationId(Order order) {
        return order.getOrderItems().stream()
                .flatMap(orderProduct -> orderProduct.getModifications().stream())
                .map(orderItemModification -> orderItemModification.getModificationTemplate().getId())
                .collect(Collectors.toSet());
    }



}
