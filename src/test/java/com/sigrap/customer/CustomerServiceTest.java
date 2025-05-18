package com.sigrap.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerMapper customerMapper;

  @InjectMocks
  private CustomerService customerService;

  @Test
  void findAll_shouldReturnAllCustomers() {
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

    CustomerInfo customerInfo1 = CustomerInfo.builder()
      .id(1L)
      .fullName("Customer 1")
      .email("customer1@example.com")
      .build();

    CustomerInfo customerInfo2 = CustomerInfo.builder()
      .id(2L)
      .fullName("Customer 2")
      .email("customer2@example.com")
      .build();

    List<CustomerInfo> customerInfos = List.of(customerInfo1, customerInfo2);

    when(customerRepository.findAll()).thenReturn(customers);
    when(customerMapper.toCustomerInfoList(customers)).thenReturn(
      customerInfos
    );

    List<CustomerInfo> result = customerService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrderElementsOf(customerInfos);
  }

  @Test
  void findById_shouldReturnCustomer_whenExists() {
    Long id = 1L;
    Customer customer = Customer.builder()
      .id(id)
      .fullName("Test Customer")
      .email("test@example.com")
      .build();

    CustomerInfo customerInfo = CustomerInfo.builder()
      .id(id)
      .fullName("Test Customer")
      .email("test@example.com")
      .build();

    when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
    when(customerMapper.toCustomerInfo(customer)).thenReturn(customerInfo);

    CustomerInfo result = customerService.findById(id);

    assertThat(result).isEqualTo(customerInfo);
  }

  @Test
  void findById_shouldThrowException_whenNotExists() {
    Long id = 1L;
    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> customerService.findById(id)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Customer not found with ID: " + id
    );
  }

  @Test
  void create_shouldCreateCustomer_whenEmailNotExists() {
    CustomerData customerData = CustomerData.builder()
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .build();

    Customer customer = Customer.builder()
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .build();

    Customer savedCustomer = Customer.builder()
      .id(1L)
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .build();

    CustomerInfo customerInfo = CustomerInfo.builder()
      .id(1L)
      .fullName("New Customer")
      .email("new@example.com")
      .phoneNumber("1234567890")
      .build();

    when(customerRepository.existsByEmail(customerData.getEmail())).thenReturn(
      false
    );
    when(customerMapper.toCustomer(customerData)).thenReturn(customer);
    when(customerRepository.save(customer)).thenReturn(savedCustomer);
    when(customerMapper.toCustomerInfo(savedCustomer)).thenReturn(customerInfo);

    CustomerInfo result = customerService.create(customerData);

    assertThat(result).isEqualTo(customerInfo);
    verify(customerRepository).save(customer);
  }

  @Test
  void create_shouldThrowException_whenEmailExists() {
    CustomerData customerData = CustomerData.builder()
      .fullName("New Customer")
      .email("existing@example.com")
      .build();

    when(customerRepository.existsByEmail(customerData.getEmail())).thenReturn(
      true
    );

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> customerService.create(customerData)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Customer with email already exists: " + customerData.getEmail()
    );
    verify(customerRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateCustomer_whenExistsWithSameEmail() {
    Long id = 1L;
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Customer")
      .email("existing@example.com")
      .phoneNumber("9876543210")
      .build();

    Customer existingCustomer = Customer.builder()
      .id(id)
      .fullName("Existing Customer")
      .email("existing@example.com")
      .build();

    Customer updatedCustomer = Customer.builder()
      .id(id)
      .fullName("Updated Customer")
      .email("existing@example.com")
      .phoneNumber("9876543210")
      .build();

    CustomerInfo customerInfo = CustomerInfo.builder()
      .id(id)
      .fullName("Updated Customer")
      .email("existing@example.com")
      .phoneNumber("9876543210")
      .build();

    when(customerRepository.findById(id)).thenReturn(
      Optional.of(existingCustomer)
    );
    when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomer);
    when(customerMapper.toCustomerInfo(updatedCustomer)).thenReturn(
      customerInfo
    );

    CustomerInfo result = customerService.update(id, customerData);

    assertThat(result).isEqualTo(customerInfo);
    verify(customerMapper).updateCustomerFromDto(
      existingCustomer,
      customerData
    );
    verify(customerRepository).save(existingCustomer);
  }

  @Test
  void update_shouldUpdateCustomer_whenExistsWithNewUnusedEmail() {
    Long id = 1L;
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Customer")
      .email("new@example.com")
      .phoneNumber("9876543210")
      .build();

    Customer existingCustomer = Customer.builder()
      .id(id)
      .fullName("Existing Customer")
      .email("existing@example.com")
      .build();

    Customer updatedCustomer = Customer.builder()
      .id(id)
      .fullName("Updated Customer")
      .email("new@example.com")
      .phoneNumber("9876543210")
      .build();

    CustomerInfo customerInfo = CustomerInfo.builder()
      .id(id)
      .fullName("Updated Customer")
      .email("new@example.com")
      .phoneNumber("9876543210")
      .build();

    when(customerRepository.findById(id)).thenReturn(
      Optional.of(existingCustomer)
    );
    when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomer);
    when(customerMapper.toCustomerInfo(updatedCustomer)).thenReturn(
      customerInfo
    );

    CustomerInfo result = customerService.update(id, customerData);

    assertThat(result).isEqualTo(customerInfo);
    verify(customerMapper).updateCustomerFromDto(
      existingCustomer,
      customerData
    );
    verify(customerRepository).save(existingCustomer);
  }

  @Test
  void update_shouldThrowException_whenEmailTaken() {
    Long id = 1L;
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Customer")
      .email("taken@example.com")
      .build();

    Customer existingCustomer = Customer.builder()
      .id(id)
      .fullName("Existing Customer")
      .email("existing@example.com")
      .build();

    when(customerRepository.findById(id)).thenReturn(
      Optional.of(existingCustomer)
    );
    when(customerRepository.existsByEmail("taken@example.com")).thenReturn(
      true
    );

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> customerService.update(id, customerData)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Email already in use by another customer: " + customerData.getEmail()
    );
    verify(customerRepository, never()).save(any());
  }

  @Test
  void update_shouldThrowException_whenCustomerNotFound() {
    Long id = 1L;
    CustomerData customerData = CustomerData.builder()
      .fullName("Updated Customer")
      .email("new@example.com")
      .build();

    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> customerService.update(id, customerData)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Customer not found with ID: " + id
    );
    verify(customerRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteCustomer_whenExists() {
    Long id = 1L;
    when(customerRepository.existsById(id)).thenReturn(true);
    doNothing().when(customerRepository).deleteById(id);

    customerService.delete(id);

    verify(customerRepository).deleteById(id);
  }

  @Test
  void delete_shouldThrowException_whenNotExists() {
    Long id = 1L;
    when(customerRepository.existsById(id)).thenReturn(false);

    EntityNotFoundException exception = assertThrows(
      EntityNotFoundException.class,
      () -> customerService.delete(id)
    );

    assertThat(exception.getMessage()).isEqualTo(
      "Customer not found with ID: " + id
    );
    verify(customerRepository, never()).deleteById(any());
  }

  @Test
  void searchByName_shouldReturnMatchingCustomers() {
    String searchTerm = "John";

    Customer customer1 = Customer.builder()
      .id(1L)
      .fullName("John Doe")
      .email("john@example.com")
      .build();

    Customer customer2 = Customer.builder()
      .id(2L)
      .fullName("Johnny Smith")
      .email("johnny@example.com")
      .build();

    List<Customer> customers = List.of(customer1, customer2);

    CustomerInfo customerInfo1 = CustomerInfo.builder()
      .id(1L)
      .fullName("John Doe")
      .email("john@example.com")
      .build();

    CustomerInfo customerInfo2 = CustomerInfo.builder()
      .id(2L)
      .fullName("Johnny Smith")
      .email("johnny@example.com")
      .build();

    List<CustomerInfo> customerInfos = List.of(customerInfo1, customerInfo2);

    when(
      customerRepository.findByFullNameContainingIgnoreCase(searchTerm)
    ).thenReturn(customers);
    when(customerMapper.toCustomerInfoList(customers)).thenReturn(
      customerInfos
    );

    List<CustomerInfo> result = customerService.searchByName(searchTerm);

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrderElementsOf(customerInfos);
  }

  @Test
  void findByCreatedDateRange_shouldReturnCustomersInRange() {
    LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

    Customer customer1 = Customer.builder()
      .id(1L)
      .fullName("January Customer")
      .email("jan@example.com")
      .build();

    List<Customer> customers = List.of(customer1);

    CustomerInfo customerInfo1 = CustomerInfo.builder()
      .id(1L)
      .fullName("January Customer")
      .email("jan@example.com")
      .build();

    List<CustomerInfo> customerInfos = List.of(customerInfo1);

    when(
      customerRepository.findByCreatedAtBetween(startDate, endDate)
    ).thenReturn(customers);
    when(customerMapper.toCustomerInfoList(customers)).thenReturn(
      customerInfos
    );

    List<CustomerInfo> result = customerService.findByCreatedDateRange(
      startDate,
      endDate
    );

    assertThat(result).hasSize(1);
    assertThat(result).containsExactlyElementsOf(customerInfos);
  }
}
