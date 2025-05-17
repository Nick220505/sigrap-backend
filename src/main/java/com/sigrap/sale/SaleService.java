package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing sales-related operations.
 */
@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final SaleItemRepository saleItemRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;
  private final SaleMapper saleMapper;

  /**
   * Find all sales.
   *
   * @return List of all sales as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findAll() {
    List<Sale> sales = saleRepository.findAll();
    return saleMapper.toInfoList(sales);
  }

  /**
   * Find a sale by its ID.
   *
   * @param id The ID of the sale to find
   * @return The sale as a SaleInfo DTO
   * @throws EntityNotFoundException if the sale is not found
   */
  @Transactional(readOnly = true)
  public SaleInfo findById(Integer id) {
    Sale sale = saleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found with ID: " + id)
      );
    return saleMapper.toInfo(sale);
  }

  /**
   * Find sales by status.
   *
   * @param status The status to filter by
   * @return List of sales with the given status as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findByStatus(SaleStatus status) {
    List<Sale> sales = saleRepository.findByStatus(status);
    return saleMapper.toInfoList(sales);
  }

  /**
   * Find sales by employee ID.
   *
   * @param employeeId The ID of the employee who processed the sales
   * @return List of sales processed by the given employee as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findByEmployeeId(Long employeeId) {
    User employee = userRepository
      .findById(employeeId)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found with ID: " + employeeId)
      );
    List<Sale> sales = saleRepository.findByEmployee(employee);
    return saleMapper.toInfoList(sales);
  }

  /**
   * Find sales by customer ID.
   *
   * @param customerId The ID of the customer who made the purchases
   * @return List of sales made by the given customer as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findByCustomerId(Long customerId) {
    Customer customer = customerRepository
      .findById(customerId)
      .orElseThrow(() ->
        new EntityNotFoundException("Customer not found with ID: " + customerId)
      );
    List<Sale> sales = saleRepository.findByCustomer(customer);
    return saleMapper.toInfoList(sales);
  }

  /**
   * Find sales created between two dates.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of sales created within the given date range as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findByCreatedDateRange(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<Sale> sales = saleRepository.findByCreatedAtBetween(
      startDate,
      endDate
    );
    return saleMapper.toInfoList(sales);
  }

  /**
   * Find sales by payment method.
   *
   * @param paymentMethod The payment method to filter by
   * @return List of sales with the given payment method as SaleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<SaleInfo> findByPaymentMethod(PaymentMethod paymentMethod) {
    List<Sale> sales = saleRepository.findByPaymentMethod(paymentMethod);
    return saleMapper.toInfoList(sales);
  }

  /**
   * Create a new sale.
   * This method also updates the stock of the products included in the sale.
   *
   * @param saleData The data for the new sale
   * @return The created sale as a SaleInfo DTO
   * @throws EntityNotFoundException if the customer or employee is not found
   * @throws IllegalArgumentException if there is insufficient stock for any product
   */
  @Transactional
  public SaleInfo create(SaleData saleData) {
    // Create the sale entity
    Sale sale = saleMapper.toEntity(saleData);

    // Set customer if provided
    if (saleData.getCustomerId() != null) {
      Customer customer = customerRepository
        .findById(saleData.getCustomerId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Customer not found with ID: " + saleData.getCustomerId()
          )
        );
      sale.setCustomer(customer);
    }

    // Set employee
    User employee = userRepository
      .findById(saleData.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found with ID: " + saleData.getEmployeeId()
        )
      );
    sale.setEmployee(employee);

    // Save the sale to get an ID
    Sale savedSale = saleRepository.save(sale);

    // Process sale items
    processItems(savedSale, saleData.getItems());

    // Refresh the sale from the database to include all relationships
    Sale refreshedSale = saleRepository
      .findById(savedSale.getId())
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found after creation")
      );

    return saleMapper.toInfo(refreshedSale);
  }

  /**
   * Update an existing sale.
   * This method also updates the stock of the products if the items have changed.
   *
   * @param id The ID of the sale to update
   * @param saleData The new data for the sale
   * @return The updated sale as a SaleInfo DTO
   * @throws EntityNotFoundException if the sale, customer, or employee is not found
   * @throws IllegalArgumentException if there is insufficient stock for any product
   */
  @Transactional
  public SaleInfo update(Integer id, SaleData saleData) {
    // Find the existing sale
    Sale existingSale = saleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found with ID: " + id)
      );

    // Save the original items for later comparison
    List<SaleItem> originalItems = existingSale.getItems();

    // Update the sale data
    saleMapper.updateEntityFromData(existingSale, saleData);

    // Update customer if provided
    if (saleData.getCustomerId() != null) {
      Customer customer = customerRepository
        .findById(saleData.getCustomerId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Customer not found with ID: " + saleData.getCustomerId()
          )
        );
      existingSale.setCustomer(customer);
    }

    // Update employee if different
    if (!existingSale.getEmployee().getId().equals(saleData.getEmployeeId())) {
      User employee = userRepository
        .findById(saleData.getEmployeeId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Employee not found with ID: " + saleData.getEmployeeId()
          )
        );
      existingSale.setEmployee(employee);
    }

    // Save the updated sale
    Sale updatedSale = saleRepository.save(existingSale);

    // Return stock for removed items
    returnStockForRemovedItems(originalItems, saleData.getItems());

    // Remove existing items and process new ones
    saleItemRepository.deleteBySale(updatedSale);
    processItems(updatedSale, saleData.getItems());

    // Refresh the sale from the database
    Sale refreshedSale = saleRepository
      .findById(updatedSale.getId())
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found after update")
      );

    return saleMapper.toInfo(refreshedSale);
  }

  /**
   * Delete a sale.
   * This method also returns the stock of the products included in the sale.
   *
   * @param id The ID of the sale to delete
   * @throws EntityNotFoundException if the sale is not found
   */
  @Transactional
  public void delete(Integer id) {
    Sale sale = saleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found with ID: " + id)
      );

    // Return stock for all items
    for (SaleItem item : sale.getItems()) {
      Product product = item.getProduct();
      product.setStock(product.getStock() + item.getQuantity());
      productRepository.save(product);
    }

    // Delete the sale (cascade will delete items)
    saleRepository.delete(sale);
  }

  /**
   * Update the status of a sale.
   *
   * @param id The ID of the sale to update
   * @param status The new status for the sale
   * @return The updated sale as a SaleInfo DTO
   * @throws EntityNotFoundException if the sale is not found
   */
  @Transactional
  public SaleInfo updateStatus(Integer id, SaleStatus status) {
    Sale sale = saleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found with ID: " + id)
      );

    sale.setStatus(status);
    Sale updatedSale = saleRepository.save(sale);

    return saleMapper.toInfo(updatedSale);
  }

  /**
   * Process sale items by creating SaleItem entities and updating product stock.
   *
   * @param sale The sale to associate the items with
   * @param itemsData The data for the items to process
   * @throws IllegalArgumentException if there is insufficient stock for any product
   */
  private void processItems(Sale sale, List<SaleItemData> itemsData) {
    for (SaleItemData itemData : itemsData) {
      // Find the product
      Product product = productRepository
        .findById(itemData.getProductId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Product not found with ID: " + itemData.getProductId()
          )
        );

      // Check if there's enough stock
      if (product.getStock() < itemData.getQuantity()) {
        throw new IllegalArgumentException(
          "Insufficient stock for product: " + product.getName()
        );
      }

      // Update the product stock
      product.setStock(product.getStock() - itemData.getQuantity());
      productRepository.save(product);

      // Create and save the sale item
      SaleItem saleItem = SaleItem.builder()
        .sale(sale)
        .product(product)
        .quantity(itemData.getQuantity())
        .unitPrice(itemData.getUnitPrice())
        .discount(itemData.getDiscount())
        .subtotal(itemData.getSubtotal())
        .build();

      saleItemRepository.save(saleItem);
    }
  }

  /**
   * Return stock for removed items when updating a sale.
   *
   * @param originalItems The original items in the sale
   * @param newItemsData The new items data
   */
  private void returnStockForRemovedItems(
    List<SaleItem> originalItems,
    List<SaleItemData> newItemsData
  ) {
    // Get all product IDs in the new items
    List<Integer> newProductIds = newItemsData
      .stream()
      .map(SaleItemData::getProductId)
      .collect(Collectors.toList());

    // For each original item, check if it's still in the new items
    for (SaleItem originalItem : originalItems) {
      Product product = originalItem.getProduct();
      Integer productId = product.getId();

      // If the product is not in the new items or its quantity has been reduced
      if (!newProductIds.contains(productId)) {
        // Return all stock for this item
        product.setStock(product.getStock() + originalItem.getQuantity());
        productRepository.save(product);
      } else {
        // If the product is in the new items, check if the quantity has been reduced
        for (SaleItemData newItemData : newItemsData) {
          if (newItemData.getProductId().equals(productId)) {
            int quantityDifference =
              originalItem.getQuantity() - newItemData.getQuantity();
            if (quantityDifference > 0) {
              // Return the difference in stock
              product.setStock(product.getStock() + quantityDifference);
              productRepository.save(product);
            }
            break;
          }
        }
      }
    }
  }
}
