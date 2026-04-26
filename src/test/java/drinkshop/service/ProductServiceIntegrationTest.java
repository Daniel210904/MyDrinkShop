package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceIntegrationTest {

    @Mock
    private Repository<Integer, Product> mockProductRepo;

    private Validator<Product> realValidator;
    private Repository<Integer, Product> realProductRepo;

    private Product testProduct;
    private final String TEST_FILE = "test_products_integration.txt";

    @BeforeEach
    void setUp() throws IOException {
        testProduct = new Product(1, "Test Cola", 5.5, CategorieBautura.JUICE, TipBautura.POWDER);
        realValidator = new ProductValidator();
        // Create an empty file to avoid FileNotFoundException in loadFromFile()
        Files.writeString(Path.of(TEST_FILE), "");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(TEST_FILE));
    }

    // ==============================================================================
    // STEP 2: Integram V (Testam S + V; pentru R folosim obiecte mock)
    // ==============================================================================
    @Test
    void step2_Integration_S_V_withMock_R_ValidProduct() {
        // Arrange
        ProductService serviceWithMockRepo = new ProductService(mockProductRepo, realValidator);
        when(mockProductRepo.save(testProduct)).thenReturn(null);

        // Act
        serviceWithMockRepo.addProduct(testProduct);

        // Assert
        verify(mockProductRepo, times(1)).save(testProduct); // Validator passed successfully
    }

    @Test
    void step2_Integration_S_V_withMock_R_InvalidProduct() {
        // Arrange
        ProductService serviceWithMockRepo = new ProductService(mockProductRepo, realValidator);
        Product invalidProduct = new Product(2, "", -5.0, CategorieBautura.JUICE, TipBautura.POWDER);

        // Act & Assert
        assertThrows(ValidationException.class, () -> serviceWithMockRepo.addProduct(invalidProduct));
        
        // Assert mock was never called because validation failed
        verify(mockProductRepo, never()).save(any(Product.class));
    }

    // ==============================================================================
    // STEP 3: Integram R (Testam S + V + R - folosim repository real)
    // ==============================================================================
    @Test
    void step3_Integration_S_V_R_AddAndFindProduct() {
        // Arrange
        realProductRepo = new FileProductRepository(TEST_FILE);
        ProductService fullService = new ProductService(realProductRepo, realValidator);

        // Act
        fullService.addProduct(testProduct);
        List<Product> products = fullService.getAllProducts();

        // Assert
        assertEquals(1, products.size());
        assertEquals("Test Cola", products.get(0).getNume());
    }

    @Test
    void step3_Integration_S_V_R_DeleteProduct() {
        // Arrange
        realProductRepo = new FileProductRepository(TEST_FILE);
        ProductService fullService = new ProductService(realProductRepo, realValidator);
        fullService.addProduct(testProduct);
        assertEquals(1, fullService.getAllProducts().size());

        // Act
        fullService.deleteProduct(testProduct.getId());

        // Assert
        assertEquals(0, fullService.getAllProducts().size());
    }
}
