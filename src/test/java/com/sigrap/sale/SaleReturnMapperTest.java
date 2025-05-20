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
class SaleReturnMapperTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private CustomerMapper customerMapper;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private SaleReturnMapper saleReturnMapper;

  private SaleReturn testSaleReturn;
  private SaleReturnItem testSaleReturnItem;
  private Sale testSale;
  private Customer testCustomer;
  private User testEmployee;
  private Product testProduct;
  private CustomerInfo testCustomerInfo;
  private UserInfo testUserInfo;
  private ProductInfo testProductInfo;
  private SaleReturnData testSaleReturnData;
  private SaleReturnItemData testSaleReturnItemData;
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

    testSale = Sale.builder().id(1).build();

    testSaleReturnItem = SaleReturnItem.builder()
      .id(1)
      .product(testProduct)
      .quantity(3)
      .unitPrice(new BigDecimal("20.00"))
      .subtotal(new BigDecimal("60.00"))
      .build();

    testSaleReturn = SaleReturn.builder()
      .id(1)
      .originalSale(testSale)
      .totalReturnAmount(new BigDecimal("60.00"))
      .customer(testCustomer)
      .employee(testEmployee)
      .reason("Defective items")
      .items(new ArrayList<>(Collections.singletonList(testSaleReturnItem)))
      .createdAt(testDateTime)
      .updatedAt(testDateTime)
      .build();
    testSaleReturnItem.setSaleReturn(testSaleReturn);

    testSaleReturnItemData = SaleReturnItemData.builder()
      .productId(1)
      .quantity(3)
      .unitPrice(new BigDecimal("20.00"))
      .subtotal(new BigDecimal("60.00"))
      .build();

    testSaleReturnData = SaleReturnData.builder()
      .originalSaleId(1)
      .totalReturnAmount(new BigDecimal("60.00"))
      .customerId(1L)
      .employeeId(1L)
      .reason("Defective items")
      .items(Collections.singletonList(testSaleReturnItemData))
      .build();
  }

  @Test
  void toInfo_shouldMapSaleReturnToSaleReturnInfo() {
    when(customerMapper.toCustomerInfo(testCustomer)).thenReturn(
      testCustomerInfo
    );
    when(userMapper.toInfo(testEmployee)).thenReturn(testUserInfo);
    when(productMapper.toInfo(testProduct)).thenReturn(testProductInfo);

    SaleReturnInfo result = saleReturnMapper.toInfo(testSaleReturn);

    assertNotNull(result);
    assertEquals(testSaleReturn.getId(), result.getId());
    assertEquals(testSale.getId(), result.getOriginalSaleId());
    assertEquals(
      testSaleReturn.getTotalReturnAmount(),
      result.getTotalReturnAmount()
    );
    assertEquals(testCustomerInfo, result.getCustomer());
    assertEquals(testUserInfo, result.getEmployee());
    assertEquals(testSaleReturn.getReason(), result.getReason());
    assertEquals(testSaleReturn.getCreatedAt(), result.getCreatedAt());
    assertEquals(testSaleReturn.getUpdatedAt(), result.getUpdatedAt());
    assertEquals(1, result.getItems().size());
  }

  @Test
  void toInfo_shouldReturnNull_whenSaleReturnIsNull() {
    SaleReturnInfo result = saleReturnMapper.toInfo(null);

    assertNull(result);
  }

  @Test
  void toInfo_shouldHandleNullOriginalSale() {
    testSaleReturn.setOriginalSale(null);
    when(customerMapper.toCustomerInfo(testCustomer)).thenReturn(
      testCustomerInfo
    );
    when(userMapper.toInfo(testEmployee)).thenReturn(testUserInfo);
    when(productMapper.toInfo(testProduct)).thenReturn(testProductInfo);

    SaleReturnInfo result = saleReturnMapper.toInfo(testSaleReturn);

    assertNotNull(result);
    assertNull(result.getOriginalSaleId());
  }

  @Test
  void toInfoList_shouldMapSaleReturnListToSaleReturnInfoList() {
    List<SaleReturn> saleReturns = Arrays.asList(
      testSaleReturn,
      testSaleReturn
    );

    when(customerMapper.toCustomerInfo(any(Customer.class))).thenReturn(
      testCustomerInfo
    );
    when(userMapper.toInfo(any(User.class))).thenReturn(testUserInfo);
    when(productMapper.toInfo(any(Product.class))).thenReturn(testProductInfo);

    List<SaleReturnInfo> result = saleReturnMapper.toInfoList(saleReturns);

    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void toInfoList_shouldReturnEmptyList_whenSaleReturnsIsNull() {
    List<SaleReturnInfo> result = saleReturnMapper.toInfoList(null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void toSaleReturnItemInfo_shouldMapSaleReturnItemToSaleReturnItemInfo() {
    when(productMapper.toInfo(testProduct)).thenReturn(testProductInfo);

    SaleReturnItemInfo result = saleReturnMapper.toSaleReturnItemInfo(
      testSaleReturnItem
    );

    assertNotNull(result);
    assertEquals(testSaleReturnItem.getId(), result.getId());
    assertEquals(testProductInfo, result.getProduct());
    assertEquals(testSaleReturnItem.getQuantity(), result.getQuantity());
    assertEquals(testSaleReturnItem.getUnitPrice(), result.getUnitPrice());
    assertEquals(testSaleReturnItem.getSubtotal(), result.getSubtotal());
  }

  @Test
  void toSaleReturnItemInfo_shouldReturnNull_whenSaleReturnItemIsNull() {
    SaleReturnItemInfo result = saleReturnMapper.toSaleReturnItemInfo(null);

    assertNull(result);
  }

  @Test
  void toSaleReturnItemInfoList_shouldMapSaleReturnItemListToSaleReturnItemInfoList() {
    List<SaleReturnItem> saleReturnItems = Arrays.asList(
      testSaleReturnItem,
      testSaleReturnItem
    );

    when(productMapper.toInfo(any(Product.class))).thenReturn(testProductInfo);

    List<SaleReturnItemInfo> result = saleReturnMapper.toSaleReturnItemInfoList(
      saleReturnItems
    );

    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void toSaleReturnItemInfoList_shouldReturnEmptyList_whenSaleReturnItemsIsNull() {
    List<SaleReturnItemInfo> result = saleReturnMapper.toSaleReturnItemInfoList(
      null
    );

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void toEntity_shouldMapSaleReturnDataToSaleReturn() {
    SaleReturn result = saleReturnMapper.toEntity(testSaleReturnData);

    assertNotNull(result);
    assertEquals(
      testSaleReturnData.getTotalReturnAmount(),
      result.getTotalReturnAmount()
    );
    assertEquals(testSaleReturnData.getReason(), result.getReason());
    assertNull(result.getOriginalSale());
    assertNull(result.getCustomer());
    assertNull(result.getEmployee());
  }

  @Test
  void toEntity_shouldReturnNull_whenSaleReturnDataIsNull() {
    SaleReturn result = saleReturnMapper.toEntity(null);

    assertNull(result);
  }

  @Test
  void toSaleReturnItemEntity_shouldMapSaleReturnItemDataToSaleReturnItem() {
    SaleReturnItem result = saleReturnMapper.toSaleReturnItemEntity(
      testSaleReturnItemData
    );

    assertNotNull(result);
    assertEquals(testSaleReturnItemData.getQuantity(), result.getQuantity());
    assertEquals(testSaleReturnItemData.getUnitPrice(), result.getUnitPrice());
    assertEquals(testSaleReturnItemData.getSubtotal(), result.getSubtotal());
    assertNull(result.getProduct());
    assertNull(result.getSaleReturn());
  }

  @Test
  void toSaleReturnItemEntity_shouldReturnNull_whenSaleReturnItemDataIsNull() {
    SaleReturnItem result = saleReturnMapper.toSaleReturnItemEntity(null);

    assertNull(result);
  }

  @Test
  void toSaleReturnItemEntityList_shouldMapSaleReturnItemDataListToSaleReturnItemList() {
    List<SaleReturnItemData> saleReturnItemDataList = Arrays.asList(
      testSaleReturnItemData,
      testSaleReturnItemData
    );
    IntFunction<Product> getProduct = id -> testProduct;

    List<SaleReturnItem> result = saleReturnMapper.toSaleReturnItemEntityList(
      saleReturnItemDataList,
      testSaleReturn,
      getProduct
    );

    assertNotNull(result);
    assertEquals(2, result.size());
    for (SaleReturnItem item : result) {
      assertEquals(testSaleReturn, item.getSaleReturn());
      assertEquals(testProduct, item.getProduct());
      assertEquals(testSaleReturnItemData.getQuantity(), item.getQuantity());
    }
  }

  @Test
  void toSaleReturnItemEntityList_shouldReturnEmptyList_whenSaleReturnItemDataListIsNull() {
    IntFunction<Product> getProduct = id -> testProduct;

    List<SaleReturnItem> result = saleReturnMapper.toSaleReturnItemEntityList(
      null,
      testSaleReturn,
      getProduct
    );

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void updateEntityFromData_shouldUpdateSaleReturnWithSaleReturnData() {
    SaleReturn saleReturnToUpdate = SaleReturn.builder()
      .totalReturnAmount(BigDecimal.ONE)
      .reason("Old reason")
      .build();

    saleReturnMapper.updateEntityFromData(
      saleReturnToUpdate,
      testSaleReturnData
    );

    assertEquals(
      testSaleReturnData.getTotalReturnAmount(),
      saleReturnToUpdate.getTotalReturnAmount()
    );
    assertEquals(
      testSaleReturnData.getReason(),
      saleReturnToUpdate.getReason()
    );
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenSaleReturnIsNull() {
    saleReturnMapper.updateEntityFromData(null, testSaleReturnData);
  }

  @Test
  void updateEntityFromData_shouldDoNothing_whenSaleReturnDataIsNull() {
    BigDecimal originalAmount = BigDecimal.ONE;
    String originalReason = "Original reason";

    SaleReturn saleReturnToUpdate = SaleReturn.builder()
      .totalReturnAmount(originalAmount)
      .reason(originalReason)
      .build();

    saleReturnMapper.updateEntityFromData(saleReturnToUpdate, null);

    assertEquals(originalAmount, saleReturnToUpdate.getTotalReturnAmount());
    assertEquals(originalReason, saleReturnToUpdate.getReason());
  }
}
