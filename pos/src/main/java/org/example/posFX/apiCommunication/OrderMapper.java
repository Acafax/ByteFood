package org.example.posFX.apiCommunication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.posFX.ItemType;
import org.example.posFX.ProductController;
import org.example.posFX.apiCommunication.order.ComboOrderRequest;
import org.example.posFX.apiCommunication.order.CreateOrderRequest;
import org.example.posFX.apiCommunication.order.ModificationRequest;
import org.example.posFX.apiCommunication.order.OrderProductRequest;
import org.example.posFX.objects.MenuItem;
import org.example.posFX.objects.MenuItemComboComponent;
import org.example.posFX.objects.ModificationTemplate;
import org.example.posFX.objects.OrderItem;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderMapper {

    private static final Duration DEFAULT_PREPARATION_TIME = Duration.ofMinutes(15);

    private final ObjectMapper objectMapper;

    public OrderMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toCreateOrderJson(List<OrderItem> orderItems) throws JsonProcessingException {
        CreateOrderRequest request = toCreateOrderRequest(orderItems);
        return objectMapper.writeValueAsString(request);
    }

    public CreateOrderRequest toCreateOrderRequest(List<OrderItem> orderItems) {
        List<OrderProductRequest> products = new ArrayList<>();
        List<ComboOrderRequest> combos = new ArrayList<>();

        for (OrderItem item : orderItems) {
            MenuItem menuItem = item.getMenuItem();
            if (menuItem.getType() == ItemType.COMBO) {
                combos.add(toComboRequest(item));
            } else {
                products.add(toProductRequest(item));
            }
        }

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.priceOfItemWithMods(item))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total must be greater than zero");
        }

        return new CreateOrderRequest(
                LocalDateTime.now(),
                DEFAULT_PREPARATION_TIME,
                totalPrice,
                products,
                combos
        );
    }

    private OrderProductRequest toProductRequest(OrderItem item) {
        MenuItem menuItem = item.getMenuItem();
        return new OrderProductRequest(
                item.getQuantity(),
                menuItem.getPrice(),
                null,
                menuItem.getId(),
                mapModifications(item.getModify())
        );
    }

    private ComboOrderRequest toComboRequest(OrderItem item) {
        MenuItem menuItem = item.getMenuItem();
        List<OrderProductRequest> comboProducts = menuItem.getComponents().stream()
                .map(this::toComboComponentRequest)
                .toList();

        return new ComboOrderRequest(
                menuItem.getName(),
                item.getQuantity(),
                menuItem.getPrice(),
                menuItem.getId(),
                comboProducts
        );
    }

    private OrderProductRequest toComboComponentRequest(MenuItemComboComponent component) {
        MenuItem product = component.menuItem();
        return new OrderProductRequest(
                component.quantity(),
                product.getPrice(),
                null,
                product.getId(),
                List.of()
        );
    }

    private List<ModificationRequest> mapModifications(LinkedHashMap<String, Integer> modify) {
        if (modify == null || modify.isEmpty()) {
            return List.of();
        }

        List<ModificationRequest> modifications = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : modify.entrySet()) {
            ModificationTemplate template = ProductController.possibleModificationMap.get(entry.getKey());
            if (template == null) {
                throw new IllegalArgumentException("Unknown modification: " + entry.getKey());
            }
            modifications.add(new ModificationRequest(
                    template.name(),
                    BigDecimal.valueOf(entry.getValue()),
                    template.price(),
                    template.id()
            ));
        }
        return modifications;
    }
}
