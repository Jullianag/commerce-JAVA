package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.dto.ProductDTO;
import com.meusprojetos.commerce.dto.ProductMinDTO;
import com.meusprojetos.commerce.entities.Product;
import com.meusprojetos.commerce.repositories.ProductRepository;
import com.meusprojetos.commerce.services.exceptions.DatabaseException;
import com.meusprojetos.commerce.services.exceptions.ResourceNotFoundException;
import com.meusprojetos.commerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Long existingProductId, nonExistingProductId, dependentProductId;
    private Product product;
    private PageImpl<Product> page;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingProductId = 1L;
        nonExistingProductId = 2L;
        dependentProductId = 3L;
        product = ProductFactory.createProduct();
        productDTO = new ProductDTO(product);

        page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.searchByName(any(), (Pageable) any())).thenReturn(page);

        Mockito.when(productRepository.save(any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productRepository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(dependentProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingProductId)).thenReturn(false);

        Mockito.doNothing().when(productRepository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentProductId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExistis() {

        ProductDTO result = productService.findById(existingProductId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), product.getName());
        Assertions.assertEquals(result.getPrice(), product.getPrice());
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingProductId);
        });
    }

    @Test
    public void findAllShouldReturnPageProductMinDTO() {

        Pageable pageable = PageRequest.of(0, 12);
        Page<ProductMinDTO> result = productService.findAll(product.getName(), pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getName(), product.getName());
        Assertions.assertEquals(result.iterator().next().getPrice(), product.getPrice());
        Assertions.assertEquals(result.iterator().next().getId(), product.getId());
    }

    @Test
    public void insertShouldReturnProductDTO() {

        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());
        Assertions.assertEquals(result.getName(), product.getName());
        Assertions.assertEquals(result.getPrice(), product.getPrice());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = productService.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), productDTO.getName());
    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingProductId, productDTO);
        });
    }

    @Test
    public void deleteShouldReturnDoNothingWhenIdExistis() {

        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingProductId);
        });
    }

    @Test
    public void deleteShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingProductId);
        });
    }

    @Test
    public void deleteShouldReturnDatabaseExceptionWhenDependentProductId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentProductId);
        });
    }
}
