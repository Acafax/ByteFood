package org.example.dtos.assemblers;

import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.product.ProductWithSemiProductIdDto;
import org.example.models.Order;
import org.example.repositories.ComboRepository;
import org.example.repositories.ModificationTemplateRepository;
import org.example.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.example.builders.OrderTestBuilder.order;
import static org.example.builders.ProductWithSemiProductIdDtoTestBuilder.forIds;
import static org.example.builders.ProductWithSemiProductIdDtoTestBuilder.productWithSemiIds;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OrderAssemblerTest {

    private static final Set<Long> COMBO_PRODUCT_IDS = Set.of(2L, 12L, 18L);

    @Mock ComboRepository comboRepository;
    @Mock ModificationTemplateRepository modificationTemplateRepository;
    @Mock ProductService productService;
    @Mock OrderDTOMapper orderDTOMapper;

    @InjectMocks
    OrderAssembler orderAssembler;

    @Captor
    ArgumentCaptor<Map<Long, ProductWithSemiProductIdDto>> productsMapCaptor;

    @BeforeEach
    void stubEmptyComboAndModificationLookups() {
        when(comboRepository.findCombosByIds(any())).thenReturn(Set.of());
        when(modificationTemplateRepository.findModificationTemplateByIds(any())).thenReturn(Set.of());
    }

    private void stubProductsByIds(Long... productIds) {
        Set<Long> ids = Set.copyOf(Arrays.asList(productIds));
        when(productService.findProductWithSemiProductsIdByIds(ids)).thenReturn(forIds(productIds));
    }

    @Test
    @DisplayName("Should assemble order DTO by fetching dependencies and passing to mapper")
    void shouldCreateOrderDto() {
        Order order = order()
                .withProduct(2L)
                .withCombo(10L, 12L, 18L)
                .build();

        stubProductsByIds(2L, 12L, 18L);

        orderAssembler.assembleOrderDto(order);

        verify(productService).findProductWithSemiProductsIdByIds(COMBO_PRODUCT_IDS);
        verify(comboRepository).findCombosByIds(Set.of(10L));
    }

    @Test
    @DisplayName("Should correctly map modification template in combo product")
    void shouldCorrectlyMapProductModificationInCombo() {
        Order order = order()
                .withProductWithModification(2L, 3L)
                .withComboWithModificationInFirstProduct(10L, 1L, 12L, 18L)
                .build();

        stubProductsByIds(2L, 12L, 18L);

        orderAssembler.assembleOrderDto(order);

        verify(productService).findProductWithSemiProductsIdByIds(COMBO_PRODUCT_IDS);
        verify(modificationTemplateRepository).findModificationTemplateByIds(Set.of(1L, 3L));
    }

    @Test
    @DisplayName("Should correctly map database Projections to ID-based Maps")
    void shouldCorrectlyMapDatabaseProjections() {
        Order order = order().withProduct(99L).build();
        ProductWithSemiProductIdDto expectedProduct = productWithSemiIds()
                .withId(99L)
                .withName("Product 99")
                .build();

        when(productService.findProductWithSemiProductsIdByIds(Set.of(99L)))
                .thenReturn(forIds(99L));

        orderAssembler.assembleOrderDto(order);

        verify(orderDTOMapper).mapToOrderDto(eq(order), productsMapCaptor.capture(), any(), any());

        Map<Long, ProductWithSemiProductIdDto> productsById = productsMapCaptor.getValue();

        assertTrue(productsById.containsKey(99L));
        assertEquals(expectedProduct, productsById.get(99L));
    }
}
