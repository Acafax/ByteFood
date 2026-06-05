package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.semiProducts.CreateProductSemiProductsDto;
import org.example.dtos.mapers.ProductDtoMapper;
import org.example.dtos.mapers.SemiProductsDtoMapper;
import org.example.dtos.product.CreateProductDto;
import org.example.dtos.product.ProductDto;
import org.example.models.Product;
import org.example.models.SemiProduct;
import org.example.models.UnitType;
import org.example.repositories.ProductRepository;
import org.example.repositories.projections.ProductProjection;
import org.example.security.CustomUserDetailsService;
import org.example.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProductService Unit Tests")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SemiProductService semiProductService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private SecurityService securityService;

    private SemiProductsDtoMapper semiProductsDtoMapper;

    private ProductDtoMapper productDtoMapper;

    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @BeforeEach
    void setUp() {
        semiProductsDtoMapper = new SemiProductsDtoMapper();
        productDtoMapper = new ProductDtoMapper(semiProductsDtoMapper);
        productService = new ProductService(
                productRepository,
                productDtoMapper,
                semiProductService,
                customUserDetailsService,
                securityService
        );
    }


    private ProductProjection createMockProductProjection(Long id, String name, BigDecimal price, String category){
        ProductProjection projection = mock(ProductProjection.class);
        lenient().when(projection.getId()).thenReturn(id);
        lenient().when(projection.getName()).thenReturn(name);
        lenient().when(projection.getPrice()).thenReturn(price);
        lenient().when(projection.getCategory()).thenReturn(category);
        return projection;
    }

    @Nested
    @DisplayName("Tests for getProductDtoById method")
    class GetProductDtoByIdTest {

        private Product testProduct;
        private final Long PRODUCT_ID = 1L;

        @BeforeEach
        void setUp() {
            testProduct = new Product(
                    PRODUCT_ID,
                    "Test Burger",
                    "BURGER",
                    new BigDecimal("25.99"),
                    1L
            );
        }

        @Test
        @DisplayName("Should return ProductDto when product exists")
        void shouldReturnProductDto_WhenProductExists() {
            // given
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));

            // when
            ProductDto result = productService.getProductDtoById(PRODUCT_ID);

            // then
            assertNotNull(result);
            assertEquals(PRODUCT_ID, result.id());
            assertEquals("Test Burger", result.name());
            assertEquals("BURGER", result.category());
            assertEquals(new BigDecimal("25.99"), result.price());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product does not exist")
        void shouldThrowEntityNotFoundException_WhenProductNotFound() {
            // given
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () -> productService.getProductDtoById(PRODUCT_ID));
        }

        @Test
        @DisplayName("Should call repository findById exactly once")
        void shouldCallRepositoryFindByIdOnce() {
            // given
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));

            // when
            productService.getProductDtoById(PRODUCT_ID);

            // then
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }

        @Test
        @DisplayName("Should not call repository more than once when product does not exist")
        void shouldNotCallRepositoryMoreThanOnce_WhenProductNotFound() {
            // given
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () -> productService.getProductDtoById(PRODUCT_ID));
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }

        @Test
        @DisplayName("Should return correct ProductDto with all fields mapped")
        void shouldReturnCorrectProductDto_WithAllFieldsMapped() {
            // given
            Product productWithDetails = new Product(
                    99L,
                    "Premium Burger",
                    "PREMIUM",
                    new BigDecimal("49.99"),
                    1L
            );

            when(productRepository.findById(99L)).thenReturn(Optional.of(productWithDetails));

            // when
            ProductDto result = productService.getProductDtoById(99L);

            // then
            assertAll(
                    () -> assertEquals(99L, result.id()),
                    () -> assertEquals("Premium Burger", result.name()),
                    () -> assertEquals("PREMIUM", result.category()),
                    () -> assertEquals(new BigDecimal("49.99"), result.price())
            );
        }

        @Test
        @DisplayName("Should correctly map product with null category")
        void shouldCorrectlyMapProduct_WithNullCategory() {
            // given
            Product productWithNullCategory = new Product(
                    2L,
                    "Special Item",
                    null,
                    new BigDecimal("15.00"),
                    1L
            );

            when(productRepository.findById(2L)).thenReturn(Optional.of(productWithNullCategory));

            // when
            ProductDto result = productService.getProductDtoById(2L);

            // then
            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals("Special Item", result.name());
            assertNull(result.category());
            assertEquals(new BigDecimal("15.00"), result.price());
        }
    }

    @Nested
    @DisplayName("findProductByIds Test")
    class  FindProductByIdsTest{

        @Test
        @DisplayName("HappyPatch should correctly return set with Projections")
        public void shouldCorrectlyReturnSetWithProjections(){
            // given
            Set<ProductProjection> mockProjections = Set.of(
                    createMockProductProjection(1L, "Junior Classic Burger 120g", new BigDecimal("29.00"), "BURGER"),
                    createMockProductProjection(2L, "Classic Burger 180g", new BigDecimal("34.00"), "BURGER"),
                    createMockProductProjection(3L, "Goliath Burger 360g", new BigDecimal("49.00"), "BURGER"),
                    createMockProductProjection(4L, "Spicy Jalapeño Burger 180g", new BigDecimal("37.00"), "BURGER")
            );

            when(productRepository.findProductsByIds(Set.of(1L,2L,3L,4L))).thenReturn(mockProjections);

            //when
            Set<ProductProjection> result = productService.findProductByIds(Set.of(1L, 2L, 3L, 4L));

            //then
            assertEquals(result, mockProjections);
        }

        @Test
        @DisplayName("UnhappyPatch missing product with id 3 ")
        public void shouldReturnMissingProductId(){
            //given
            Set<ProductProjection> mockProjections = Set.of(
                    createMockProductProjection(1L, "Junior Classic Burger 120g", new BigDecimal("29.00"), "BURGER"),
                    createMockProductProjection(2L, "Classic Burger 180g", new BigDecimal("34.00"), "BURGER")
            );

            Set<Long> ids = Set.of(1L, 2L, 3L);
            when(productRepository.findProductsByIds(ids)).thenReturn(mockProjections);

            //when & then
            assertThatThrownBy(() -> productService.findProductByIds(ids))
                    .isInstanceOf(EntityNotFoundException.class)
                            .hasMessageContaining("3")
                            .hasMessageContaining("Not found");
        }

        @Test
        @DisplayName("UnhappyPatch missing product with id 1,2,3 ")
        public void shouldReturnMissingAllProductId(){
            //given
            Set<ProductProjection> mockProjections = Set.of();

            Set<Long> ids = Set.of(1L, 2L, 3L);
            when(productRepository.findProductsByIds(ids)).thenReturn(mockProjections);

            //when & then
            assertThatThrownBy(() -> productService.findProductByIds(ids))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("1")
                    .hasMessageContaining("2")
                    .hasMessageContaining("3")
                    .hasMessageContaining("Not found");
        }


        @Test
        @DisplayName("ShouldReturnEmptySetAndDontUseRepositoryClass")
        public void ShouldReturnEmptySetAndDontUseRepositoryClass(){
            Set<Long> ids = Set.of();

            //when & then
            Set<ProductProjection> result = productService.findProductByIds(ids);

            assertNotNull(result);
            assertEquals(0,result.size());
            verifyNoInteractions(productRepository);
        }

        @Test
        @DisplayName("ShouldReturnIllegalArgumentException")
        public void ShouldReturnIllegalArgumentException(){
            Set<Long> ids = null;

            //when & then
            assertThatThrownBy(() ->productService.findProductByIds(ids))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ids cannot be null");

        }


    }

    @Nested
    @DisplayName("Create Product Test")
    class CreateProductTest{
        private CreateProductDto mockCreateProductDto;
        private final long savedProductId = 1L;
        private SemiProduct mockSemiProduct;
        private Product mappedProduct;
        private ProductDto savedProductDto;
        private Product savedProduct;

        @BeforeEach
        void setup(){
            mockSemiProduct = new SemiProduct(1L, "Bułka brioche", new BigDecimal("8.5"), new BigDecimal("9.0"),new BigDecimal("50.0") , UnitType.PCS, new BigDecimal(7000));
             mockCreateProductDto = new CreateProductDto(
                     "New Product",
                    "BURGER",
                    new BigDecimal("100"),
                    Set.of(new CreateProductSemiProductsDto(1L, BigDecimal.ONE)));

            mappedProduct = new Product();
            mappedProduct.setName("New Product");
            mappedProduct.setCategory("BURGER");
            mappedProduct.setPrice(new BigDecimal("100"));
            mappedProduct.setSemiProducts(new HashSet<>());

            savedProductDto = new ProductDto(savedProductId, "New Product", "BURGER", new BigDecimal("100"), 1L);
            savedProduct = new Product(savedProductId, "New Product", "BURGER", new BigDecimal("100"), 1L);
        }

        @Test
        public void shouldCreateProduct(){
            //given
            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

            when(customUserDetailsService.getCurrentRestaurantId()).thenReturn(1L);
            when(semiProductService.getById(1L)).thenReturn(mockSemiProduct);
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            //then
            ProductDto result = productService.createProduct(mockCreateProductDto);

            //when
            verify(productRepository).save(productCaptor.capture());
            Product captorProduct = productCaptor.getValue();

            assertNotNull(result);
            assertEquals("New Product", result.name());
            assertEquals(1,captorProduct.getSemiProducts().size());
        }

    }


}

