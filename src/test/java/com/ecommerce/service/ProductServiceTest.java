package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Spy  private ModelMapper modelMapper;
    @InjectMocks private ProductService productService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder().id(1L).name("Electronics").build();
        testProduct = Product.builder()
                .id(1L).name("Test Phone").description("A great phone")
                .price(new BigDecimal("9999.00")).stockQuantity(50)
                .active(true).category(testCategory).build();
    }

    @Test
    @DisplayName("Should return product DTO when valid ID is provided")
    void shouldGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductDTO result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("Test Phone", result.getName());
        assertEquals(new BigDecimal("9999.00"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid product ID")
    void shouldThrowExceptionForInvalidId() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(999L));
    }

    @Test
    @DisplayName("Should create product when valid request is provided")
    void shouldCreateProduct() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Product");
        request.setDescription("Description");
        request.setPrice(new BigDecimal("1999.00"));
        request.setStockQuantity(100);
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        ProductDTO result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should soft delete product by setting active to false")
    void shouldSoftDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(1L);

        assertFalse(testProduct.getActive());
        verify(productRepository).save(testProduct);
    }
}
