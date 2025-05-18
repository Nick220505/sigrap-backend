package com.sigrap.customer;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Customer entities and DTOs.
 * Handles the conversion of data between different representations.
 *
 * <p>This class provides methods for:
 * <ul>
 *   <li>Converting entities to DTOs</li>
 *   <li>Converting DTOs to entities</li>
 *   <li>Updating entities from DTOs</li>
 *   <li>Batch conversions</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class CustomerMapper {

  /**
   * Converts a Customer entity to CustomerInfo DTO.
   *
   * @param customer The customer entity to convert
   * @return The resulting CustomerInfo DTO, or null if input is null
   */
  public CustomerInfo toCustomerInfo(Customer customer) {
    if (customer == null) {
      return null;
    }

    return CustomerInfo.builder()
      .id(customer.getId())
      .fullName(customer.getFullName())
      .documentId(customer.getDocumentId())
      .email(customer.getEmail())
      .phoneNumber(customer.getPhoneNumber())
      .address(customer.getAddress())
      .createdAt(customer.getCreatedAt())
      .updatedAt(customer.getUpdatedAt())
      .build();
  }

  /**
   * Converts a list of Customer entities to a list of CustomerInfo DTOs.
   *
   * @param customers The list of customer entities to convert
   * @return A list of CustomerInfo DTOs, or an empty list if input is null
   */
  public List<CustomerInfo> toCustomerInfoList(List<Customer> customers) {
    if (customers == null) {
      return Collections.emptyList();
    }

    return customers.stream().map(this::toCustomerInfo).toList();
  }

  /**
   * Creates a new Customer entity from CustomerData DTO.
   *
   * @param customerData The DTO containing customer data
   * @return A new Customer entity, or null if input is null
   */
  public Customer toCustomer(CustomerData customerData) {
    if (customerData == null) {
      return null;
    }

    return Customer.builder()
      .fullName(customerData.getFullName())
      .documentId(customerData.getDocumentId())
      .email(customerData.getEmail())
      .phoneNumber(customerData.getPhoneNumber())
      .address(customerData.getAddress())
      .build();
  }

  /**
   * Updates an existing Customer entity with data from CustomerData DTO.
   *
   * @param customer The customer entity to update
   * @param customerData The DTO containing the new customer data
   */
  public void updateCustomerFromDto(
    Customer customer,
    CustomerData customerData
  ) {
    if (customer == null || customerData == null) {
      return;
    }

    customer.setFullName(customerData.getFullName());
    customer.setDocumentId(customerData.getDocumentId());
    customer.setEmail(customerData.getEmail());
    customer.setPhoneNumber(customerData.getPhoneNumber());
    customer.setAddress(customerData.getAddress());
  }
}
