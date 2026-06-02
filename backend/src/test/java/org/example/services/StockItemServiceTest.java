package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.builders.StockItemTestBuilder;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.mapers.StockItemDTOMapper;
import org.example.dtos.order.OrderDto;
import org.example.models.StockItem;
import org.example.repositories.StockItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



@Slf4j
@DisplayName("Stock Item Service ")
@ExtendWith(MockitoExtension.class)
class StockItemServiceTest {

    @InjectMocks
    private OrderDTOMapper orderDTOMapper;

    @InjectMocks
    private StockItemDTOMapper stockItemDTOMapper;

    @Mock
    private StockItemRepository stockItemRepository;

    private StockItemService stockItemService;

    List<StockItem> mockListOfStockItems = new ArrayList<>();

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    @BeforeEach
    void setUp()  {
        orderDTOMapper = new OrderDTOMapper();
        stockItemDTOMapper = new StockItemDTOMapper();
        stockItemService = new StockItemService(
                stockItemRepository,
                orderDTOMapper,
                stockItemDTOMapper
        );
    }


    @Test
    public  void shouldCorrectlyAdjustStockAmount() throws IOException {
        mockListOfStockItems.addAll(List.of(
                new StockItemTestBuilder().withSemiProductId(1L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(6L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(10L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(14L).withQuantity(new BigDecimal("2.00")).withExpirationDate(LocalDateTime.now()).build(),
                new StockItemTestBuilder().withSemiProductId(14L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(15L).withQuantity(new BigDecimal("1.00")).withExpirationDate(LocalDateTime.now()).build(),
                new StockItemTestBuilder().withSemiProductId(15L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(17L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(26L).withQuantity(new BigDecimal("10.00")).build()
        ));

        File file = ResourceUtils.getFile("classpath:orderDto/createOrderResponse.json");
        OrderDto mockOrder = objectMapper.readValue(file, OrderDto.class);

        when(stockItemRepository.getStockItemsBySemiProductIds(Set.of(1L, 6L, 10L, 14L, 15L, 17L, 26L)))
                .thenReturn(mockListOfStockItems);

        stockItemService.adjustStockItemAmount(mockOrder);

        ArgumentCaptor<List<StockItem>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<StockItem>> updatedCaptor = ArgumentCaptor.forClass(List.class);

        verify(stockItemRepository).deleteAll(deleteCaptor.capture());
        verify(stockItemRepository).saveAll(updatedCaptor.capture());


        List<StockItem> deletedItems = deleteCaptor.getValue();
        List<StockItem> updatedItems = updatedCaptor.getValue();

        assertEquals(2,deletedItems.size());
        assertEquals(new BigDecimal("0"),deletedItems.get(0).getQuantity());
        assertEquals(new BigDecimal("0"),deletedItems.get(1).getQuantity());

        assertEquals(7,updatedItems.size());


        assertEquals(new BigDecimal("7.00"),updatedItems.get(0).getQuantity()); // semiId = 1
        assertEquals(new BigDecimal("7.00"),updatedItems.get(1).getQuantity()); // semiId = 17
        assertEquals(new BigDecimal("7.00"),updatedItems.get(2).getQuantity());// semiId = 6
        assertEquals(new BigDecimal("8.00"),updatedItems.get(3).getQuantity());// semiId = 10
        assertEquals(new BigDecimal("7.00"),updatedItems.get(4).getQuantity());// semiId = 26
        assertEquals(new BigDecimal("9.00"),updatedItems.get(5).getQuantity());// semiId = 14 I used everything from StockItem most expired stock item
        assertEquals(new BigDecimal("8.00"),updatedItems.get(6).getQuantity());// semiId = 15 I used everything from StockItem most expired stock item

        for (int i = 0; i < updatedItems.size(); i++) {
            StockItem item = updatedItems.get(i);
            log.info("Nr. {} | semiProductId: {} | ILOŚĆ: {} | expDate: {}",
                    i, item.getSemiProduct().getId(), item.getQuantity(), item.getExpirationDate());
        }

        log.info("--- USUNIĘTE ---");
        for (int i = 0; i < deletedItems.size(); i++) {
            StockItem item = deletedItems.get(i);
            log.info("Nr. {} | semiProductId: {} | ILOŚĆ: {} | expDate: {}",
                    i, item.getSemiProduct().getId(), item.getQuantity(), item.getExpirationDate());
        }
    }


    @Test
    public  void shouldCorrectlyAdjustStockAmountWithNullStockItem() throws IOException {
        mockListOfStockItems.addAll(List.of(
                new StockItemTestBuilder().withSemiProductId(1L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(6L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(15L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(17L).withQuantity(new BigDecimal("10.00")).build(),
                new StockItemTestBuilder().withSemiProductId(26L).withQuantity(new BigDecimal("10.00")).build()
        ));

        File file = ResourceUtils.getFile("classpath:orderDto/createOrderResponse.json");
        OrderDto mockOrder = objectMapper.readValue(file, OrderDto.class);

        when(stockItemRepository.getStockItemsBySemiProductIds(Set.of(1L, 6L, 10L, 14L, 15L, 17L, 26L)))
                .thenReturn(mockListOfStockItems);

        stockItemService.adjustStockItemAmount(mockOrder);

        ArgumentCaptor<List<StockItem>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<StockItem>> updatedCaptor = ArgumentCaptor.forClass(List.class);

        verify(stockItemRepository).deleteAll(deleteCaptor.capture());
        verify(stockItemRepository).saveAll(updatedCaptor.capture());


        List<StockItem> deletedItems = deleteCaptor.getValue();
        List<StockItem> updatedItems = updatedCaptor.getValue();

        assertEquals(0,deletedItems.size());

        assertEquals(5,updatedItems.size());


        assertEquals(new BigDecimal("7.00"),updatedItems.get(0).getQuantity()); // semiId = 1
        assertEquals(new BigDecimal("7.00"),updatedItems.get(1).getQuantity()); // semiId = 17
        assertEquals(new BigDecimal("7.00"),updatedItems.get(2).getQuantity());// semiId = 6
        assertEquals(new BigDecimal("7.00"),updatedItems.get(3).getQuantity());// semiId = 26
        assertEquals(new BigDecimal("7.00"),updatedItems.get(4).getQuantity());// semiId = 15

        for (int i = 0; i < updatedItems.size(); i++) {
            StockItem item = updatedItems.get(i);
            log.info("Nr. {} | semiProductId: {} | ILOŚĆ: {} | expDate: {}",
                    i, item.getSemiProduct().getId(), item.getQuantity(), item.getExpirationDate());
        }

        log.info("--- USUNIĘTE ---");
        for (int i = 0; i < deletedItems.size(); i++) {
            StockItem item = deletedItems.get(i);
            log.info("Nr. {} | semiProductId: {} | ILOŚĆ: {} | expDate: {}",
                    i, item.getSemiProduct().getId(), item.getQuantity(), item.getExpirationDate());
        }
    }

}
