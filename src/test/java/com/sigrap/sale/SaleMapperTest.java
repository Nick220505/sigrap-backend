package com.sigrap.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerInfo;
import com.sigrap.customer.CustomerMapper;
import com.sigrap.product.Product;
import com.sigrap.product.ProductInfo;
import com.sigrap.product.ProductMapper;
import com.sigrap.user.User;
import com.sigrap.user.UserInfo;
import com.sigrap.user.UserMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleMapperTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private CustomerMapper customerMapper;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private SaleMapper saleMapper;

  private Sale testSale;
  private SaleItem testSaleItem;
  private Customer testCustomer;
  private User testEmployee;
  private Product testProduct;
  private CustomerInfo testCustomerInfo;
  private UserInfo testUserInfo;
  private ProductInfo testProductInfo;
  private SaleData testSaleData;
  private SaleItemData testSaleItemData;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    testDateTime = LocalDateTime.now();

    testProduct = Product.builder().id(1).name("Test Product").build();

    testProductInfo = ProductInfo.builder().id(1).name("Test Product").build();

    testEmployee = User.builder().id(1L).name("Test Employee").build();

    testUserInfo = UserInfo.builder().id(1L).name("Test Employee").build();

    testCustomer = Customer.builder().id(1L).fullName("Test Customer").build();

    testCustomerInfo = CustomerInfo.builder()
      .id(1L)
      .fullName("Test Customer")
      .build();

    testSaleItem = SaleItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(5)
      .unitPrice(new BigDecimal("20.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    testSale = Sale.builder()
      .id(1)
      .totalAmount(new BigDecimal("100.00"))
      .taxAmount(new BigDecimal("19.00"))
      .discountAmount(new BigDecimal("5.00"))
      .finalAmount(new BigDecimal("114.00"))
      .customer(testCustomer)
      .employee(testEmployee)
      .items(new ArrayList<>(Collections.singletonList(testSaleItem)))
      .createdAt(testDateTime)
      .updatedAt(testDateTime)
      .build();
    testSaleItem.setSale(testSale);

    testSaleItemData = SaleItemData.builder()
      .productId(1)
      .quantity(5)
      .unitPrice(new BigDecimal("20.00"))
      .subtotal(new BigDecimal("100.00"))
      .build();

    testSaleData = SaleData.builder()
      .totalAmount(new BigDecimal("100.00"))
      .taxAmount(new BigDecimal("19.00"))
      .discountAmount(new BigDecimal("5.00"))
      .finalAmount(new BigDecimal("114.00"))
      .customerId(1L)
      .employeeId(1L)
      .items(Collections.singletonList(testSaleItemData))
      .build();
  }

  @Test
  void toInfo_shouldMapSaleToSaleInfo() {
    // Arrange
    when(customerMapper.toCustomerInfo(testCustomer)).thenReturn(
      testCustomerInfo
    );
    when(userMapper.toInfo(testEmployee)).thenReturn(testUserInfo);
    when(productMapper.toInfo(testProduct)).thenReturn(testProductInfo);

    // Act
    SaleInfo result = saleMapper.toInfo(testSale);

    // Assert
    assertNotNull(result);
    assertEquals(testSale.getId(), result.getId());
    assertEquals(testSale.getTotalAmount(), result.getTotalAmount());
    assertEquals(testSale.getTaxAmount(), result.getTaxAmount());
    assertEquals(testSale.getDiscountAmount(), result.getDiscountAmount());
    assertEquals(testSale.getFinalAmount(), result.getFinalAmount());
    assertEquals(testCustomerInfo, result.getCustomer());
    assertEquals(testUserInfo, result.getEmployee());
    assertEquals(testSale.getCreatedAt(), result.getCreatedAt());
    assertEquals(testSale.getUpdatedAt(), result.getUpdatedAt());
    assertEquals(1, result.getItems().size());
  }

  @Test
  void toInfo_shouldReturnNull_whenSaleIsNull() {
    // Act
    SaleInfo result = saleMapper.toInfo(null);

    // Assert
    assertNull(result);
  }

  @Test
  void toInfoList_shouldMapSaleListToSaleInfoList() {
    // Arrange
    List<Sale> sales = Arrays.asList(testSale, testSale);

    when(customerMapper.toCustomerInfo(any(Customer.class))).thenReturn(
      testCustomerInfo
    );
    when(userMapper.toInfo(any(User.class))).thenReturn(testUserInfo);
    when(productMapper.toInfo(any(Product.class))).thenReturn(testProductInfo);

    // Act
    List<SaleInfo> result = saleMapper.toInfoList(sales);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void toInfoList_shouldReturnEmptyList_whenSalesIsNull() {
    // Act
    List<SaleInfo> result = saleMapper.toInfoList(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void toSaleItemInfo_shouldMapSaleItemToSaleItemInfo() {
    // Arrange
    when(productMapper.toInfo(testProduct)).thenReturn(testProductInfo);

    // Act
    SaleItemInfo result = saleMapper.toSaleItemInfo(testSaleItem);

    // Assert
    assertNotNull(result);
    assertEquals(testSaleItem.getId(), result.getId());
    assertEquals(testProductInfo, result.getProduct());
    assertEquals(testSaleItem.getQuantity(), result.getQuantity());
    assertEquals(testSaleItem.getUnitPrice(), result.getUnitPrice());
    assertEquals(testSaleItem.getSubtotal(), result.getSubtotal());
  }

  @Test
  void toSaleItemInfo_shouldReturnNull_whenSaleItemIsNull() {
    // Act
    SaleItemInfo result = saleMapper.toSaleItemInfo(null);

    // Assert
    assertNull(result);
  }

  @Test
  void toSaleItemInfoList_shouldMapSaleItemListToSaleItemInfoList() {
    // Arrange
    List<SaleItem> saleItems = Arrays.asList(testSaleItem, testSaleItem);

    when(productMapper.toInfo(any(Product.class))).thenReturn(testProductInfo);

    // Act
    List<SaleItemInfo> result = saleMapper.toSaleItemInfoList(saleItems);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void toSaleItemInfoList_shouldReturnEmptyList_whenSaleItemsIsNull() {
    // Act
    List<SaleItemInfo> result = saleMapper.toSaleItemInfoList(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void toEntity_shouldMapSaleDataToSale() {
    // Act
    Sale result = saleMapper.toEntity(testSaleData);

    // Assert
    assertNotNull(result);
    assertEquals(testSaleData.getTotalAmount(), result.getTotalAmount());
    assertEquals(testSaleData.getTaxAmount(), result.getTaxAmount());
    assertEquals(testSaleData.getDiscountAmount(), result.getDiscountAmount());
    assertEquals(testSaleData.getFinalAmount(), result.getFinalAmount());
    assertNull(result.getCustomer());
    assertNull(result.getEmployee());
  }

  @Test
  void toEntity_shouldReturnNull_whenSaleDataIsNull() {
    // Act
    Sale result = saleMapper.toEntity(null);

    // Assert
    assertNull(result);
  }

  @Test
  void toSaleItemEntity_shouldMapSaleItemDataToSaleItem() {
    // Act
    SaleItem result = saleMapper.toSaleItemEntity(testSaleItemData);

    // Assert
    assertNotNull(result);
    assertEquals(testSaleItemData.getQuantity(), result.getQuantity());
    assertEquals(testSaleItemData.getUnitPrice(), result.getUnitPrice());
    assertEquals(testSaleItemData.getSubtotal(), result.getSubtotal());
    assertNull(result.getProduct());
    assertNull(result.getSale());
  }

  @Test
  void toSaleItemEntity_shouldReturnNull_whenSaleItemDataIsNull() {
    // Act
    SaleItem result = saleMapper.toSaleItemEntity(null);

    // Assert
    assertNull(result);
  }

  @Test
  void toSaleItemEntityList_shouldMapSaleItemDataListToSaleItemList() {
    // Arrange
    List<SaleItemData> saleItemDataList = Arrays.asList(
      testSaleItemData,
      testSaleItemData
    );
    IntFunction<Product> getProduct = id -> testProduct;

    // Act
    List<SaleItem> result = saleMapper.toSaleItemEntityList(
      saleItemDataList,
      testSale,
      getProduct
    );

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    for (SaleItem item : result) {
      assertEquals(testSale, item.getSale());
      assertEquals(testProduct, item.getProduct());
      assertEquals(testSaleItemData.getQuantity(), item.getQuantity());
    }
  }

  @Test
  void toSaleItemEntityList_shouldReturnEmptyList_whenSaleItemDataListIsNull() {
    // Arrange
    IntFunction<Product> getProduct = id -> testProduct;

    // Act
    List<SaleItem> result = saleMapper.toSaleItemEntityList(
      null,
      testSale,
      getProduct
    );

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void updateEntityFromData_shouldUpdateSaleWithSaleData() {
    // Arrange
    Sale saleToUpdate = Sale.builder()
      .totalAmount(BigDecimal.ONE)
      .taxAmount(BigDecimal.ONE)
      .discountAmount(BigDecimal.ONE)
      .finalAmount(BigDecimal.ONE)
      .build();

    // Act
    saleMapper.updateEntityFromData(saleToUpdate, testSaleData);

    // Assert
    assertEquals(testSaleData.getTotalAmount(), saleToUpdate.getTotalAmount());
    assertEquals(testSaleData.getTaxAmount(), saleToUpdate.getTaxAmount());
    assertEquals(
      testSaleData.getDiscountAmount(),
      saleToUpdate.getDiscountAmount()
    );
    assertEquals(testSaleData.getFinalAmount(), saleToUpdate.getFinalAmount());
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenSaleIsNull() {
    // This should not throw any exception
    saleMapper.updateEntityFromData(null, testSaleData);
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenSaleDataIsNull() {
    // Arrange
    Sale originalSale = Sale.builder()
      .totalAmount(BigDecimal.ONE)
      .taxAmount(BigDecimal.ONE)
      .discountAmount(BigDecimal.ONE)
      .finalAmount(BigDecimal.ONE)
      .build();
    Sale saleToUpdate = Sale.builder()
      .totalAmount(BigDecimal.ONE)
      .taxAmount(BigDecimal.ONE)
      .discountAmount(BigDecimal.ONE)
      .finalAmount(BigDecimal.ONE)
      .build();

    // Act
    saleMapper.updateEntityFromData(saleToUpdate, null);

    // Assert - sale should not be changed
    assertEquals(originalSale.getTotalAmount(), saleToUpdate.getTotalAmount());
    assertEquals(originalSale.getTaxAmount(), saleToUpdate.getTaxAmount());
    assertEquals(
      originalSale.getDiscountAmount(),
      saleToUpdate.getDiscountAmount()
    );
    assertEquals(originalSale.getFinalAmount(), saleToUpdate.getFinalAmount());
  }

  @Test
  void setCustomerAndEmployee_shouldUpdateSaleWithCustomerAndEmployee() {
    // Arrange
    Sale saleToUpdate = Sale.builder().build();

    // Act
    saleMapper.setCustomerAndEmployee(saleToUpdate, testCustomer, testEmployee);

    // Assert
    assertEquals(testCustomer, saleToUpdate.getCustomer());
    assertEquals(testEmployee, saleToUpdate.getEmployee());
  }

  @Test
  void setCustomerAndEmployee_shouldDoNothing_whenSaleIsNull() {
    // This should not throw any exception
    saleMapper.setCustomerAndEmployee(null, testCustomer, testEmployee);
  }
}
