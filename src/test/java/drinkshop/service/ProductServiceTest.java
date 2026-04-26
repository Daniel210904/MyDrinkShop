package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {

    private ProductService service;
    private AbstractRepository<Integer, Product> repo;

    @BeforeEach
    void setUp() {
        repo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        service = new ProductService(repo, new ProductValidator());
    }

    @AfterEach
    void tearDown() {
        repo = null;
        service = null;
    }

    // ==================== ECP Tests ====================

    @Test
    @DisplayName("ECP_valid: add product with valid name and valid price")
    @Tag("ECP")
    @Order(1)
    void testAddProduct_ECP_ValidNameAndPrice() {
        // Arrange
        Product p = new Product(1, "Espresso", 10.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        service.addProduct(p);

        // Assert
        assertEquals(1, service.getAllProducts().size());
        assertEquals("Espresso", service.findById(1).getNume());
    }

    @Test
    @DisplayName("ECP_invalid: add product with null name")
    @Tag("ECP")
    @Order(2)
    void testAddProduct_ECP_NullName() {
        // Arrange
        Product p = new Product(1, null, 10.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addProduct(p));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol"));
        assertEquals(0, service.getAllProducts().size());
    }

    @Test
    @DisplayName("ECP_invalid: add product with empty name")
    @Tag("ECP")
    @Order(3)
    void testAddProduct_ECP_EmptyName() {
        // Arrange
        Product p = new Product(1, "", 10.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.addProduct(p));
        assertEquals(0, service.getAllProducts().size());
    }

    @Test
    @DisplayName("ECP_invalid: add product with negative price")
    @Tag("ECP")
    @Order(4)
    void testAddProduct_ECP_NegativePrice() {
        // Arrange
        Product p = new Product(1, "Espresso", -5.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addProduct(p));
        assertTrue(ex.getMessage().contains("Pret invalid"));
        assertEquals(0, service.getAllProducts().size());
    }

    // ==================== BVA Tests ====================

    @Test
    @DisplayName("BVA_valid: price just above boundary (0.01)")
    @Tag("BVA")
    @Order(5)
    void testAddProduct_BVA_PriceJustAboveZero() {
        // Arrange
        Product p = new Product(1, "Espresso", 0.01,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        service.addProduct(p);

        // Assert
        assertEquals(1, service.getAllProducts().size());
        assertEquals(0.01, service.findById(1).getPret(), 0.001);
    }

    @Test
    @DisplayName("BVA_valid: price at nominal value (100.0)")
    @Tag("BVA")
    @Order(6)
    void testAddProduct_BVA_PriceNominal() {
        // Arrange
        Product p = new Product(1, "Cappuccino", 100.0,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act
        service.addProduct(p);

        // Assert
        assertEquals(1, service.getAllProducts().size());
        assertEquals(100.0, service.findById(1).getPret(), 0.001);
    }

    @Test
    @DisplayName("BVA_invalid: price at boundary (0.0)")
    @Tag("BVA")
    @Order(7)
    void testAddProduct_BVA_PriceZero() {
        // Arrange
        Product p = new Product(1, "Espresso", 0.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.addProduct(p));
        assertEquals(0, service.getAllProducts().size());
    }

    @Test
    @DisplayName("BVA_invalid: price just below boundary (-0.01)")
    @Tag("BVA")
    @Order(8)
    void testAddProduct_BVA_PriceJustBelowZero() {
        // Arrange
        Product p = new Product(1, "Espresso", -0.01,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.addProduct(p));
        assertEquals(0, service.getAllProducts().size());
    }

    // ==================== Parameterized BVA for name boundary ====================

    @ParameterizedTest(name = "BVA_invalid: blank name \"{0}\" should be rejected")
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("BVA_invalid: blank/empty names at boundary")
    @Tag("BVA")
    @Order(9)
    void testAddProduct_BVA_BlankNames(String name) {
        // Arrange
        Product p = new Product(1, name, 10.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.addProduct(p));
        assertEquals(0, service.getAllProducts().size());
    }

    @ParameterizedTest(name = "BVA_valid: shortest valid name \"{0}\" should succeed")
    @ValueSource(strings = {"A", "Ab"})
    @DisplayName("BVA_valid: minimal-length valid names just above boundary")
    @Tag("BVA")
    @Order(10)
    void testAddProduct_BVA_MinimalValidName(String name) {
        // Arrange
        Product p = new Product(1, name, 10.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        service.addProduct(p);

        // Assert
        assertEquals(1, service.getAllProducts().size());
        assertEquals(name, service.findById(1).getNume());
    }
}
