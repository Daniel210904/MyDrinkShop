package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StocServiceTest {

    private StocService service;
    private AbstractRepository<Integer, Stoc> repo;

    @BeforeEach
    void setUp() {
        repo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
        service = new StocService(repo);
    }

    @AfterEach
    void tearDown() {
        repo = null;
        service = null;
    }

    // ==================== Statement Coverage (sc) ====================

    @Test
    @DisplayName("SC_01: null reteta -> IllegalArgumentException (D1=true)")
    @Tag("WBT")
    @Order(1)
    void testConsuma_NullReteta() {
        assertThrows(IllegalArgumentException.class, () -> service.consuma(null));
    }

    @Test
    @DisplayName("SC_02: reteta with null ingredients -> returns normally (D2=true)")
    @Tag("WBT")
    @Order(2)
    void testConsuma_NullIngredients() {
        Reteta reteta = new Reteta(1, null);
        assertDoesNotThrow(() -> service.consuma(reteta));
    }

    @Test
    @DisplayName("SC_03: reteta with empty ingredients -> returns normally (D3=true)")
    @Tag("WBT")
    @Order(3)
    void testConsuma_EmptyIngredients() {
        Reteta reteta = new Reteta(1, Collections.emptyList());
        assertDoesNotThrow(() -> service.consuma(reteta));
    }

    @Test
    @DisplayName("SC_04: insufficient stock -> IllegalStateException (D4=true)")
    @Tag("WBT")
    @Order(4)
    void testConsuma_InsufficientStock() {
        repo.save(new Stoc(1, "Lapte", 5, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Lapte", 100)));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.consuma(reteta));
        assertTrue(ex.getMessage().contains("Stoc insuficient"));
    }

    @Test
    @DisplayName("SC_05: single ingredient, single stock entry -> stock consumed (full path)")
    @Tag("WBT")
    @Order(5)
    void testConsuma_SingleIngredient_SingleStock() {
        repo.save(new Stoc(1, "Cafea", 100, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 30)));

        service.consuma(reteta);

        assertEquals(70.0, repo.findOne(1).getCantitate(), 0.001);
    }

    // ==================== Decision / Condition Coverage (dc, cc, dcc) ====================

    @Test
    @DisplayName("DCC_01: D1=false, D2=false, D3=false, D4=false -> consume proceeds")
    @Tag("WBT")
    @Order(6)
    void testConsuma_AllGuardsFalse_HappyPath() {
        repo.save(new Stoc(1, "Zahar", 50, 0));
        repo.save(new Stoc(2, "Lapte", 80, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Zahar", 10),
                new IngredientReteta("Lapte", 20)));

        service.consuma(reteta);

        assertEquals(40.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(60.0, repo.findOne(2).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("DCC_02: D7=true -> inner loop breaks when ramas <= 0")
    @Tag("WBT")
    @Order(7)
    void testConsuma_InnerLoopBreak_RamasZero() {
        repo.save(new Stoc(1, "Cafea", 50, 0));
        repo.save(new Stoc(2, "Cafea", 30, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 50)));

        service.consuma(reteta);

        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(30.0, repo.findOne(2).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("DCC_03: D7=false -> inner loop continues consuming from next stock entry")
    @Tag("WBT")
    @Order(8)
    void testConsuma_InnerLoopContinues_MultipleStockEntries() {
        repo.save(new Stoc(1, "Cafea", 20, 0));
        repo.save(new Stoc(2, "Cafea", 40, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 50)));

        service.consuma(reteta);

        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(10.0, repo.findOne(2).getCantitate(), 0.001);
    }

    // ==================== Multiple Condition Coverage (mcc) ====================

    @Test
    @DisplayName("MCC_01: D2 compound: ingredients=null (first condition true, short-circuit)")
    @Tag("WBT")
    @Order(9)
    void testConsuma_MCC_IngredientsNull() {
        Reteta reteta = new Reteta(1, null);
        assertDoesNotThrow(() -> service.consuma(reteta));
    }

    @Test
    @DisplayName("MCC_02: D2 compound: ingredients!=null but empty (first false, second true)")
    @Tag("WBT")
    @Order(10)
    void testConsuma_MCC_IngredientsNotNullButEmpty() {
        Reteta reteta = new Reteta(1, new ArrayList<>());
        assertDoesNotThrow(() -> service.consuma(reteta));
    }

    @Test
    @DisplayName("MCC_03: D2 compound: ingredients!=null and not empty (both conditions false)")
    @Tag("WBT")
    @Order(11)
    void testConsuma_MCC_IngredientsPresent() {
        repo.save(new Stoc(1, "Cafea", 100, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 10)));

        service.consuma(reteta);

        assertEquals(90.0, repo.findOne(1).getCantitate(), 0.001);
    }

    // ==================== All Path Coverage (apc) ====================

    @Test
    @DisplayName("APC_01: Path 1 -> D1=true exit")
    @Tag("WBT")
    @Order(12)
    void testConsuma_Path1_NullReteta() {
        assertThrows(IllegalArgumentException.class, () -> service.consuma(null));
    }

    @Test
    @DisplayName("APC_02: Path 2 -> D1=F, D2=T exit")
    @Tag("WBT")
    @Order(13)
    void testConsuma_Path2_NullIngredients() {
        assertDoesNotThrow(() -> service.consuma(new Reteta(1, null)));
    }

    @Test
    @DisplayName("APC_03: Path 3 -> D1=F, D2=F, D3=T exit")
    @Tag("WBT")
    @Order(14)
    void testConsuma_Path3_EmptyIngredients() {
        assertDoesNotThrow(() -> service.consuma(new Reteta(1, List.of())));
    }

    @Test
    @DisplayName("APC_04: Path 4 -> D1=F, D2=F, D3=F, D4=T exit (insuf. stock)")
    @Tag("WBT")
    @Order(15)
    void testConsuma_Path4_InsufficientStock() {
        repo.save(new Stoc(1, "Lapte", 2, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Lapte", 500)));

        assertThrows(IllegalStateException.class, () -> service.consuma(reteta));
        assertEquals(2.0, repo.findOne(1).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("APC_05: Path 5 -> full path, outer 1 iter, inner 1 iter, D7=F")
    @Tag("WBT")
    @Order(16)
    void testConsuma_Path5_SingleIterBoth() {
        repo.save(new Stoc(1, "Cafea", 100, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 25)));

        service.consuma(reteta);
        assertEquals(75.0, repo.findOne(1).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("APC_06: Path 6 -> full path, inner loop breaks via D7=T on 2nd entry")
    @Tag("WBT")
    @Order(17)
    void testConsuma_Path6_InnerBreak() {
        repo.save(new Stoc(1, "Cafea", 30, 0));
        repo.save(new Stoc(2, "Cafea", 30, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 30)));

        service.consuma(reteta);

        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(30.0, repo.findOne(2).getCantitate(), 0.001);
    }

    // ==================== Simple Loop Coverage (lc) ====================

    @Test
    @DisplayName("LC_outer_0: outer loop 0 iterations (empty ingredients)")
    @Tag("WBT")
    @Order(18)
    void testConsuma_OuterLoop_ZeroIterations() {
        Reteta reteta = new Reteta(1, Collections.emptyList());
        assertDoesNotThrow(() -> service.consuma(reteta));
    }

    @Test
    @DisplayName("LC_outer_1: outer loop 1 iteration (1 ingredient)")
    @Tag("WBT")
    @Order(19)
    void testConsuma_OuterLoop_OneIteration() {
        repo.save(new Stoc(1, "Cafea", 50, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 10)));

        service.consuma(reteta);
        assertEquals(40.0, repo.findOne(1).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("LC_outer_2: outer loop 2 iterations (2 ingredients)")
    @Tag("WBT")
    @Order(20)
    void testConsuma_OuterLoop_TwoIterations() {
        repo.save(new Stoc(1, "Cafea", 50, 0));
        repo.save(new Stoc(2, "Lapte", 80, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 10),
                new IngredientReteta("Lapte", 20)));

        service.consuma(reteta);
        assertEquals(40.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(60.0, repo.findOne(2).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("LC_outer_N: outer loop N iterations (3 ingredients)")
    @Tag("WBT")
    @Order(21)
    void testConsuma_OuterLoop_ThreeIterations() {
        repo.save(new Stoc(1, "Cafea", 50, 0));
        repo.save(new Stoc(2, "Lapte", 80, 0));
        repo.save(new Stoc(3, "Zahar", 100, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 5),
                new IngredientReteta("Lapte", 10),
                new IngredientReteta("Zahar", 15)));

        service.consuma(reteta);
        assertEquals(45.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(70.0, repo.findOne(2).getCantitate(), 0.001);
        assertEquals(85.0, repo.findOne(3).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("LC_inner_1: inner loop 1 iteration (1 stock entry per ingredient)")
    @Tag("WBT")
    @Order(22)
    void testConsuma_InnerLoop_OneIteration() {
        repo.save(new Stoc(1, "Cafea", 100, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 40)));

        service.consuma(reteta);
        assertEquals(60.0, repo.findOne(1).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("LC_inner_2: inner loop 2 iterations (stock split across 2 entries)")
    @Tag("WBT")
    @Order(23)
    void testConsuma_InnerLoop_TwoIterations() {
        repo.save(new Stoc(1, "Cafea", 20, 0));
        repo.save(new Stoc(2, "Cafea", 30, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 35)));

        service.consuma(reteta);
        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(15.0, repo.findOne(2).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("LC_inner_N: inner loop 3 iterations (stock split across 3 entries)")
    @Tag("WBT")
    @Order(24)
    void testConsuma_InnerLoop_ThreeIterations() {
        repo.save(new Stoc(1, "Cafea", 10, 0));
        repo.save(new Stoc(2, "Cafea", 10, 0));
        repo.save(new Stoc(3, "Cafea", 10, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 25)));

        service.consuma(reteta);
        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
        assertEquals(0.0, repo.findOne(2).getCantitate(), 0.001);
        assertEquals(5.0, repo.findOne(3).getCantitate(), 0.001);
    }

    // ==================== Additional edge cases ====================

    @Test
    @DisplayName("EDGE_01: case-insensitive ingredient matching")
    @Tag("WBT")
    @Order(25)
    void testConsuma_CaseInsensitiveMatch() {
        repo.save(new Stoc(1, "CAFEA", 50, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("cafea", 10)));

        service.consuma(reteta);
        assertEquals(40.0, repo.findOne(1).getCantitate(), 0.001);
    }

    @Test
    @DisplayName("EDGE_02: stock fully consumed to zero")
    @Tag("WBT")
    @Order(26)
    void testConsuma_StockFullyConsumed() {
        repo.save(new Stoc(1, "Cafea", 30, 0));

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("Cafea", 30)));

        service.consuma(reteta);
        assertEquals(0.0, repo.findOne(1).getCantitate(), 0.001);
    }
}
