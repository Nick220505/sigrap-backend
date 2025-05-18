package com.sigrap.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private CustomerMapper customerMapper;

  @BeforeEach
  void setUp() {
    customerMapper = new CustomerMapper();
  }

  @Test
  void toCustomerInfo_shouldMapCustomerToCustomerInfo() {
    LocalDateTime now = LocalDateTime.now();

    Customer customer = Customer.builder()
      .id(1L)
      .fullName("Test Customer")
      .documentId("DOC123")
      .email("test@example.com")
      .phoneNumber("1234567890")
      .address("123 Test St")
      .createdAt(now)
      .updatedAt(now)
      .build();

    CustomerInfo customerInfo = customerMapper.toCustomerInfo(customer);

    assertThat(customerInfo).isNotNull();
    assertThat(customerInfo.getId()).isEqualTo(1L);
    assertThat(customerInfo.getFullName()).isEqualTo("Test Customer");
    assertThat(customerInfo.getDocumentId()).isEqualTo("DOC123");
    assertThat(customerInfo.getEmail()).isEqualTo("test@example.com");
    assertThat(customerInfo.getPhoneNumber()).isEqualTo("1234567890");
    assertThat(customerInfo.getAddress()).isEqualTo("123 Test St");
    assertThat(customerInfo.getCreatedAt()).isEqualTo(now);
    assertThat(customerInfo.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void toCustomerInfo_shouldReturnNull_whenCustomerIsNull() {
    CustomerInfo customerInfo = customerMapper.toCustomerInfo(null);

    assertThat(customerInfo).isNull();
  }

  @Test
  void toCustomerInfoList_shouldMapCustomerListToCustomerInfoList() {
    Customer customer1 = Customer.builder()
      .id(1L)
      .fullName("Customer 1")
      .email("customer1@example.com")
      .build();

    Customer customer2 = Customer.builder()
      .id(2L)
      .fullName("Customer 2")
      .email("customer2@example.com")
      .build();

    List<Customer> customers = List.of(customer1, customer2);

    List<CustomerInfo> customerInfos = customerMapper.toCustomerInfoList(
      customers
    );

    assertThat(customerInfos).hasSize(2);
    assertThat(customerInfos.get(0).getId()).isEqualTo(1L);
    assertThat(customerInfos.get(0).getFullName()).isEqualTo("Customer 1");
    assertThat(customerInfos.get(0).getEmail()).isEqualTo(
      "customer1@example.com"
    );
    assertThat(customerInfos.get(1).getId()).isEqualTo(2L);
    assertThat(customerInfos.get(1).getFullName()).isEqualTo("Customer 2");
    assertThat(customerInfos.get(1).getEmail()).isEqualTo(
      "customer2@example.com"
    );
  }

  @Test
  void toCustomerInfoList_shouldReturnEmptyList_whenCustomersIsNull() {
    List<CustomerInfo> customerInfos = customerMapper.toCustomerInfoList(null);

    assertThat(customerInfos).isNotNull();
    assertThat(customerInfos).isEmpty();
  }

  @Test
  void toCustomerInfoList_shouldReturnEmptyList_whenCustomersIsEmpty() {
    List<CustomerInfo> customerInfos = customerMapper.toCustomerInfoList(
      Collections.emptyList()
    );

    assertThat(customerInfos).isNotNull();
    assertThat(customerInfos).isEmpty();
  }

  @Test
  void toCustomer_shouldMapCustomerDataToCustomer() {
    CustomerData customerData = CustomerData.builder()
      .fullName("Test Customer")
      .documentId("DOC123")
      .email("test@example.com")
      .phoneNumber("1234567890")
      .address("123 Test St")
      .build();

    Customer customer = customerMapper.toCustomer(customerData);

    assertThat(customer).isNotNull();
    assertThat(customer.getId()).isNull();
    assertThat(customer.getFullName()).isEqualTo("Test Customer");
    assertThat(customer.getDocumentId()).isEqualTo("DOC123");
    assertThat(customer.getEmail()).isEqualTo("test@example.com");
    assertThat(customer.getPhoneNumber()).isEqualTo("1234567890");
    assertThat(customer.getAddress()).isEqualTo("123 Test St");
  }

  @Test
  void toCustomer_shouldReturnNull_whenCustomerDataIsNull() {
    Customer customer = customerMapper.toCustomer(null);

    assertThat(customer).isNull();
  }

  @Test
  void updateCustomerFromDto_shouldUpdateCustomerWithCustomerDataValues() {
    Customer customer = Customer.builder()
      .id(1L)
      .fullName("Original Name")
      .documentId("ORIGINAL_DOC")
      .email("original@example.com")
      .phoneNumber("0000000000")
      .address("Original Address")
      .build();

    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Name")
      .documentId("UPDATED_DOC")
      .email("updated@example.com")
      .phoneNumber("1111111111")
      .address("Updated Address")
      .build();

    customerMapper.updateCustomerFromDto(customer, customerData);

    assertThat(customer.getId()).isEqualTo(1L);
    assertThat(customer.getFullName()).isEqualTo("Updated Name");
    assertThat(customer.getDocumentId()).isEqualTo("UPDATED_DOC");
    assertThat(customer.getEmail()).isEqualTo("updated@example.com");
    assertThat(customer.getPhoneNumber()).isEqualTo("1111111111");
    assertThat(customer.getAddress()).isEqualTo("Updated Address");
  }

  @Test
  void updateCustomerFromDto_shouldDoNothing_whenCustomerIsNull() {
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Name")
      .build();

    customerMapper.updateCustomerFromDto(null, customerData);
  }

  @Test
  void updateCustomerFromDto_shouldDoNothing_whenCustomerDataIsNull() {
    Customer customer = Customer.builder()
      .id(1L)
      .fullName("Original Name")
      .documentId("ORIGINAL_DOC")
      .email("original@example.com")
      .build();

    Customer originalCustomer = Customer.builder()
      .id(1L)
      .fullName("Original Name")
      .documentId("ORIGINAL_DOC")
      .email("original@example.com")
      .build();

    customerMapper.updateCustomerFromDto(customer, null);

    assertThat(customer.getId()).isEqualTo(originalCustomer.getId());
    assertThat(customer.getFullName()).isEqualTo(
      originalCustomer.getFullName()
    );
    assertThat(customer.getDocumentId()).isEqualTo(
      originalCustomer.getDocumentId()
    );
    assertThat(customer.getEmail()).isEqualTo(originalCustomer.getEmail());
  }
}
