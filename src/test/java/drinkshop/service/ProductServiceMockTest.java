package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//V <--- S ---> R

@ExtendWith(MockitoExtension.class)
public class ProductServiceMockTest {

    @Mock
    private Repository<Integer, Product> productRepo;

    @Mock
    private Validator<Product> validator;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Cola", 5.5, CategorieBautura.JUICE, TipBautura.POWDER);
    }

    @Test
    void addProduct_ShouldValidateAndSaveProduct() {
        // Arrange
        doNothing().when(validator).validate(testProduct);
        when(productRepo.save(testProduct)).thenReturn(null);

        // Act
        productService.addProduct(testProduct);

        // Assert (verify)
        verify(validator, times(1)).validate(testProduct);
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    void findById_ShouldReturnProduct() {
        // Arrange
        when(productRepo.findOne(1)).thenReturn(testProduct);
        
        // Act
        Product found = productService.findById(1);
        
        // Assert
        assertEquals(testProduct.getId(), found.getId());
        assertEquals("Test Cola", found.getNume());
        verify(productRepo, times(1)).findOne(1);
    }
}
