package org.example.dtos.assemblers;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.models.*;
import org.example.repositories.ComboRepository;
import org.example.repositories.ModificationTemplateRepository;
import org.example.repositories.projections.ProductProjection;
import org.example.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.example.builders.OrderTestBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderAssemblerTest {

    @Mock ComboRepository comboRepository;
    @Mock ModificationTemplateRepository modificationTemplateRepository;
    @Mock ProductService productService;

    // ZMOCKOWANY MAPPER! To jest klucz do sukcesu.
    @Mock OrderDTOMapper orderDTOMapper;

    @InjectMocks
    OrderAssembler orderAssembler;

    @Test
    @DisplayName("Should assemble order DTO by fetching dependencies and passing to mapper")
    void shouldCreateOrderDto() {
        Order order = order()
                .withProduct(2L)
                .withCombo(10L, 12L, 18L)
                .build();

        when(productService.findProductByIds(Set.of(2L,12L, 18L))).thenReturn(Set.of());
        when(comboRepository.findCombosByIds(Set.of(10L))).thenReturn(Set.of());
        when(modificationTemplateRepository.findModificationTemplateByIds(any())).thenReturn(Set.of());

        // when
        orderAssembler.assembleOrderDto(order);

        //then
        verify(productService).findProductByIds(Set.of(2L,12L, 18L));
        verify(comboRepository).findCombosByIds(Set.of(10L));
    }

    @Test
    @DisplayName("Should correctly map modification template in combo product")
    void shouldCorrectlyMapProductModificationInCombo() {
        Order order = order()
                .withProductWithModification(2L,3L)
                .withComboWithModificationInFirstProduct(10L,1L  ,12L, 18L)
                .build();

        // when
        orderAssembler.assembleOrderDto(order);

        //then
        verify(productService).findProductByIds(Set.of(2L,12L,18L));
        verify(modificationTemplateRepository).findModificationTemplateByIds(Set.of(1L,3L));
    }

    @Test
    @DisplayName("Should correctly map database Projections to ID-based Maps")
    void shouldCorrectlyMapDatabaseProjections(){
        //given
        Order order = order().withProduct(99L).build();

        ProductProjection projection = mock(ProductProjection.class);
        lenient().when(projection.getId()).thenReturn(99L);

        when(productService.findProductByIds(Set.of(99L))).thenReturn(Set.of(projection));
        when(comboRepository.findCombosByIds(any())).thenReturn(Set.of());
        when(modificationTemplateRepository.findModificationTemplateByIds(any())).thenReturn(Set.of());

        var mapCaptor = ArgumentCaptor.forClass(Map.class);

        //when
        orderAssembler.assembleOrderDto(order);

        //then
        verify(orderDTOMapper).mapToOrderDto(eq(order),mapCaptor.capture(), any(), any());

        Map<Long,ProductProjection> value = mapCaptor.getValue();

        assertTrue(value.containsKey(99L));
        assertEquals(projection, value.get(99L));
    }



}
