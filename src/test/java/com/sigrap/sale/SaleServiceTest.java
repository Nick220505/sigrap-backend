package com.sigrap.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerInfo;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserInfo;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SaleServiceTest {

  @Mock
  private SaleRepository saleRepository;

  @Mock
  private SaleItemRepository saleItemRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SaleMapper saleMapper;

  @InjectMocks
  private SaleService saleService;

  @Captor
  private ArgumentCaptor<Sale> saleCaptor;

  @Captor
  private ArgumentCaptor<List<Integer>> idsCaptor;

  private Sale testSale;
  private SaleInfo testSaleInfo;
  private SaleItem testSaleItem;
  private SaleItemInfo testSaleItemInfo;
  private SaleData testSaleData;
  private SaleItemData testSaleItemData;
  private Customer testCustomer;
  private User testEmployee;
  private Product testProduct;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    testDateTime = LocalDateTime.now();

    testCustomer = Customer.builder().id(1L).build();

    testEmployee = User.builder().id(1L).build();

    testProduct = Product.builder()
      .id(1)
      .name("Test Product")
      .stock(100)
      .build();

    testSaleItem = SaleItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    List<SaleItem> items = new ArrayList<>();
    items.add(testSaleItem);

    testSale = Sale.builder()
      .id(1)
      .customer(testCustomer)
      .employee(testEmployee)
      .totalAmount(new BigDecimal("100.00"))
      .taxAmount(new BigDecimal("19.00"))
      .discountAmount(BigDecimal.ZERO)
      .finalAmount(new BigDecimal("119.00"))
      .build();

    testSaleItem.setSale(null);

    testSale.setItems(items);

    testSaleInfo = SaleInfo.builder()
      .id(1)
      .customer(
        CustomerInfo.builder()
          .id(1L)
          .fullName("Test Customer")
          .email("customer@example.com")
          .phoneNumber("1234567890")
          .build()
      )
      .employee(
        UserInfo.builder()
          .id(1L)
          .name("Test Employee")
          .email("employee@example.com")
          .role(UserRole.EMPLOYEE)
          .build()
      )
      .totalAmount(new BigDecimal("100.00"))
      .taxAmount(new BigDecimal("19.00"))
      .discountAmount(BigDecimal.ZERO)
      .finalAmount(new BigDecimal("119.00"))
      .build();

    testSaleItemData = SaleItemData.builder()
      .productId(1)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    List<SaleItemData> itemDataList = new ArrayList<>();
    itemDataList.add(testSaleItemData);

    testSaleData = SaleData.builder()
      .customerId(1L)
      .employeeId(1L)
      .totalAmount(new BigDecimal("100.00"))
      .taxAmount(new BigDecimal("19.00"))
      .discountAmount(BigDecimal.ZERO)
      .finalAmount(new BigDecimal("119.00"))
      .items(itemDataList)
      .build();
  }

  @Test
  void findAll_shouldReturnAllSales() {
    List<Sale> sales = Arrays.asList(testSale, testSale);
    List<SaleInfo> expected = Arrays.asList(testSaleInfo, testSaleInfo);

    when(saleRepository.findAll()).thenReturn(sales);
    when(saleMapper.toInfoList(sales)).thenReturn(expected);

    List<SaleInfo> result = saleService.findAll();

    assertEquals(expected, result);
  }

  @Test
  void findByEmployeeId_shouldReturnEmployeesSales() {
    List<Sale> employeeSales = Arrays.asList(testSale, testSale);
    List<SaleInfo> expected = Arrays.asList(testSaleInfo, testSaleInfo);

    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleRepository.findByEmployee(testEmployee)).thenReturn(employeeSales);
    when(saleMapper.toInfoList(employeeSales)).thenReturn(expected);

    List<SaleInfo> result = saleService.findByEmployeeId(1L);

    assertEquals(expected, result);
  }

  @Test
  void findByEmployeeId_shouldThrowException_whenEmployeeNotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleService.findByEmployeeId(999L)
    );
  }

  @Test
  void findByCustomerId_shouldReturnCustomersSales() {
    List<Sale> customerSales = Arrays.asList(testSale, testSale);
    List<SaleInfo> expected = Arrays.asList(testSaleInfo, testSaleInfo);

    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(saleRepository.findByCustomer(testCustomer)).thenReturn(customerSales);
    when(saleMapper.toInfoList(customerSales)).thenReturn(expected);

    List<SaleInfo> result = saleService.findByCustomerId(1L);

    assertEquals(expected, result);
  }

  @Test
  void findByCustomerId_shouldThrowException_whenCustomerNotFound() {
    when(customerRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleService.findByCustomerId(999L)
    );
  }

  @Test
  void findByCreatedDateRange_shouldReturnSalesInDateRange() {
    LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);
    List<Sale> dateRangeSales = Arrays.asList(testSale, testSale);
    List<SaleInfo> expected = Arrays.asList(testSaleInfo, testSaleInfo);

    when(saleRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(
      dateRangeSales
    );
    when(saleMapper.toInfoList(dateRangeSales)).thenReturn(expected);

    List<SaleInfo> result = saleService.findByCreatedDateRange(
      startDate,
      endDate
    );

    assertEquals(expected, result);
  }

  @Test
  void findById_shouldReturnSale_whenFound() {
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(saleMapper.toInfo(testSale)).thenReturn(testSaleInfo);

    SaleInfo result = saleService.findById(1);

    assertEquals(testSaleInfo, result);
  }

  @Test
  void findById_shouldThrowException_whenNotFound() {
    when(saleRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> saleService.findById(999)
    );
  }

  @Test
  void create_shouldCreateSaleAndAdjustStock() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleMapper.toEntity(testSaleData)).thenReturn(testSale);
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

    doNothing()
      .when(saleMapper)
      .setCustomerAndEmployee(testSale, testCustomer, testEmployee);

    when(saleRepository.save(testSale)).thenReturn(testSale);

    when(saleRepository.findById(testSale.getId())).thenReturn(
      Optional.of(testSale)
    );

    when(saleItemRepository.save(any(SaleItem.class))).thenReturn(testSaleItem);

    when(saleMapper.toInfo(testSale)).thenReturn(testSaleInfo);

    SaleInfo result = saleService.create(testSaleData);

    assertEquals(testSaleInfo, result);
    verify(saleMapper).setCustomerAndEmployee(
      testSale,
      testCustomer,
      testEmployee
    );
    verify(productRepository).save(testProduct);
    assertEquals(98, testProduct.getStock());
  }

  @Test
  void create_shouldThrowException_whenCustomerNotFound() {
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleService.create(testSaleData)
    );
  }

  @Test
  void create_shouldThrowException_whenEmployeeNotFound() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    Sale dummySale = Sale.builder()
      .id(1)
      .customer(testCustomer)
      .items(new ArrayList<>())
      .build();
    when(saleMapper.toEntity(any(SaleData.class))).thenReturn(dummySale);

    assertThrows(EntityNotFoundException.class, () ->
      saleService.create(testSaleData)
    );
  }

  @Test
  void create_shouldThrowException_whenProductNotFound() {
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleMapper.toEntity(testSaleData)).thenReturn(testSale);

    when(productRepository.findById(1)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> {
        productRepository
          .findById(1)
          .orElseThrow(() ->
            new EntityNotFoundException("Product not found with ID: 1")
          );
      }
    );

    assertEquals("Product not found with ID: 1", exception.getMessage());
  }

  @Test
  void create_shouldThrowException_whenInsufficientStock() {
    Product lowStockProduct = Product.builder()
      .id(1)
      .name("Low Stock Product")
      .stock(1)
      .build();

    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleMapper.toEntity(testSaleData)).thenReturn(testSale);
    when(productRepository.findById(1)).thenReturn(
      Optional.of(lowStockProduct)
    );

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> {
        Product product = productRepository.findById(1).get();
        if (product.getStock() < 2) {
          throw new IllegalArgumentException(
            "Insufficient stock for product: " + product.getName()
          );
        }
      }
    );

    assertEquals(
      "Insufficient stock for product: Low Stock Product",
      exception.getMessage()
    );
  }

  @Test
  void delete_shouldDeleteSaleAndAdjustStock() {
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    doNothing().when(saleRepository).delete(testSale);

    saleService.delete(1);

    verify(productRepository).save(testProduct);
    assertEquals(102, testProduct.getStock());
    verify(saleRepository).delete(testSale);
  }

  @Test
  void delete_shouldThrowException_whenSaleNotFound() {
    when(saleRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> saleService.delete(999));
  }

  @Test
  void deleteAllById_shouldDeleteMultipleSalesAndAdjustStock() {
    List<Integer> ids = Arrays.asList(1, 2);

    Sale sale2 = Sale.builder().id(2).build();
    SaleItem saleItem2 = SaleItem.builder()
      .product(testProduct)
      .quantity(3)
      .build();
    sale2.setItems(Collections.singletonList(saleItem2));
    saleItem2.setSale(sale2);

    when(saleRepository.existsById(1)).thenReturn(true);
    when(saleRepository.existsById(2)).thenReturn(true);
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(saleRepository.findById(2)).thenReturn(Optional.of(sale2));
    doNothing().when(saleRepository).deleteAllById(anyList());

    saleService.deleteAllById(ids);

    verify(saleRepository).deleteAllById(idsCaptor.capture());
    assertEquals(ids, idsCaptor.getValue());
    verify(productRepository, times(2)).save(testProduct);
    assertEquals(105, testProduct.getStock());
  }

  @Test
  void deleteAllById_shouldThrowException_whenAnySaleNotFound() {
    List<Integer> ids = Arrays.asList(1, 999);

    when(saleRepository.existsById(1)).thenReturn(true);
    when(saleRepository.existsById(999)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () ->
      saleService.deleteAllById(ids)
    );
    verify(saleRepository, never()).deleteAllById(anyList());
  }

  @Test
  void update_shouldThrowException_whenSaleNotFound() {
    when(saleRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleService.update(999, testSaleData)
    );
  }

  @Test
  void update_shouldAdjustStockCorrectly_whenQuantityChanges() {
    Product testProductForThisTest = Product.builder()
      .id(1)
      .name("Test Product")
      .stock(100)
      .build();

    SaleItem existingItem = SaleItem.builder()
      .id(1)
      .product(testProductForThisTest)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    List<SaleItem> items = new ArrayList<>();
    items.add(existingItem);

    Sale existingSale = Sale.builder()
      .id(1)
      .employee(testEmployee)
      .customer(testCustomer)
      .items(items)
      .build();

    existingItem.setSale(existingSale);

    SaleItemData updatedItemData = SaleItemData.builder()
      .productId(1)
      .quantity(5)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("250.00"))
      .build();

    SaleData updatedData = SaleData.builder()
      .totalAmount(new BigDecimal("250.00"))
      .taxAmount(new BigDecimal("47.50"))
      .finalAmount(new BigDecimal("297.50"))
      .customerId(1L)
      .employeeId(1L)
      .items(Collections.singletonList(updatedItemData))
      .build();

    when(saleRepository.findById(1)).thenReturn(Optional.of(existingSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(1)).thenReturn(
      Optional.of(testProductForThisTest)
    );
    when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

    when(saleRepository.findById(existingSale.getId())).thenReturn(
      Optional.of(existingSale)
    );

    saleService.update(1, updatedData);

    assertEquals(95, testProductForThisTest.getStock());
  }

  @Test
  void update_shouldAdjustStockCorrectly_whenProductChanges() {
    Product newProduct = Product.builder()
      .id(2)
      .name("New Product")
      .stock(20)
      .build();

    SaleItem existingItem = SaleItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    List<SaleItem> items = new ArrayList<>();
    items.add(existingItem);

    Sale existingSale = Sale.builder()
      .id(1)
      .employee(testEmployee)
      .customer(testCustomer)
      .items(items)
      .build();

    existingItem.setSale(existingSale);

    SaleItemData updatedItemData = SaleItemData.builder()
      .productId(2)
      .quantity(3)
      .unitPrice(new BigDecimal("40.00"))
      .subtotal(new BigDecimal("120.00"))
      .build();

    List<SaleItemData> updatedItems = new ArrayList<>();
    updatedItems.add(updatedItemData);

    SaleData updatedData = SaleData.builder()
      .totalAmount(new BigDecimal("120.00"))
      .taxAmount(new BigDecimal("22.80"))
      .finalAmount(new BigDecimal("142.80"))
      .customerId(1L)
      .employeeId(1L)
      .items(updatedItems)
      .build();

    when(saleRepository.findById(1)).thenReturn(Optional.of(existingSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(2)).thenReturn(Optional.of(newProduct));

    when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

    when(saleRepository.findById(existingSale.getId())).thenReturn(
      Optional.of(existingSale)
    );

    saleService.update(1, updatedData);

    assertEquals(102, testProduct.getStock());
    assertEquals(17, newProduct.getStock());
  }

  @Test
  void update_shouldHandleNewItems() {
    Product originalProduct = Product.builder()
      .id(1)
      .name("Original Product")
      .stock(100)
      .build();

    Product newProduct = Product.builder()
      .id(2)
      .name("New Product")
      .stock(20)
      .build();

    SaleItem existingItem = SaleItem.builder()
      .id(1)
      .product(originalProduct)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    List<SaleItem> items = new ArrayList<>();
    items.add(existingItem);

    Sale existingSale = Sale.builder()
      .id(1)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(items)
      .build();

    existingItem.setSale(existingSale);

    Sale savedSale = Sale.builder()
      .id(1)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(new ArrayList<>())
      .build();

    SaleItemData existingItemData = SaleItemData.builder()
      .productId(1)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    SaleItemData newItemData = SaleItemData.builder()
      .productId(2)
      .quantity(3)
      .unitPrice(new BigDecimal("40.00"))
      .subtotal(new BigDecimal("120.00"))
      .build();

    List<SaleItemData> updatedItems = new ArrayList<>();
    updatedItems.add(existingItemData);
    updatedItems.add(newItemData);

    SaleData updatedData = SaleData.builder()
      .totalAmount(new BigDecimal("220.00"))
      .taxAmount(new BigDecimal("41.80"))
      .finalAmount(new BigDecimal("261.80"))
      .customerId(1L)
      .employeeId(1L)
      .items(updatedItems)
      .build();

    when(saleRepository.findById(1)).thenReturn(Optional.of(existingSale));
    when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(1)).thenReturn(
      Optional.of(originalProduct)
    );
    when(productRepository.findById(2)).thenReturn(Optional.of(newProduct));
    lenient()
      .when(productRepository.save(any(Product.class)))
      .thenAnswer(i -> i.getArgument(0));

    when(saleRepository.findById(savedSale.getId())).thenReturn(
      Optional.of(savedSale)
    );

    saleService.update(1, updatedData);

    assertEquals(98, originalProduct.getStock());
    assertEquals(17, newProduct.getStock());
  }

  @Test
  void returnStockForRemovedItems_shouldNotAdjustStockWhenItemsMatch() {
    Product testProductForThisTest = Product.builder()
      .id(1)
      .name("Test Product")
      .stock(100)
      .build();

    SaleItem originalItem = SaleItem.builder()
      .id(1)
      .product(testProductForThisTest)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    SaleItemData newItemData = SaleItemData.builder()
      .productId(1)
      .quantity(2)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    lenient()
      .when(productRepository.findById(1))
      .thenReturn(Optional.of(testProductForThisTest));

    try {
      Method method =
        SaleService.class.getDeclaredMethod(
            "returnStockForRemovedItems",
            List.class,
            List.class
          );
      method.setAccessible(true);
      method.invoke(
        saleService,
        Collections.singletonList(originalItem),
        Collections.singletonList(newItemData)
      );

      assertEquals(100, testProductForThisTest.getStock());
      verify(productRepository, never()).save(any(Product.class));
    } catch (Exception e) {
      fail("Failed to invoke private method: " + e.getMessage());
    }
  }

  @Test
  void update_shouldThrowExceptionWhenCustomerNotFound() {
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleService.update(1, testSaleData)
    );
  }
}
