package com.sigrap.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.audit.AuditLogRepository;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.employee.attendance.AttendanceRepository;
import com.sigrap.employee.schedule.ScheduleRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.sale.Sale;
import com.sigrap.sale.SaleItem;
import com.sigrap.sale.SaleRepository;
import com.sigrap.sale.SaleReturnRepository;
import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.PurchaseOrderItem;
import com.sigrap.supplier.PurchaseOrderRepository;
import com.sigrap.supplier.Supplier;
import com.sigrap.supplier.SupplierRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSeederTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private PurchaseOrderRepository purchaseOrderRepository;

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private SaleRepository saleRepository;

  @Mock
  private SaleReturnRepository saleReturnRepository;

  @Mock
  private AuditLogRepository auditLogRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private DataSeeder dataSeeder;

  private User adminUser;
  private User employeeUser;
  private Category officeCategory;
  private List<Product> products;
  private List<Supplier> suppliers;
  private List<Customer> customers;
  private List<Sale> sales;

  @BeforeEach
  void setUp() {
    adminUser = User.builder()
      .id(1L)
      .name("Admin User")
      .email("admin@sigrap.com")
      .password("encoded-password")
      .role(UserRole.ADMINISTRATOR)
      .build();

    employeeUser = User.builder()
      .id(2L)
      .name("Employee")
      .email("employee@sigrap.com")
      .password("encoded-password")
      .role(UserRole.EMPLOYEE)
      .build();

    officeCategory = Category.builder()
      .id(1L)
      .name("Office Supplies")
      .description("Office supplies and stationery")
      .build();

    // Setup products
    products = new ArrayList<>();
    Product pencil = Product.builder()
      .id(1)
      .name("Pencil")
      .description("HB pencil")
      .salePrice(new BigDecimal("1.50"))
      .costPrice(new BigDecimal("0.75"))
      .stock(100)
      .minimumStockThreshold(10)
      .category(officeCategory)
      .build();
    products.add(pencil);

    // Setup suppliers
    suppliers = new ArrayList<>();
    Supplier supplier = Supplier.builder()
      .id(1L)
      .name("Office Depot")
      .email("contact@officedepot.com")
      .phone("123-456-7890")
      .address("123 Main St")
      .build();
    suppliers.add(supplier);

    // Setup customers
    customers = new ArrayList<>();
    Customer customer = Customer.builder()
      .id(1L)
      .fullName("John Doe")
      .email("john@example.com")
      .phoneNumber("987-654-3210")
      .address("456 Oak St")
      .build();
    customers.add(customer);

    // Setup sales
    sales = new ArrayList<>();
    Sale sale = Sale.builder()
      .id(1)
      .customer(customer)
      .employee(employeeUser)
      .totalAmount(new BigDecimal("15.00"))
      .taxAmount(new BigDecimal("2.00"))
      .discountAmount(BigDecimal.ZERO)
      .finalAmount(new BigDecimal("17.00"))
      .items(new ArrayList<>())
      .createdAt(LocalDateTime.now().minusDays(10))
      .build();

    SaleItem saleItem = new SaleItem();
    saleItem.setProduct(pencil);
    saleItem.setQuantity(10);
    saleItem.setUnitPrice(new BigDecimal("1.50"));
    saleItem.setSubtotal(new BigDecimal("15.00"));
    saleItem.setSale(sale);

    sale.getItems().add(saleItem);
    sales.add(sale);

    // Default mocks
    lenient()
      .when(passwordEncoder.encode(any()))
      .thenReturn("encoded-password");
    lenient()
      .when(categoryRepository.findById(1L))
      .thenReturn(Optional.of(officeCategory));
    lenient().when(userRepository.count()).thenReturn(0L);
    lenient().when(categoryRepository.count()).thenReturn(0L);
    lenient().when(productRepository.count()).thenReturn(0L);
    lenient().when(customerRepository.count()).thenReturn(0L);
    lenient().when(supplierRepository.count()).thenReturn(0L);
    lenient().when(purchaseOrderRepository.count()).thenReturn(0L);
    lenient().when(attendanceRepository.count()).thenReturn(0L);
    lenient().when(scheduleRepository.count()).thenReturn(0L);
    lenient().when(saleRepository.count()).thenReturn(0L);
    lenient().when(saleReturnRepository.count()).thenReturn(0L);
    lenient().when(auditLogRepository.count()).thenReturn(0L);

    // Mock findAll methods
    List<User> users = new ArrayList<>();
    users.add(adminUser);
    users.add(employeeUser);
    lenient().when(userRepository.findAll()).thenReturn(users);
    lenient().when(productRepository.findAll()).thenReturn(products);
    lenient().when(supplierRepository.findAll()).thenReturn(suppliers);
    lenient().when(customerRepository.findAll()).thenReturn(customers);
    lenient().when(saleRepository.findAll()).thenReturn(sales);
  }

  @Test
  void testSeedCategories_WhenCategoriesEmpty() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(categoryRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedCategories_WhenCategoriesExist() throws Exception {
    when(categoryRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(categoryRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenProductsEmpty() throws Exception {
    when(productRepository.count()).thenReturn(0L);

    List<Category> categories = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      categories.add(
        Category.builder().id(Long.valueOf(i)).name("Category " + i).build()
      );
    }
    when(categoryRepository.findAll()).thenReturn(categories);

    lenient()
      .when(customerRepository.findAll())
      .thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(productRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenProductsExist() throws Exception {
    when(productRepository.count()).thenReturn(10L);

    lenient()
      .when(customerRepository.findAll())
      .thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenCategoriesNotFound() throws Exception {
    when(productRepository.count()).thenReturn(0L);
    when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

    lenient()
      .when(customerRepository.findAll())
      .thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedUsers_WhenUsersEmpty() throws Exception {
    when(userRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(userRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedUsers_WhenUsersExist() throws Exception {
    when(userRepository.count()).thenReturn(2L);

    dataSeeder.run();

    verify(userRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedSchedules_WhenSchedulesEmpty() throws Exception {
    when(scheduleRepository.count()).thenReturn(0L);
    when(userRepository.findAll()).thenReturn(List.of(employeeUser));

    dataSeeder.run();

    verify(scheduleRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedSchedules_WhenSchedulesExist() throws Exception {
    when(scheduleRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(scheduleRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedAttendance_WhenAttendanceEmpty() throws Exception {
    when(attendanceRepository.count()).thenReturn(0L);

    List<User> users = new ArrayList<>();
    User gladysUser = User.builder()
      .id(3L)
      .name("Gladys")
      .email("gladys@sigrap.com")
      .password("encoded-password")
      .role(UserRole.EMPLOYEE)
      .build();
    users.add(adminUser);
    users.add(employeeUser);
    users.add(gladysUser);
    when(userRepository.findAll()).thenReturn(users);

    dataSeeder.run();

    verify(attendanceRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedAttendance_WhenAttendanceExist() throws Exception {
    when(attendanceRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(attendanceRepository, never()).saveAll(anyList());
  }

  @Test
  void testCalculateHoursWorked() throws Exception {
    LocalDateTime clockIn = LocalDateTime.of(2023, 1, 1, 8, 0);
    LocalDateTime clockOut = LocalDateTime.of(2023, 1, 1, 17, 0);

    // Create a spy to access the private method
    DataSeeder dataSeederSpy = Mockito.spy(dataSeeder);

    // Use reflection to make the private method accessible
    java.lang.reflect.Method method =
      DataSeeder.class.getDeclaredMethod(
          "calculateHoursWorked",
          LocalDateTime.class,
          LocalDateTime.class
        );
    method.setAccessible(true);

    double hours = (double) method.invoke(dataSeederSpy, clockIn, clockOut);

    assertEquals(9.0, hours);
  }

  @Test
  void testSeedSuppliers_WhenSuppliersEmpty() throws Exception {
    when(supplierRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(supplierRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedSuppliers_WhenSuppliersExist() throws Exception {
    when(supplierRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(supplierRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedPurchaseOrders_WhenOrdersEmpty() throws Exception {
    when(purchaseOrderRepository.count()).thenReturn(0L);
    when(supplierRepository.findAll()).thenReturn(suppliers);
    when(productRepository.findAll()).thenReturn(products);

    dataSeeder.run();

    verify(purchaseOrderRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedPurchaseOrders_WhenOrdersExist() throws Exception {
    when(purchaseOrderRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(purchaseOrderRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedPurchaseOrders_WhenSuppliersEmpty() throws Exception {
    when(purchaseOrderRepository.count()).thenReturn(0L);
    when(supplierRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(purchaseOrderRepository, never()).saveAll(anyList());
  }

  @Test
  void testFindSupplierByName() throws Exception {
    // Create a spy to access the private method
    DataSeeder dataSeederSpy = Mockito.spy(dataSeeder);

    // Use reflection to make the private method accessible
    java.lang.reflect.Method method =
      DataSeeder.class.getDeclaredMethod(
          "findSupplierByName",
          List.class,
          String.class
        );
    method.setAccessible(true);

    Supplier result = (Supplier) method.invoke(
      dataSeederSpy,
      suppliers,
      "Office Depot"
    );

    assertEquals(suppliers.get(0), result);
  }

  @Test
  void testFindProductByName() throws Exception {
    // Create a spy to access the private method
    DataSeeder dataSeederSpy = Mockito.spy(dataSeeder);

    // Use reflection to make the private method accessible
    java.lang.reflect.Method method =
      DataSeeder.class.getDeclaredMethod(
          "findProductByName",
          List.class,
          String.class
        );
    method.setAccessible(true);

    Product result = (Product) method.invoke(dataSeederSpy, products, "Pencil");

    assertEquals(products.get(0), result);
  }

  @Test
  void testCalculateOrderTotal() throws Exception {
    // Create a purchase order with items
    PurchaseOrder order = new PurchaseOrder();
    List<PurchaseOrderItem> items = new ArrayList<>();

    PurchaseOrderItem item1 = new PurchaseOrderItem();
    item1.setQuantity(10);
    item1.setUnitPrice(new BigDecimal("5.00"));

    PurchaseOrderItem item2 = new PurchaseOrderItem();
    item2.setQuantity(5);
    item2.setUnitPrice(new BigDecimal("10.00"));

    items.add(item1);
    items.add(item2);
    order.setItems(items);

    // Create a spy to access the private method
    DataSeeder dataSeederSpy = Mockito.spy(dataSeeder);

    // Use reflection to make the private method accessible
    java.lang.reflect.Method method =
      DataSeeder.class.getDeclaredMethod(
          "calculateOrderTotal",
          PurchaseOrder.class
        );
    method.setAccessible(true);

    BigDecimal total = (BigDecimal) method.invoke(dataSeederSpy, order);

    assertEquals(new BigDecimal("100.00"), total);
  }

  @Test
  void testSeedCustomers_WhenCustomersEmpty() throws Exception {
    when(customerRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(customerRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedCustomers_WhenCustomersExist() throws Exception {
    when(customerRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(customerRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedSales_WhenSalesEmpty() throws Exception {
    when(saleRepository.count()).thenReturn(0L);
    when(customerRepository.findAll()).thenReturn(customers);
    when(productRepository.findAll()).thenReturn(products);
    List<User> users = new ArrayList<>();
    users.add(employeeUser);
    when(userRepository.findAll()).thenReturn(users);

    dataSeeder.run();

    verify(saleRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedSales_WhenSalesExist() throws Exception {
    when(saleRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(saleRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedSales_WhenCustomersEmpty() throws Exception {
    when(saleRepository.count()).thenReturn(0L);
    when(customerRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(saleRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedSaleReturns_WhenReturnsEmpty() throws Exception {
    when(saleReturnRepository.count()).thenReturn(0L);
    when(saleRepository.findAll()).thenReturn(sales);

    dataSeeder.run();

    verify(saleReturnRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedSaleReturns_WhenReturnsExist() throws Exception {
    when(saleReturnRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(saleReturnRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedSaleReturns_WhenSalesEmpty() throws Exception {
    when(saleReturnRepository.count()).thenReturn(0L);
    when(saleRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(saleReturnRepository, never()).saveAll(anyList());
  }

  @Test
  void testGetRandomReturnReason() throws Exception {
    // Create a spy to access the private method
    DataSeeder dataSeederSpy = Mockito.spy(dataSeeder);

    // Use reflection to make the private method accessible
    java.lang.reflect.Method method =
      DataSeeder.class.getDeclaredMethod("getRandomReturnReason");
    method.setAccessible(true);

    String reason = (String) method.invoke(dataSeederSpy);

    // Just verify it returns a non-empty string
    assert reason != null && !reason.isEmpty();
  }

  @Test
  void testSeedAuditLogs_WhenAuditLogsEmpty() throws Exception {
    when(auditLogRepository.count()).thenReturn(0L);

    // Mock userRepository.findAll() to return a list with at least one user
    List<User> users = new ArrayList<>();
    User adminUser = User.builder()
      .id(1L)
      .name("Admin")
      .email("rosita@sigrap.com")
      .role(UserRole.ADMINISTRATOR)
      .build();
    User employeeUser = User.builder()
      .id(2L)
      .name("Employee")
      .email("gladys@sigrap.com")
      .role(UserRole.EMPLOYEE)
      .build();
    users.add(adminUser);
    users.add(employeeUser);

    when(userRepository.findAll()).thenReturn(users);

    dataSeeder.run();

    verify(auditLogRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedAuditLogs_WhenAuditLogsExist() throws Exception {
    when(auditLogRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(auditLogRepository, never()).saveAll(anyList());
  }
}
