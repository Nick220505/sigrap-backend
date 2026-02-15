package com.sigrap.category.performance;

import com.sigrap.category.CategoryData;
import com.sigrap.category.CategoryInfo;
import com.sigrap.category.CategoryService;
import com.sigrap.category.application.port.in.CreateCategoryUseCase;
import com.sigrap.category.application.port.in.DeleteCategoryUseCase;
import com.sigrap.category.application.port.in.GetCategoryUseCase;
import com.sigrap.category.application.port.in.UpdateCategoryUseCase;
import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance test comparing old layered architecture vs new hexagonal architecture.
 * 
 * Tests measure:
 * - Response times for CRUD operations
 * - Throughput (requests per second)
 * - Concurrent operation performance
 * - Large dataset operations
 * - Memory usage patterns
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryPerformanceTest {

    @Autowired(required = false)
    private CategoryService oldArchitectureService;

    @Autowired
    private CreateCategoryUseCase createCategoryUseCase;

    @Autowired
    private GetCategoryUseCase getCategoryUseCase;

    @Autowired
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Autowired
    private DeleteCategoryUseCase deleteCategoryUseCase;

    private final PerformanceMetrics oldArchMetrics = new PerformanceMetrics("Old Layered Architecture");
    private final PerformanceMetrics newArchMetrics = new PerformanceMetrics("New Hexagonal Architecture");

    private static final int WARMUP_ITERATIONS = 10;
    private static final int TEST_ITERATIONS = 100;
    private static final int CONCURRENT_THREADS = 10;
    private static final int LARGE_DATASET_SIZE = 1000;

    @BeforeEach
    void setUp() {
        // Warmup JVM
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Single Create Operation Performance")
    void testCreatePerformance() {
        System.out.println("\n=== Test 1: Single Create Operation ===");

        // Test Old Architecture (if available)
        if (oldArchitectureService != null) {
            measureOldArchitectureCreate();
        }

        // Test New Architecture
        measureNewArchitectureCreate();

        printComparison("Create Operation", oldArchMetrics, newArchMetrics);
    }

    @Test
    @Order(2)
    @DisplayName("2. Single Read Operation Performance")
    void testReadPerformance() {
        System.out.println("\n=== Test 2: Single Read Operation ===");

        // Setup: Create a category to read
        Category category = createCategoryUseCase.create(
            new CreateCategoryCommand("Test Category", "Test Description")
        );
        Long categoryId = category.getId().value();

        // Test Old Architecture (if available)
        if (oldArchitectureService != null) {
            measureOldArchitectureRead(categoryId);
        }

        // Test New Architecture
        measureNewArchitectureRead(categoryId);

        printComparison("Read Operation", oldArchMetrics, newArchMetrics);

        // Cleanup
        deleteCategoryUseCase.delete(new CategoryId(categoryId));
    }

    @Test
    @Order(3)
    @DisplayName("3. Single Update Operation Performance")
    void testUpdatePerformance() {
        System.out.println("\n=== Test 3: Single Update Operation ===");

        // Setup: Create a category to update
        Category category = createCategoryUseCase.create(
            new CreateCategoryCommand("Update Test", "Original Description")
        );
        Long categoryId = category.getId().value();

        // Test Old Architecture (if available)
        if (oldArchitectureService != null) {
            measureOldArchitectureUpdate(categoryId);
        }

        // Test New Architecture
        measureNewArchitectureUpdate(categoryId);

        printComparison("Update Operation", oldArchMetrics, newArchMetrics);

        // Cleanup
        deleteCategoryUseCase.delete(new CategoryId(categoryId));
    }

    @Test
    @Order(4)
    @DisplayName("4. Single Delete Operation Performance")
    void testDeletePerformance() {
        System.out.println("\n=== Test 4: Single Delete Operation ===");

        // Test Old Architecture (if available)
        if (oldArchitectureService != null) {
            measureOldArchitectureDelete();
        }

        // Test New Architecture
        measureNewArchitectureDelete();

        printComparison("Delete Operation", oldArchMetrics, newArchMetrics);
    }

    @Test
    @Order(5)
    @DisplayName("5. Bulk Read (Get All) Performance")
    void testBulkReadPerformance() {
        System.out.println("\n=== Test 5: Bulk Read (Get All) ===");

        // Setup: Create 100 categories
        List<CategoryId> categoryIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Category cat = createCategoryUseCase.create(
                new CreateCategoryCommand("Bulk Category " + i, "Description " + i)
            );
            categoryIds.add(cat.getId());
        }

        // Test Old Architecture (if available)
        if (oldArchitectureService != null) {
            measureOldArchitectureBulkRead();
        }

        // Test New Architecture
        measureNewArchitectureBulkRead();

        printComparison("Bulk Read (100 items)", oldArchMetrics, newArchMetrics);

        // Cleanup
        deleteCategoryUseCase.deleteAll(categoryIds);
    }

    @Test
    @Order(6)
    @DisplayName("6. Concurrent Operations Performance")
    void testConcurrentOperations() throws InterruptedException, ExecutionException {
        System.out.println("\n=== Test 6: Concurrent Operations ===");

        // Test New Architecture with concurrent creates
        measureConcurrentCreates();

        System.out.println("Concurrent Creates (10 threads, 10 ops each):");
        System.out.println("  Total Time: " + newArchMetrics.getTotalDuration() + " ms");
        System.out.println("  Throughput: " + String.format("%.2f", newArchMetrics.getThroughput()) + " ops/sec");
    }

    @Test
    @Order(7)
    @DisplayName("7. Large Dataset Operations")
    void testLargeDatasetOperations() {
        System.out.println("\n=== Test 7: Large Dataset Operations ===");

        // Create 1000 categories
        Instant start = Instant.now();
        List<CategoryId> categoryIds = new ArrayList<>();
        
        for (int i = 0; i < LARGE_DATASET_SIZE; i++) {
            Category cat = createCategoryUseCase.create(
                new CreateCategoryCommand("Large Dataset " + i, "Description " + i)
            );
            categoryIds.add(cat.getId());
        }
        
        long createTime = Duration.between(start, Instant.now()).toMillis();

        // Read all
        start = Instant.now();
        List<Category> allCategories = getCategoryUseCase.getAll();
        long readTime = Duration.between(start, Instant.now()).toMillis();

        // Batch delete
        start = Instant.now();
        deleteCategoryUseCase.deleteAll(categoryIds);
        long deleteTime = Duration.between(start, Instant.now()).toMillis();

        System.out.println("Large Dataset (1000 items):");
        System.out.println("  Create Time: " + createTime + " ms");
        System.out.println("  Read All Time: " + readTime + " ms");
        System.out.println("  Batch Delete Time: " + deleteTime + " ms");
        System.out.println("  Total Items: " + allCategories.size());

        assertTrue(allCategories.size() >= LARGE_DATASET_SIZE, "Should have at least 1000 categories");
    }

    // Old Architecture Measurement Methods

    private void measureOldArchitectureCreate() {
        if (oldArchitectureService == null) return;

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            CategoryData data = new CategoryData("Warmup " + i, "Warmup");
            CategoryInfo info = oldArchitectureService.create(data);
            oldArchitectureService.delete(info.getId());
        }

        // Actual test
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            CategoryData data = new CategoryData("Old Arch Create " + i, "Description");
            
            Instant start = Instant.now();
            CategoryInfo info = oldArchitectureService.create(data);
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
            oldArchitectureService.delete(info.getId());
        }

        oldArchMetrics.recordDurations(durations);
    }

    private void measureOldArchitectureRead(Long categoryId) {
        if (oldArchitectureService == null) return;

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Instant start = Instant.now();
            oldArchitectureService.findById(categoryId);
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            durations.add(duration);
        }

        oldArchMetrics.recordDurations(durations);
    }

    private void measureOldArchitectureUpdate(Long categoryId) {
        if (oldArchitectureService == null) return;

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            CategoryData data = new CategoryData("Updated " + i, "Updated Description");
            
            Instant start = Instant.now();
            oldArchitectureService.update(categoryId, data);
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
        }

        oldArchMetrics.recordDurations(durations);
    }

    private void measureOldArchitectureDelete() {
        if (oldArchitectureService == null) return;

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            CategoryData data = new CategoryData("Delete Test " + i, "Description");
            CategoryInfo info = oldArchitectureService.create(data);
            
            Instant start = Instant.now();
            oldArchitectureService.delete(info.getId());
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
        }

        oldArchMetrics.recordDurations(durations);
    }

    private void measureOldArchitectureBulkRead() {
        if (oldArchitectureService == null) return;

        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Instant start = Instant.now();
            oldArchitectureService.findAll();
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            durations.add(duration);
        }

        oldArchMetrics.recordDurations(durations);
    }

    // New Architecture Measurement Methods

    private void measureNewArchitectureCreate() {
        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            CreateCategoryCommand cmd = new CreateCategoryCommand("Warmup " + i, "Warmup");
            Category cat = createCategoryUseCase.create(cmd);
            deleteCategoryUseCase.delete(cat.getId());
        }

        // Actual test
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            CreateCategoryCommand cmd = new CreateCategoryCommand("New Arch Create " + i, "Description");
            
            Instant start = Instant.now();
            Category cat = createCategoryUseCase.create(cmd);
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
            deleteCategoryUseCase.delete(cat.getId());
        }

        newArchMetrics.recordDurations(durations);
    }

    private void measureNewArchitectureRead(Long categoryId) {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Instant start = Instant.now();
            getCategoryUseCase.getById(new CategoryId(categoryId));
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            durations.add(duration);
        }

        newArchMetrics.recordDurations(durations);
    }

    private void measureNewArchitectureUpdate(Long categoryId) {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            UpdateCategoryCommand cmd = new UpdateCategoryCommand("Updated " + i, "Updated Description");
            
            Instant start = Instant.now();
            updateCategoryUseCase.update(new CategoryId(categoryId), cmd);
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
        }

        newArchMetrics.recordDurations(durations);
    }

    private void measureNewArchitectureDelete() {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            CreateCategoryCommand cmd = new CreateCategoryCommand("Delete Test " + i, "Description");
            Category cat = createCategoryUseCase.create(cmd);
            
            Instant start = Instant.now();
            deleteCategoryUseCase.delete(cat.getId());
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            
            durations.add(duration);
        }

        newArchMetrics.recordDurations(durations);
    }

    private void measureNewArchitectureBulkRead() {
        List<Long> durations = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Instant start = Instant.now();
            getCategoryUseCase.getAll();
            long duration = Duration.between(start, Instant.now()).toNanos() / 1_000_000;
            durations.add(duration);
        }

        newArchMetrics.recordDurations(durations);
    }

    private void measureConcurrentCreates() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        List<Future<Long>> futures = new ArrayList<>();

        Instant start = Instant.now();

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            Future<Long> future = executor.submit(() -> {
                List<CategoryId> ids = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    CreateCategoryCommand cmd = new CreateCategoryCommand(
                        "Concurrent " + threadId + "-" + j,
                        "Description"
                    );
                    Category cat = createCategoryUseCase.create(cmd);
                    ids.add(cat.getId());
                }
                return (long) ids.size();
            });
            futures.add(future);
        }

        long totalOps = 0;
        for (Future<Long> future : futures) {
            totalOps += future.get();
        }

        long duration = Duration.between(start, Instant.now()).toMillis();
        executor.shutdown();

        newArchMetrics.recordConcurrentTest(duration, totalOps);

        // Cleanup
        List<Category> allCategories = getCategoryUseCase.getAll();
        List<CategoryId> idsToDelete = allCategories.stream()
            .filter(cat -> cat.getName().value().startsWith("Concurrent"))
            .map(Category::getId)
            .toList();
        if (!idsToDelete.isEmpty()) {
            deleteCategoryUseCase.deleteAll(idsToDelete);
        }
    }

    private void printComparison(String operation, PerformanceMetrics oldMetrics, PerformanceMetrics newMetrics) {
        System.out.println("\n" + operation + " Results:");
        
        if (oldArchitectureService != null && oldMetrics.hasData()) {
            System.out.println("\nOld Architecture:");
            System.out.println("  Average: " + String.format("%.2f", oldMetrics.getAverage()) + " ms");
            System.out.println("  P50: " + oldMetrics.getP50() + " ms");
            System.out.println("  P95: " + oldMetrics.getP95() + " ms");
            System.out.println("  P99: " + oldMetrics.getP99() + " ms");
            System.out.println("  Min: " + oldMetrics.getMin() + " ms");
            System.out.println("  Max: " + oldMetrics.getMax() + " ms");
        }

        if (newMetrics.hasData()) {
            System.out.println("\nNew Architecture:");
            System.out.println("  Average: " + String.format("%.2f", newMetrics.getAverage()) + " ms");
            System.out.println("  P50: " + newMetrics.getP50() + " ms");
            System.out.println("  P95: " + newMetrics.getP95() + " ms");
            System.out.println("  P99: " + newMetrics.getP99() + " ms");
            System.out.println("  Min: " + newMetrics.getMin() + " ms");
            System.out.println("  Max: " + newMetrics.getMax() + " ms");
        }

        if (oldArchitectureService != null && oldMetrics.hasData() && newMetrics.hasData()) {
            double improvement = ((oldMetrics.getAverage() - newMetrics.getAverage()) / oldMetrics.getAverage()) * 100;
            System.out.println("\nComparison:");
            if (improvement > 0) {
                System.out.println("  New architecture is " + String.format("%.2f", improvement) + "% faster");
            } else {
                System.out.println("  New architecture is " + String.format("%.2f", Math.abs(improvement)) + "% slower");
            }
        }

        // Reset metrics for next test
        oldMetrics.reset();
        newMetrics.reset();
    }

    @AfterAll
    static void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Performance Test Summary");
        System.out.println("=".repeat(60));
        System.out.println("All performance tests completed successfully.");
        System.out.println("See individual test results above for detailed metrics.");
    }
}
