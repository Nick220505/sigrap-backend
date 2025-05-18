package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    Sale sale = saleMapper.toEntity(saleData);

    Customer customer = customerRepository
      .findById(saleData.getCustomerId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Customer not found with ID: " + saleData.getCustomerId()
        )
      );
    sale.setCustomer(customer);

    User employee = userRepository
      .findById(saleData.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found with ID: " + saleData.getEmployeeId()
        )
      );
    sale.setEmployee(employee);

    Sale savedSale = saleRepository.save(sale);

    processItems(savedSale, saleData.getItems());

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
    Sale existingSale = saleRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale not found with ID: " + id)
      );

    List<SaleItem> originalItems = existingSale.getItems();

    saleMapper.updateEntityFromData(existingSale, saleData);

    Customer customer = customerRepository
      .findById(saleData.getCustomerId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Customer not found with ID: " + saleData.getCustomerId()
        )
      );
    existingSale.setCustomer(customer);

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

    Sale updatedSale = saleRepository.save(existingSale);

    returnStockForRemovedItems(originalItems, saleData.getItems());

    saleItemRepository.deleteBySale(updatedSale);
    processItems(updatedSale, saleData.getItems());

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
        new EntityNotFoundException("Sale not found with id: " + id)
      );
    for (SaleItem item : sale.getItems()) {
      Product product = item.getProduct();
      product.setStock(product.getStock() + item.getQuantity());
      productRepository.save(product);
    }
    saleRepository.delete(sale);
  }

  /**
   * Deletes multiple sales by their IDs.
   * Validates all IDs exist before performing the deletion and returns stock for all deleted items.
   *
   * @param ids List of sale IDs to delete
   * @throws EntityNotFoundException if any of the sales is not found
   */
  @Transactional
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!saleRepository.existsById(id)) {
        throw new EntityNotFoundException("Sale with id " + id + " not found");
      }
    });

    ids.forEach(id -> {
      Sale sale = saleRepository.findById(id).get();
      for (SaleItem item : sale.getItems()) {
        Product product = item.getProduct();
        product.setStock(product.getStock() + item.getQuantity());
        productRepository.save(product);
      }
    });

    saleRepository.deleteAllById(ids);
  }

  /**
   * Process sale items by creating SaleItem entities and updating product stock.
   *
   * @param sale The sale to associate the items with
   * @param itemsData The data for the items to process
   * @throws IllegalArgumentException if there is insufficient stock for any product
   */
  private void processItems(Sale sale, List<SaleItemData> itemsData) {
    if (sale.getItems() == null) {
      sale.setItems(new ArrayList<>());
    }
    for (SaleItemData itemData : itemsData) {
      Product product = productRepository
        .findById(itemData.getProductId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Product not found with ID: " + itemData.getProductId()
          )
        );

      if (product.getStock() < itemData.getQuantity()) {
        throw new IllegalArgumentException(
          "Insufficient stock for product: " + product.getName()
        );
      }

      product.setStock(product.getStock() - itemData.getQuantity());
      productRepository.save(product);

      SaleItem saleItem = SaleItem.builder()
        .sale(sale)
        .product(product)
        .quantity(itemData.getQuantity())
        .unitPrice(itemData.getUnitPrice())
        .subtotal(itemData.getSubtotal())
        .build();

      SaleItem savedSaleItem = saleItemRepository.save(saleItem);
      sale.getItems().add(savedSaleItem);
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
    List<Integer> newProductIds = newItemsData
      .stream()
      .map(SaleItemData::getProductId)
      .toList();

    for (SaleItem originalItem : originalItems) {
      Product product = originalItem.getProduct();
      Integer productId = product.getId();

      if (!newProductIds.contains(productId)) {
        product.setStock(product.getStock() + originalItem.getQuantity());
        productRepository.save(product);
      } else {
        for (SaleItemData newItemData : newItemsData) {
          if (newItemData.getProductId().equals(productId)) {
            int quantityDifference =
              originalItem.getQuantity() - newItemData.getQuantity();
            if (quantityDifference > 0) {
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
