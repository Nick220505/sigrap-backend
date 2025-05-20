package com.sigrap.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleReturnServiceTest {

  @Mock
  private SaleReturnRepository saleReturnRepository;

  @Mock
  private SaleReturnItemRepository saleReturnItemRepository;

  @Mock
  private SaleRepository saleRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SaleReturnMapper saleReturnMapper;

  @InjectMocks
  private SaleReturnService saleReturnService;

  private Sale testSale;
  private Customer testCustomer;
  private User testEmployee;
  private Product testProduct;
  private SaleItem testSaleItem;
  private SaleReturn testSaleReturn;
  private SaleReturnItem testSaleReturnItem;
  private SaleReturnData testSaleReturnData;
  private SaleReturnInfo testSaleReturnInfo;

  @BeforeEach
  void setUp() {
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
      .quantity(10)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("500.00"))
      .build();

    List<SaleItem> saleItems = new ArrayList<>();
    saleItems.add(testSaleItem);

    testSale = Sale.builder()
      .id(1)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(saleItems)
      .totalAmount(new BigDecimal("500.00"))
      .taxAmount(new BigDecimal("95.00"))
      .finalAmount(new BigDecimal("595.00"))
      .build();

    testSaleItem.setSale(null);

    testSaleReturnItem = SaleReturnItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(5)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("250.00"))
      .build();

    testSaleReturn = SaleReturn.builder()
      .id(1)
      .originalSale(testSale)
      .customer(testCustomer)
      .employee(testEmployee)
      .totalReturnAmount(new BigDecimal("250.00"))
      .reason("Defective items")
      .items(new ArrayList<>(Collections.singletonList(testSaleReturnItem)))
      .build();
    testSaleReturnItem.setSaleReturn(testSaleReturn);

    SaleReturnItemData itemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(5)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("250.00"))
      .build();

    testSaleReturnData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("250.00"))
      .reason("Defective items")
      .items(Collections.singletonList(itemData))
      .build();

    testSaleReturnInfo = SaleReturnInfo.builder()
      .id(1)
      .originalSaleId(1)
      .totalReturnAmount(new BigDecimal("250.00"))
      .reason("Defective items")
      .build();
  }

  @Test
  void create_shouldCreateSaleReturn_whenValidData() {
    when(
      saleRepository.findById(testSaleReturnData.getOriginalSaleId())
    ).thenReturn(Optional.of(testSale));
    when(
      customerRepository.findById(testSaleReturnData.getCustomerId())
    ).thenReturn(Optional.of(testCustomer));
    when(
      userRepository.findById(testSaleReturnData.getEmployeeId())
    ).thenReturn(Optional.of(testEmployee));
    when(saleReturnMapper.toEntity(testSaleReturnData)).thenReturn(
      testSaleReturn
    );
    when(saleReturnRepository.save(testSaleReturn)).thenReturn(testSaleReturn);
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(saleReturnItemRepository.save(any(SaleReturnItem.class))).thenReturn(
      testSaleReturnItem
    );
    when(saleReturnRepository.findById(testSaleReturn.getId())).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleReturnMapper.toInfo(testSaleReturn)).thenReturn(
      testSaleReturnInfo
    );

    SaleReturnInfo result = saleReturnService.create(testSaleReturnData);

    assertEquals(testSaleReturnInfo, result);
    verify(productRepository, times(1)).save(testProduct);
    verify(saleReturnRepository, times(1)).save(testSaleReturn);
  }

  @Test
  void create_shouldThrowException_whenOriginalSaleNotFound() {
    when(
      saleRepository.findById(testSaleReturnData.getOriginalSaleId())
    ).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.create(testSaleReturnData)
    );
  }

  @Test
  void create_shouldThrowException_whenCustomerNotFound() {
    when(
      saleRepository.findById(testSaleReturnData.getOriginalSaleId())
    ).thenReturn(Optional.of(testSale));
    when(
      customerRepository.findById(testSaleReturnData.getCustomerId())
    ).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.create(testSaleReturnData)
    );
  }

  @Test
  void create_shouldThrowException_whenCustomerMismatch() {
    Customer differentCustomer = Customer.builder()
      .id(2L)
      .fullName("Different Customer")
      .build();

    when(
      saleRepository.findById(testSaleReturnData.getOriginalSaleId())
    ).thenReturn(Optional.of(testSale));
    when(
      customerRepository.findById(testSaleReturnData.getCustomerId())
    ).thenReturn(Optional.of(differentCustomer));

    assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.create(testSaleReturnData)
    );
  }

  @Test
  void delete_shouldDeleteAndAdjustStock() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    doNothing().when(saleReturnRepository).delete(testSaleReturn);

    saleReturnService.delete(1);

    verify(productRepository, times(1)).save(testProduct);
    assertEquals(95, testProduct.getStock());
    verify(saleReturnRepository, times(1)).delete(testSaleReturn);
  }

  @Test
  void delete_shouldThrowException_whenSaleReturnNotFound() {
    when(saleReturnRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.delete(999)
    );
  }

  @Test
  void deleteAllById_shouldDeleteMultipleAndAdjustStock() {
    List<Integer> ids = Arrays.asList(1, 2);

    SaleReturnItem saleReturnItem2 = SaleReturnItem.builder()
      .id(2)
      .product(testProduct)
      .quantity(3)
      .build();

    SaleReturn saleReturn2 = SaleReturn.builder()
      .id(2)
      .originalSale(testSale)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(new ArrayList<>(Collections.singletonList(saleReturnItem2)))
      .build();
    saleReturnItem2.setSaleReturn(saleReturn2);

    when(saleReturnRepository.existsById(1)).thenReturn(true);
    when(saleReturnRepository.existsById(2)).thenReturn(true);
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleReturnRepository.findById(2)).thenReturn(Optional.of(saleReturn2));
    doNothing().when(saleReturnRepository).deleteAllById(ids);

    saleReturnService.deleteAllById(ids);

    verify(saleReturnRepository, times(1)).deleteAllById(ids);
    verify(productRepository, times(2)).save(testProduct);
  }

  @Test
  void deleteAllById_shouldThrowException_whenAnySaleReturnNotFound() {
    List<Integer> ids = Arrays.asList(1, 999);

    when(saleReturnRepository.existsById(1)).thenReturn(true);
    when(saleReturnRepository.existsById(999)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.deleteAllById(ids)
    );
  }

  @Test
  void findById_shouldReturnSaleReturn_whenFound() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleReturnMapper.toInfo(testSaleReturn)).thenReturn(
      testSaleReturnInfo
    );

    SaleReturnInfo result = saleReturnService.findById(1);

    assertEquals(testSaleReturnInfo, result);
  }

  @Test
  void findById_shouldThrowException_whenNotFound() {
    when(saleReturnRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.findById(999)
    );
  }

  @Test
  void update_shouldUpdateSaleReturn_whenValidData() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleReturnRepository.save(testSaleReturn)).thenReturn(testSaleReturn);
    when(saleReturnMapper.toInfo(testSaleReturn)).thenReturn(
      testSaleReturnInfo
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      Collections.singletonList(testSaleReturnItem)
    );
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("200.00"))
      .reason("Updated reason")
      .items(
        Collections.singletonList(
          SaleReturnItemData.builder()
            .productId(1)
            .quantity(4)
            .unitPrice(new BigDecimal("50.00"))
            .subtotal(new BigDecimal("200.00"))
            .build()
        )
      )
      .build();

    SaleReturnInfo result = saleReturnService.update(1, updateData);

    assertEquals(testSaleReturnInfo, result);
  }

  @Test
  void update_shouldThrowException_whenChangingOriginalSale() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );

    Sale differentSale = Sale.builder().id(2).build();
    when(saleRepository.findById(2)).thenReturn(Optional.of(differentSale));

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(2)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("250.00"))
      .reason("Defective items")
      .items(
        Collections.singletonList(
          SaleReturnItemData.builder()
            .productId(1)
            .quantity(5)
            .unitPrice(new BigDecimal("50.00"))
            .subtotal(new BigDecimal("250.00"))
            .build()
        )
      )
      .build();

    assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.update(1, updateData)
    );
  }

  @Test
  void update_shouldThrowException_whenChangingCustomer() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));

    Customer differentCustomer = Customer.builder()
      .id(2L)
      .fullName("Different Customer")
      .build();
    when(customerRepository.findById(2L)).thenReturn(
      Optional.of(differentCustomer)
    );

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(2L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("250.00"))
      .reason("Defective items")
      .items(
        Collections.singletonList(
          SaleReturnItemData.builder()
            .productId(1)
            .quantity(5)
            .unitPrice(new BigDecimal("50.00"))
            .subtotal(new BigDecimal("250.00"))
            .build()
        )
      )
      .build();

    assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.update(1, updateData)
    );
  }

  @Test
  void findAll_shouldReturnAllSaleReturns() {
    List<SaleReturn> saleReturns = Arrays.asList(
      testSaleReturn,
      testSaleReturn
    );
    List<SaleReturnInfo> expected = Arrays.asList(
      testSaleReturnInfo,
      testSaleReturnInfo
    );

    when(saleReturnRepository.findAll()).thenReturn(saleReturns);
    when(saleReturnMapper.toInfoList(saleReturns)).thenReturn(expected);

    List<SaleReturnInfo> result = saleReturnService.findAll();

    assertEquals(expected, result);
  }

  @Test
  void findByOriginalSaleId_shouldReturnSaleReturnsForSale() {
    List<SaleReturn> saleSaleReturns = Arrays.asList(testSaleReturn);
    List<SaleReturnInfo> expected = Arrays.asList(testSaleReturnInfo);

    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(saleReturnRepository.findByOriginalSale(testSale)).thenReturn(
      saleSaleReturns
    );
    when(saleReturnMapper.toInfoList(saleSaleReturns)).thenReturn(expected);

    List<SaleReturnInfo> result = saleReturnService.findByOriginalSaleId(1);

    assertEquals(expected, result);
  }

  @Test
  void findByOriginalSaleId_shouldThrowException_whenSaleNotFound() {
    when(saleRepository.findById(999)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.findByOriginalSaleId(999)
    );
  }

  @Test
  void update_shouldThrowException_whenEmployeeNotFound() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      saleReturnService.update(1, testSaleReturnData)
    );
  }

  @Test
  void create_shouldThrowException_whenReturnQuantityExceedsPurchasedQuantity() {
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

    SaleReturn validReturn = SaleReturn.builder()
      .id(1)
      .originalSale(testSale)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(new ArrayList<>())
      .build();

    when(saleReturnRepository.save(any(SaleReturn.class))).thenReturn(
      validReturn
    );
    when(saleReturnMapper.toEntity(any(SaleReturnData.class))).thenReturn(
      validReturn
    );

    SaleReturnItemData invalidItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(20)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("1000.00"))
      .build();

    SaleReturnData invalidData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("1000.00"))
      .reason("Returning more than purchased")
      .items(Collections.singletonList(invalidItemData))
      .build();

    testSale.setItems(Collections.singletonList(testSaleItem));
    testSaleItem.setProduct(testProduct);
    testSaleItem.setQuantity(10);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.create(invalidData)
    );

    assertTrue(exception.getMessage().contains("Cannot return more items"));
  }

  @Test
  void create_shouldThrowException_whenProductWasNotInOriginalSale() {
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    SaleReturn validReturn = SaleReturn.builder()
      .id(1)
      .originalSale(testSale)
      .customer(testCustomer)
      .employee(testEmployee)
      .items(new ArrayList<>())
      .build();

    Product differentProduct = Product.builder()
      .id(2)
      .name("Different Product")
      .build();
    when(productRepository.findById(2)).thenReturn(
      Optional.of(differentProduct)
    );

    when(saleReturnRepository.save(any(SaleReturn.class))).thenReturn(
      validReturn
    );
    when(saleReturnMapper.toEntity(any(SaleReturnData.class))).thenReturn(
      validReturn
    );

    SaleReturnItemData invalidItemData = SaleReturnItemData.builder()
      .productId(2)
      .quantity(1)
      .unitPrice(new BigDecimal("100.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    SaleReturnData invalidData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("100.00"))
      .reason("Returning product not in original sale")
      .items(Collections.singletonList(invalidItemData))
      .build();

    testSale.setItems(Collections.singletonList(testSaleItem));
    testSaleItem.setProduct(testProduct);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.create(invalidData)
    );

    assertTrue(exception.getMessage().contains("was not in the original sale"));
  }

  @Test
  void update_shouldHandleReturnItemsUpdatesCorrectly() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

    List<SaleReturnItem> existingItems = Collections.singletonList(
      testSaleReturnItem
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      existingItems
    );

    SaleReturnItemData updatedItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(4)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("200.00"))
      .build();

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("200.00"))
      .reason("Updated reason")
      .items(Collections.singletonList(updatedItemData))
      .build();

    when(saleReturnRepository.save(any(SaleReturn.class))).thenReturn(
      testSaleReturn
    );
    when(saleReturnItemRepository.save(any(SaleReturnItem.class))).thenReturn(
      testSaleReturnItem
    );
    when(saleReturnMapper.toInfo(any(SaleReturn.class))).thenReturn(
      testSaleReturnInfo
    );

    saleReturnService.update(1, updateData);

    verify(productRepository).save(testProduct);
    assertEquals(4, testSaleReturnItem.getQuantity());
  }

  @Test
  void update_shouldHandleItemRemovalCorrectly() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    List<SaleReturnItem> existingItems = Collections.singletonList(
      testSaleReturnItem
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      existingItems
    );

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(BigDecimal.ZERO)
      .reason("Removed all items")
      .items(Collections.emptyList())
      .build();

    when(saleReturnRepository.save(any(SaleReturn.class))).thenReturn(
      testSaleReturn
    );
    when(saleReturnMapper.toInfo(any(SaleReturn.class))).thenReturn(
      testSaleReturnInfo
    );

    saleReturnService.update(1, updateData);

    verify(saleReturnItemRepository).delete(testSaleReturnItem);
    verify(productRepository).save(testProduct);
  }

  @Test
  void update_shouldHandleAddingNewItemCorrectly() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    Product product2 = Product.builder()
      .id(2)
      .name("Second Product")
      .stock(50)
      .build();

    SaleItem saleItem2 = SaleItem.builder()
      .id(2)
      .product(product2)
      .quantity(5)
      .unitPrice(new BigDecimal("30.00"))
      .subtotal(new BigDecimal("150.00"))
      .build();

    testSale.getItems().add(saleItem2);

    List<SaleReturnItem> existingItems = Collections.singletonList(
      testSaleReturnItem
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      existingItems
    );
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(productRepository.findById(2)).thenReturn(Optional.of(product2));

    SaleReturnItemData existingItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(5)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("250.00"))
      .build();

    SaleReturnItemData newItemData = SaleReturnItemData.builder()
      .productId(2)
      .quantity(2)
      .unitPrice(new BigDecimal("30.00"))
      .subtotal(new BigDecimal("60.00"))
      .build();

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("310.00"))
      .reason("Added new item")
      .items(Arrays.asList(existingItemData, newItemData))
      .build();

    when(saleReturnRepository.save(any(SaleReturn.class))).thenReturn(
      testSaleReturn
    );
    when(saleReturnItemRepository.save(any(SaleReturnItem.class))).thenReturn(
      testSaleReturnItem
    );
    when(saleReturnMapper.toInfo(any(SaleReturn.class))).thenReturn(
      testSaleReturnInfo
    );

    saleReturnService.update(1, updateData);

    verify(productRepository).save(product2);
    assertEquals(52, product2.getStock());
  }

  @Test
  void update_shouldThrowException_whenNewItemProductNotInOriginalSale() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

    Product newProduct = Product.builder()
      .id(3)
      .name("Not In Sale Product")
      .stock(30)
      .build();

    List<SaleReturnItem> existingItems = Collections.singletonList(
      testSaleReturnItem
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      existingItems
    );
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(productRepository.findById(3)).thenReturn(Optional.of(newProduct));

    SaleReturnItemData existingItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(5)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("250.00"))
      .build();

    SaleReturnItemData invalidItemData = SaleReturnItemData.builder()
      .productId(3)
      .quantity(2)
      .unitPrice(new BigDecimal("40.00"))
      .subtotal(new BigDecimal("80.00"))
      .build();

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("330.00"))
      .reason("Invalid item added")
      .items(Arrays.asList(existingItemData, invalidItemData))
      .build();

    assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.update(1, updateData)
    );
  }

  @Test
  void update_shouldThrowException_whenNewItemQuantityExceedsPurchasedQuantity() {
    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

    List<SaleReturnItem> existingItems = Collections.singletonList(
      testSaleReturnItem
    );
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      existingItems
    );

    SaleReturnItemData invalidItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(15)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("750.00"))
      .build();

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .totalReturnAmount(new BigDecimal("750.00"))
      .reason("Excessive quantity")
      .items(Collections.singletonList(invalidItemData))
      .build();

    assertThrows(IllegalArgumentException.class, () ->
      saleReturnService.update(1, updateData)
    );
  }

  @Test
  void processReturnItems_shouldCreateNewItemsCorrectly() {
    when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
    when(saleReturnItemRepository.save(any(SaleReturnItem.class))).thenReturn(
      testSaleReturnItem
    );

    testSaleReturn.setItems(null);

    SaleReturnItemData itemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(3)
      .unitPrice(new BigDecimal("50.00"))
      .subtotal(new BigDecimal("150.00"))
      .build();

    testSale.setItems(Collections.singletonList(testSaleItem));
    testSaleItem.setProduct(testProduct);
    testSaleItem.setQuantity(10);

    when(
      saleRepository.findById(testSaleReturnData.getOriginalSaleId())
    ).thenReturn(Optional.of(testSale));
    when(
      customerRepository.findById(testSaleReturnData.getCustomerId())
    ).thenReturn(Optional.of(testCustomer));
    when(
      userRepository.findById(testSaleReturnData.getEmployeeId())
    ).thenReturn(Optional.of(testEmployee));
    when(saleReturnMapper.toEntity(testSaleReturnData)).thenReturn(
      testSaleReturn
    );
    when(saleReturnRepository.save(testSaleReturn)).thenReturn(testSaleReturn);
    when(saleReturnRepository.findById(testSaleReturn.getId())).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleReturnMapper.toInfo(testSaleReturn)).thenReturn(
      testSaleReturnInfo
    );

    saleReturnService.create(testSaleReturnData);

    verify(productRepository).save(testProduct);
    assertTrue(
      testSaleReturn.getItems() != null && !testSaleReturn.getItems().isEmpty()
    );
  }

  @Test
  void calculateTotalReturnAmount_shouldCalculateCorrectly() {
    SaleReturnItem item1 = SaleReturnItem.builder()
      .id(1)
      .subtotal(new BigDecimal("100.00"))
      .build();

    SaleReturnItem item2 = SaleReturnItem.builder()
      .id(2)
      .subtotal(new BigDecimal("150.00"))
      .build();

    SaleReturnItem item3 = SaleReturnItem.builder()
      .id(3)
      .subtotal(new BigDecimal("75.50"))
      .build();

    List<SaleReturnItem> items = Arrays.asList(item1, item2, item3);
    testSaleReturn.setItems(items);

    when(saleReturnRepository.save(testSaleReturn)).thenReturn(testSaleReturn);
    when(saleReturnMapper.toInfo(testSaleReturn)).thenReturn(
      testSaleReturnInfo
    );

    SaleReturnData updateData = SaleReturnData.builder()
      .originalSaleId(1)
      .customerId(1L)
      .employeeId(1L)
      .reason("Testing total calculation")
      .items(Collections.emptyList())
      .build();

    when(saleReturnRepository.findById(1)).thenReturn(
      Optional.of(testSaleReturn)
    );
    when(saleRepository.findById(1)).thenReturn(Optional.of(testSale));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(userRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(saleReturnItemRepository.findBySaleReturn(testSaleReturn)).thenReturn(
      Collections.emptyList()
    );

    saleReturnService.update(1, updateData);

    verify(saleReturnRepository).save(testSaleReturn);
    assertEquals(
      new BigDecimal("325.50"),
      testSaleReturn.getTotalReturnAmount()
    );
  }
}
