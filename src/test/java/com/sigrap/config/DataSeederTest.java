package com.sigrap.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigrap.audit.AuditLogRepository;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.employee.attendance.AttendanceRepository;
import com.sigrap.employee.schedule.ScheduleRepository;
import com.sigrap.product.ProductRepository;
import com.sigrap.sale.SaleRepository;
import com.sigrap.sale.SaleReturnRepository;
import com.sigrap.supplier.PurchaseOrderRepository;
import com.sigrap.supplier.SupplierRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;

@ExtendWith(MockitoExtension.class)
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
  private Category officeCategory;

  @BeforeEach
  void setUp() {
    adminUser = User.builder()
      .id(1L)
      .name("Admin User")
      .email("admin@sigrap.com")
      .password("encoded-password")
      .role(UserRole.ADMINISTRATOR)
      .build();

    officeCategory = Category.builder()
      .id(1L)
      .name("Office Supplies")
      .description("Office supplies and stationery")
      .build();

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
    lenient().when(saleRepository.count()).thenReturn(2L);
    lenient().when(saleReturnRepository.count()).thenReturn(1L);
    lenient().when(auditLogRepository.count()).thenReturn(0L);
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

    dataSeeder.run();

    verify(productRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenProductsExist() throws Exception {
    when(productRepository.count()).thenReturn(10L);

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenCategoriesNotFound() throws Exception {
    when(productRepository.count()).thenReturn(0L);
    when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

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
