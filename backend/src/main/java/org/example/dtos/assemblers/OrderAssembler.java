package org.example.dtos.assemblers;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.order.CreateOrderDTORequest;
import org.example.dtos.order.OrderDto;
import org.example.dtos.order.OrderProductRequestDTO;
import org.example.dtos.product.ProductWithSemiProductIdDto;
import org.example.models.*;
import org.example.repositories.ComboRepository;
import org.example.repositories.ModificationTemplateRepository;
import org.example.repositories.projections.ComboProjection;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.example.repositories.projections.ProductProjection;
import org.example.services.ProductService;
import org.example.util.utilityClass.OrderHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAssembler {

    private final ModificationTemplateRepository modificationTemplateRepository;
    private final ComboRepository comboRepository;
    private final OrderDTOMapper orderDTOMapper;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public OrderDto assembleOrderDto(Order order) {
        Set<Long> productIds = getProductsId(order);
        Set<Long> combosIds = getComboId(order);
        Set<Long> modificationsTemplateIds = getModificationId(order);

        Map<Long, ProductWithSemiProductIdDto> MapOfProducts = productService.findProductWithSemiProductsIdByIds(productIds)
                .stream()
                .collect(Collectors.toMap(ProductWithSemiProductIdDto::id, Function.identity()));

        Map<Long, ComboProjection> MapOfCombos= comboRepository.findCombosByIds(combosIds)
                .stream()
                .collect(Collectors.toMap(ComboProjection::getId, Function.identity()));

        Map<Long, ModificationTemplateProjection> MapOfModifications = modificationTemplateRepository.findModificationTemplateByIds(modificationsTemplateIds)
                .stream()
                .collect(Collectors.toMap(ModificationTemplateProjection::getId, Function.identity()));

        return orderDTOMapper.mapToOrderDto(order, MapOfProducts, MapOfCombos, MapOfModifications);

    }

    @Transactional
    public Order assembleOrder(CreateOrderDTORequest createOrderDTORequest){
        Order order = new Order();
        order.setOrderTime(createOrderDTORequest.orderTime());
        order.setPreparationTime(createOrderDTORequest.preparationTime());
        order.setPrice(createOrderDTORequest.price());

        order.setOrderItems(new HashSet<>());
        order.setCombos(new HashSet<>());

        if (createOrderDTORequest.products() != null){
            Set<OrderProduct> orderProducts = new HashSet<>();
            createOrderDTORequest.products().stream().forEach(orderProductDTO -> {
                OrderProduct orderProduct = mapToOrderProductInCombo(orderProductDTO, order);
                orderProducts.add(orderProduct);
            } );
            order.setOrderItems(orderProducts);
        }

        if (createOrderDTORequest.combos() != null){
            Set<OrderCombo> orderCombos = new HashSet<>();
            createOrderDTORequest.combos().stream().forEach(comboDTO -> {
                OrderCombo orderCombo = new OrderCombo();
                orderCombo.setQuantity(comboDTO.quantity());
                orderCombo.setComboPrice(comboDTO.price());

                Combo combo = comboRepository.findById(comboDTO.comboId()).orElseThrow(EntityNotFoundException::new);
                orderCombo.setCombo(combo);

                List<OrderProduct> orderProducts = new ArrayList<>();
                if (comboDTO.comboProducts() != null){
                    comboDTO.comboProducts()
                            .forEach(orderProductDTO -> {
                                OrderProduct orderProduct = mapToOrderProductInCombo(orderProductDTO, order, orderCombo);
                                orderProducts.add(orderProduct);
                            });
                    orderCombo.setProductsInCombo(orderProducts);
                    orderCombos.add(orderCombo);
                }
            });
            order.setCombos(orderCombos);
        }
        return order;
    }

    private OrderProduct mapToOrderProductInCombo(OrderProductRequestDTO orderProductDTO, Order order){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setPrice(orderProductDTO.price());
        orderProduct.setQuantity(orderProductDTO.quantity());
        orderProduct.setDescription(orderProductDTO.description());

        List<OrderItemModification> modificationTemplates = new ArrayList<>();
        orderProductDTO.modifications().forEach(modification -> {
            OrderItemModification orderItemModification =  new OrderItemModification();
            orderItemModification.setPrice(modification.price());
            orderItemModification.setQuantity(modification.quantity());
            orderItemModification.setOrderProduct(orderProduct);
            ModificationTemplate modificationTemplate = modificationTemplateRepository.findById(modification.modificationTemplateId()).orElseThrow(EntityNotFoundException::new);
            orderItemModification.setModificationTemplate(modificationTemplate);

            modificationTemplates.add(orderItemModification);
        });

        orderProduct.setModifications(modificationTemplates);
        orderProduct.setOrder(order);
        orderProduct.setPartOfCombo(null);
        orderProduct.setProduct(productService.getProductById(orderProductDTO.productId()));
        return orderProduct;
    }

    private OrderProduct mapToOrderProductInCombo(OrderProductRequestDTO orderProductDTO, Order order, OrderCombo orderCombo){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setPrice(orderProductDTO.price());
        orderProduct.setQuantity(orderProductDTO.quantity());
        orderProduct.setDescription(orderProductDTO.description());
        orderProduct.setOrder(order);
        orderProduct.setPartOfCombo(orderCombo);

        if (orderProductDTO.productId() != null) {
            orderProduct.setProduct(productService.getProductById(orderProductDTO.productId()));
        }

        List<OrderItemModification> modificationTemplates = new ArrayList<>();
        if (orderProductDTO.modifications() != null){
            orderProductDTO.modifications().forEach(modification -> {
                OrderItemModification orderItemModification =  new OrderItemModification();
                orderItemModification.setPrice(modification.price());
                orderItemModification.setQuantity(modification.quantity());
                orderItemModification.setOrderProduct(orderProduct);
                orderItemModification.setOrderCombo(orderCombo);
                ModificationTemplate modificationTemplate = modificationTemplateRepository.findById(modification.modificationTemplateId()).orElseThrow(EntityNotFoundException::new);
                orderItemModification.setModificationTemplate(modificationTemplate);

                modificationTemplates.add(orderItemModification);
            });
        }


        orderProduct.setModifications(modificationTemplates);

        return orderProduct;
    }



    private Set<Long> getProductsId(Order order) {

        Set<Long> looseProductsIds = order.getOrderItems()
                .stream()
                .map(OrderHelper::getProductId)
                .collect(Collectors.toSet());

        Set<Long> comboProductsIds = order.getCombos().stream()
                .flatMap(OrderHelper::getProductsInCombo)
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
        Set<Long> modificationInOrderItems = order.getOrderItems().stream()
                .flatMap(OrderHelper::getModificationTemplateIds)
                .collect(Collectors.toSet());

        Set<Long> modificationsInOrderCombos = order.getCombos().stream()
                .flatMap(orderCombo -> orderCombo.getProductsInCombo().stream())
                .flatMap(OrderHelper::getModificationTemplateIds)
                .collect(Collectors.toSet());

        modificationInOrderItems.addAll(modificationsInOrderCombos);
        return modificationInOrderItems;


    }



}
