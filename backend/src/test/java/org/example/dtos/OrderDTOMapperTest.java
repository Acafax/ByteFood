package org.example.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.order.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@DisplayName("OrderDTO Mapper Test")
@ExtendWith(MockitoExtension.class)
public class OrderDTOMapperTest {

    @InjectMocks
    OrderDTOMapper orderDTOMapper = new OrderDTOMapper();

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    public void shouldReturnMap() throws IOException {
        File file = ResourceUtils.getFile("classpath:orderDto/createOrderResponse.json");
        OrderDto mockOrderDto = objectMapper.readValue(file, OrderDto.class);

        Map<Long, BigDecimal> result = orderDTOMapper.getSemiProductsFromOrderDto(mockOrderDto);

        assertNotNull(result);
        assertEquals(7, result.size());

        assertEquals(new BigDecimal("3.00"), result.get(1L));
        assertEquals(new BigDecimal("3.00"), result.get(6L));
        assertEquals(new BigDecimal("3.00"), result.get(14L));
        assertEquals(new BigDecimal("3.00"), result.get(15L));
        assertEquals(new BigDecimal("3.00"), result.get(17L));
        assertEquals(new BigDecimal("3.00"), result.get(26L));

        assertThat(result.get(10L)).isEqualByComparingTo(new BigDecimal("2"));
    }




}
