package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.assemblers.OrderAssembler;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.order.CreateOrderDTORequest;
import org.example.dtos.order.OrderDto;
import org.example.dtos.order.OrderProductDTO;
import org.example.dtos.order.OrderProductRequestDTO;
import org.example.models.*;
import org.example.repositories.ComboRepository;
import org.example.repositories.ModificationTemplateRepository;
import org.example.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final ModificationTemplateRepository modificationTemplateRepository;
    private final ProductService productService;
    private final OrderDTOMapper orderDTOMapper;
    private final OrderAssembler orderAssembler;
    private final ComboRepository comboRepository;

    public OrderService(OrderRepository orderRepository, ModificationTemplateRepository modificationTemplateRepository, ProductService productService, OrderDTOMapper orderDTOMapper, OrderAssembler orderAssembler, ComboRepository comboRepository) {
        this.orderRepository = orderRepository;
        this.modificationTemplateRepository = modificationTemplateRepository;
        this.productService = productService;
        this.orderDTOMapper = orderDTOMapper;
        this.orderAssembler = orderAssembler;
        this.comboRepository = comboRepository;
    }



    @Transactional
    public OrderDto saveOrder(CreateOrderDTORequest createOrderDTORequest){
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

        Order save = orderRepository.save(order);

        OrderDto orderDto = orderAssembler.assembleOrder(save);

        log.info("DODAŁEM");
        return orderDto;




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


}
