package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.product.CreateProductDto;
import org.example.dtos.product.PatchProductDTO;
import org.example.dtos.product.ProductDto;
import org.example.dtos.product.ProductDtoWithSemiProducts;
import org.example.dtos.mapers.ProductDtoMapper;
import org.example.models.Product;
import org.example.models.SemiProduct;
import org.example.repositories.ProductRepository;
import org.example.repositories.projections.ProductProjection;
import org.example.security.CustomUserDetailsService;
import org.example.security.SecurityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final SemiProductService semiProductService;
    private final CustomUserDetailsService customUserDetailsService;
    private final SecurityService securityService;


    public ProductService(ProductRepository productRepository, ProductDtoMapper productDtoMapper, SemiProductService semiProductService, CustomUserDetailsService customUserDetailsService, SecurityService securityService) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
        this.semiProductService = semiProductService;
        this.customUserDetailsService = customUserDetailsService;
        this.securityService = securityService;
    }

    @PreAuthorize("@securityService.isEmployee()and @securityService.hasAccess(#id , @productRepository)")
    public ProductDtoWithSemiProducts getProductWithSemiProductsById(Long id){
        Product byId = productRepository.findWithSemiProductsById(id).orElseThrow(EntityNotFoundException::new);

        return productDtoMapper.mapToProductDtoWithSemiProduct(byId);

    }

    @PreAuthorize("@securityService.isEmployee()and @securityService.hasAccess(#id , @productRepository)")
    public ProductDto getProductDtoById(Long id){
        Product byId = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return productDtoMapper.mapToProductDto(byId);
    }

    @PreAuthorize("@securityService.isEmployee()and @securityService.hasAccess(#id , @productRepository)")
    public Product getProductById(Long id){
        return productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @PreAuthorize("@securityService.isEmployee()")
    public List<ProductDto> getAllProducts() {
        Long restaurantId = customUserDetailsService.getCurrentRestaurantId();
        List<Product> allProducts = productRepository.findAllProductsByRestaurantId(restaurantId);
        return allProducts.stream()
                .map(productDtoMapper::mapToProductDto)
                .toList();
    }

    @PreAuthorize("@securityService.isManager()")
    public List<ProductDto> getProductsDTOByIds(List<Long> productIds) {
        Set<Long> ids = new HashSet<>(productIds);
        return findProductByIds(ids).stream()
                .map(productDtoMapper::mapToProductDTO)
                .toList();
    }

    @PreAuthorize("@securityService.isManager()")
    public List<Product> getProductsByIds(List<Long> productIds) {
        Set<Long> ids = new HashSet<>(productIds);
        return findProductByIds(ids).stream()
                .map(productDtoMapper::mapToProduct)
                .toList();

    }

    public Set<ProductProjection> findProductByIds(Set<Long> ids){
        Set<ProductProjection> products = productRepository.findProductByIds(ids);

        if (products.size() == ids.size()){
            return products;
        }

        Set<Long> foundIds = products.stream()
                .map(ProductProjection::getId)
                .collect(Collectors.toSet());

        Set<Long> missingIds = new HashSet<>(ids);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()){
            throw new EntityNotFoundException("Products with ID: "+missingIds+" Not found");
        }

        return products;
    }

    @PreAuthorize("@securityService.isManager()")
    public Map<Long, ProductProjection> getProductsMapByIds(List<Long> productIds) {
        Set<Long> ids = new HashSet<>(productIds);
        return findProductByIds(ids)
                .stream()
                .collect(Collectors.toMap(ProductProjection::getId, Function.identity()));
    }

    @PreAuthorize("@securityService.isManager()")
    public Map<Long, Product> getMapOfProductsByIds(List<Long> productIds) {
        Set<Long> ids = new HashSet<>(productIds);
        return findProductByIds(ids)
                .stream()
                .collect(Collectors.toMap(ProductProjection::getId, productDtoMapper::mapToProduct));
    }

    @PreAuthorize("@securityService.isEmployee()")
    public List<ProductDtoWithSemiProducts> getAllRestaurantProducts(){
        Long restaurantId = customUserDetailsService.getCurrentRestaurantId();
        List<Product> products = productRepository.findAllWithSemiProductsByRestaurantId(restaurantId);
        return products.stream()
                .filter(Objects::nonNull)
                .map(productDtoMapper::mapToProductDtoWithSemiProduct)
                .toList();
    }


    @PreAuthorize("@securityService.isManager()")
    @Transactional
    public ProductDto createProduct(CreateProductDto createProductDto) {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        Product product = productDtoMapper.mapToProduct(createProductDto, currentRestaurantId);

        for(var createProductSemiProductsDto : createProductDto.productsSemiProducts()){
            System.out.println("flaga");
            SemiProduct semiProduct = semiProductService.getById(createProductSemiProductsDto.semiProductId());
            product.addSemiProduct(semiProduct, createProductSemiProductsDto.quantity(), currentRestaurantId);
        }
        System.out.println("flaga");
        Product createdProduct = productRepository.save(product);
        return productDtoMapper.mapToProductDto(createdProduct);

    }

    @PreAuthorize("@securityService.isManager() and @securityService.hasAccess(#id , @productRepository)")
    public ProductDto deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepository.deleteById(id);
        return productDtoMapper.mapToProductDto(product);


    }

    @PreAuthorize("@securityService.isManager()")
    public List<String> getProductCategories() {
        Long restaurantId = customUserDetailsService.getCurrentRestaurantId();
        return productRepository.getProductCategories(restaurantId);
    }

    @PreAuthorize("@securityService.isManager()")
    @Transactional
    public ProductDto patchProduct(Long id, PatchProductDTO patchProductData) {
        Product productById = getProductById(id);

        if (patchProductData.name()!= null){
            productById.setName(patchProductData.name());
        }
        if (patchProductData.category()!= null){
            productById.setCategory(patchProductData.category());
        }
        if (patchProductData.price()!= null){
            productById.setPrice(patchProductData.price());
        }

        Product save = productRepository.save(productById);
        return productDtoMapper.mapToProductDto(save);

    }
}
