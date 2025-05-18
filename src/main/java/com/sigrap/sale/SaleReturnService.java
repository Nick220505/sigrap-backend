package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing sales return-related operations.
 *
 * <p>This service handles the business logic for creating, retrieving, updating,
 * and deleting sales returns. It interacts with various repositories to manage
 * sales return data, product stock adjustments, and related entities.</p>
 */
@Service
@RequiredArgsConstructor
public class SaleReturnService {

  private final SaleReturnRepository saleReturnRepository;
  private final SaleReturnItemRepository saleReturnItemRepository;
  private final SaleRepository saleRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;
  private final SaleReturnMapper saleReturnMapper;

  /**
   * Creates a new sales return based on the provided data.
   *
   * <p>This method performs several operations:
   * <ul>
   *   <li>Validates the existence of the original sale, customer, and employee.</li>
   *   <li>Ensures the customer making the return is the same as in the original sale.</li>
   *   <li>Persists the new {@link SaleReturn} entity.</li>
   *   <li>Processes each item in the return, adjusting product stock levels by increasing them.</li>
   * </ul>
   * </p>
   *
   * @param saleReturnData The data for the new sales return.
   * @return A {@link SaleReturnInfo} DTO representing the created sales return.
   * @throws EntityNotFoundException if the original sale, customer, or employee is not found.
   * @throws IllegalArgumentException if the return customer does not match the original sale customer,
   *                                  or if any returned item is invalid (e.g., product not in original sale,
   *                                  quantity to return exceeds quantity purchased).
   */
  @Transactional
  public SaleReturnInfo create(SaleReturnData saleReturnData) {
    Sale originalSale = saleRepository
      .findById(saleReturnData.getOriginalSaleId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Original sale not found with ID: " +
          saleReturnData.getOriginalSaleId()
        )
      );

    Customer customer = customerRepository
      .findById(saleReturnData.getCustomerId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Customer not found with ID: " + saleReturnData.getCustomerId()
        )
      );

    if (!originalSale.getCustomer().getId().equals(customer.getId())) {
      throw new IllegalArgumentException(
        "Return customer does not match original sale customer."
      );
    }

    User employee = userRepository
      .findById(saleReturnData.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found with ID: " + saleReturnData.getEmployeeId()
        )
      );

    SaleReturn saleReturn = saleReturnMapper.toEntity(saleReturnData);
    saleReturn.setOriginalSale(originalSale);
    saleReturn.setCustomer(customer);
    saleReturn.setEmployee(employee);

    SaleReturn savedSaleReturn = saleReturnRepository.save(saleReturn);
    processReturnItems(
      savedSaleReturn,
      originalSale,
      saleReturnData.getItems()
    );

    SaleReturn refreshedReturn = saleReturnRepository
      .findById(savedSaleReturn.getId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "SaleReturn not found after creation and item processing"
        )
      );

    return saleReturnMapper.toInfo(refreshedReturn);
  }

  /**
   * Processes the individual items within a sales return.
   *
   * <p>For each item in the return data:
   * <ul>
   *   <li>Validates the existence of the product.</li>
   *   <li>Ensures the product was part of the original sale.</li>
   *   <li>Validates that the quantity being returned does not exceed the quantity purchased in the original sale.</li>
   *   <li>Increases the stock of the product by the quantity returned.</li>
   *   <li>Persists the {@link SaleReturnItem} entity.</li>
   * </ul>
   * </p>
   *
   * @param saleReturn The parent {@link SaleReturn} entity to associate items with.
   * @param originalSale The {@link Sale} entity from which items are being returned, for validation.
   * @param itemsData A list of {@link SaleReturnItemData} DTOs representing the items to be processed.
   * @throws EntityNotFoundException if a product specified in an item is not found.
   * @throws IllegalArgumentException if a product was not in the original sale, or if the return quantity is invalid.
   */
  private void processReturnItems(
    SaleReturn saleReturn,
    Sale originalSale,
    List<SaleReturnItemData> itemsData
  ) {
    if (saleReturn.getItems() == null) {
      saleReturn.setItems(new java.util.ArrayList<>());
    }
    for (SaleReturnItemData itemData : itemsData) {
      Product product = productRepository
        .findById(itemData.getProductId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Product not found with ID: " + itemData.getProductId()
          )
        );

      Optional<SaleItem> originalSaleItemOpt = originalSale
        .getItems()
        .stream()
        .filter(originalItem ->
          originalItem.getProduct().getId().equals(product.getId())
        )
        .findFirst();

      if (originalSaleItemOpt.isEmpty()) {
        throw new IllegalArgumentException(
          "Product " + product.getName() + " was not in the original sale."
        );
      }

      SaleItem originalSaleItem = originalSaleItemOpt.get();
      if (itemData.getQuantity() > originalSaleItem.getQuantity()) {
        throw new IllegalArgumentException(
          "Cannot return more items of " +
          product.getName() +
          " than were originally purchased."
        );
      }

      product.setStock(product.getStock() + itemData.getQuantity());
      productRepository.save(product);

      SaleReturnItem saleReturnItem = SaleReturnItem.builder()
        .saleReturn(saleReturn)
        .product(product)
        .quantity(itemData.getQuantity())
        .unitPrice(itemData.getUnitPrice())
        .subtotal(itemData.getSubtotal())
        .build();
      SaleReturnItem savedSaleReturnItem = saleReturnItemRepository.save(
        saleReturnItem
      );
      saleReturn.getItems().add(savedSaleReturnItem);
    }
  }

  /**
   * Retrieves all sales returns present in the system.
   *
   * @return A list of {@link SaleReturnInfo} DTOs, each representing a sales return.
   *         Returns an empty list if no sales returns are found.
   */
  @Transactional(readOnly = true)
  public List<SaleReturnInfo> findAll() {
    return saleReturnMapper.toInfoList(saleReturnRepository.findAll());
  }

  /**
   * Finds a specific sales return by its unique identifier.
   *
   * @param id The ID of the sales return to retrieve.
   * @return A {@link SaleReturnInfo} DTO representing the found sales return.
   * @throws EntityNotFoundException if no sales return is found with the given ID.
   */
  @Transactional(readOnly = true)
  public SaleReturnInfo findById(Integer id) {
    SaleReturn saleReturn = saleReturnRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sales return not found with ID: " + id)
      );
    return saleReturnMapper.toInfo(saleReturn);
  }

  /**
   * Finds all sales returns associated with a specific original sale ID.
   *
   * @param originalSaleId The ID of the original sale for which to retrieve returns.
   * @return A list of {@link SaleReturnInfo} DTOs related to the specified original sale.
   *         Returns an empty list if no returns are found for the given original sale ID.
   * @throws EntityNotFoundException if the original sale with the given ID is not found.
   */
  @Transactional(readOnly = true)
  public List<SaleReturnInfo> findByOriginalSaleId(Integer originalSaleId) {
    Sale originalSale = saleRepository
      .findById(originalSaleId)
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Original sale not found with ID: " + originalSaleId
        )
      );
    return saleReturnMapper.toInfoList(
      saleReturnRepository.findByOriginalSale(originalSale)
    );
  }

  /**
   * Deletes a sales return identified by its ID.
   *
   * <p>This method also reverts the stock adjustments made when the return was created.
   * For each item in the deleted return, the product stock is decreased by the quantity
   * that was returned (effectively undoing the stock increase from the return).</p>
   *
   * @param id The ID of the sales return to delete.
   * @throws EntityNotFoundException if no sales return is found with the given ID.
   */
  @Transactional
  public void delete(Integer id) {
    SaleReturn saleReturn = saleReturnRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Sale return not found with id: " + id)
      );

    List<SaleReturnItem> returnItems = saleReturn.getItems();

    for (SaleReturnItem item : returnItems) {
      Product product = item.getProduct();
      Integer currentStock = product.getStock();
      product.setStock(currentStock - item.getQuantity());
      productRepository.save(product);
    }

    saleReturnRepository.delete(saleReturn);
  }

  /**
   * Deletes multiple sale returns by their IDs.
   * Validates all IDs exist before performing the deletion and adjusts product stock accordingly.
   *
   * @param ids List of sale return IDs to delete
   * @throws EntityNotFoundException if any of the sale returns is not found
   */
  @Transactional
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!saleReturnRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Sale return with id " + id + " not found"
        );
      }
    });

    ids.forEach(id -> {
      SaleReturn saleReturn = saleReturnRepository.findById(id).get();

      for (SaleReturnItem item : saleReturn.getItems()) {
        Product product = item.getProduct();
        Integer currentStock = product.getStock();
        product.setStock(currentStock - item.getQuantity());
        productRepository.save(product);
      }
    });

    saleReturnRepository.deleteAllById(ids);
  }

  /**
   * Updates an existing sales return identified by its ID.
   *
   * <p>This method performs several operations:
   * <ul>
   *   <li>Validates the existence of the sales return, original sale, customer, and employee.</li>
   *   <li>Ensures that the original sale and customer are not changed during the update.</li>
   *   <li>Updates the employee processing the return and the reason for the return.</li>
   *   <li>Manages the list of returned items:
   *     <ul>
   *       <li>Items not present in the new data are removed, and their stock impact is reversed (stock decreased).</li>
   *       <li>Existing items are updated (quantity, price), and stock is adjusted for the difference.</li>
   *       <li>New items are added, and their stock impact is applied (stock increased).</li>
   *     </ul>
   *   </li>
   *   <li>Recalculates and updates the total return amount based on the modified items.</li>
   *   <li>Persists the changes to the {@link SaleReturn} and its associated {@link SaleReturnItem}s.</li>
   * </ul>
   * </p>
   *
   * @param id The ID of the sales return to update.
   * @param saleReturnData The {@link SaleReturnData} DTO containing the updated information.
   * @return A {@link SaleReturnInfo} DTO representing the updated sales return.
   * @throws EntityNotFoundException if the sales return, original sale, customer, employee, or any product in items is not found.
   * @throws IllegalArgumentException if an attempt is made to change the original sale or customer,
   *                                  or if any item validation fails (e.g., returning more than purchased).
   */
  @Transactional
  public SaleReturnInfo update(Integer id, SaleReturnData saleReturnData) {
    SaleReturn existingSaleReturn = saleReturnRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("SaleReturn not found with ID: " + id)
      );

    Sale originalSale = saleRepository
      .findById(saleReturnData.getOriginalSaleId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Original sale not found with ID: " +
          saleReturnData.getOriginalSaleId()
        )
      );

    if (
      !existingSaleReturn.getOriginalSale().getId().equals(originalSale.getId())
    ) {
      throw new IllegalArgumentException(
        "Cannot change the original sale of a return."
      );
    }

    Customer customer = customerRepository
      .findById(saleReturnData.getCustomerId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Customer not found with ID: " + saleReturnData.getCustomerId()
        )
      );
    if (!existingSaleReturn.getCustomer().getId().equals(customer.getId())) {
      throw new IllegalArgumentException(
        "Cannot change the customer of a return."
      );
    }

    User employee = userRepository
      .findById(saleReturnData.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found with ID: " + saleReturnData.getEmployeeId()
        )
      );
    existingSaleReturn.setEmployee(employee);

    saleReturnMapper.updateEntityFromData(existingSaleReturn, saleReturnData);

    updateReturnItems(
      existingSaleReturn,
      originalSale,
      saleReturnData.getItems()
    );

    existingSaleReturn.setTotalReturnAmount(
      calculateTotalReturnAmount(existingSaleReturn.getItems())
    );

    SaleReturn updatedSaleReturn = saleReturnRepository.save(
      existingSaleReturn
    );
    return saleReturnMapper.toInfo(updatedSaleReturn);
  }

  /**
   * Updates the items of a sales return, adjusting product stock accordingly.
   *
   * <p>This method synchronizes the persisted {@link SaleReturnItem}s with the provided
   * list of {@link SaleReturnItemData}. It handles:
   * <ul>
   *  <li><strong>Item Removal</strong>: If an existing item is not in {@code newItemsData},
   *      it's deleted, and its quantity is subtracted from the product's stock (reversing the initial return stock adjustment for that item).</li>
   *  <li><strong>Item Modification</strong>: If an item exists in both current and new data,
   *      its details (quantity, price, subtotal) are updated. The product stock is adjusted by the difference
   *      between the new and old quantities.</li>
   *  <li><strong>Item Addition</strong>: If an item in {@code newItemsData} is not an existing item,
   *      it's created, and its quantity is added to the product's stock.</li>
   * </ul>
   * All product stock changes are validated against original purchase quantities.
   * </p>
   *
   * @param saleReturn The {@link SaleReturn} entity being updated.
   * @param originalSale The {@link Sale} entity from which items were originally returned, for validation.
   * @param newItemsData A list of {@link SaleReturnItemData} DTOs representing the desired state of items for the return.
   * @throws EntityNotFoundException if any product specified in {@code newItemsData} is not found.
   * @throws IllegalArgumentException if any validation fails, such as returning more items than purchased or referencing a product not in the original sale.
   */
  private void updateReturnItems(
    SaleReturn saleReturn,
    Sale originalSale,
    List<SaleReturnItemData> newItemsData
  ) {
    List<SaleReturnItem> existingItems =
      saleReturnItemRepository.findBySaleReturn(saleReturn);
    List<Integer> newItemProductIds = newItemsData
      .stream()
      .map(SaleReturnItemData::getProductId)
      .toList();

    for (SaleReturnItem existingItem : existingItems) {
      if (!newItemProductIds.contains(existingItem.getProduct().getId())) {
        Product product = existingItem.getProduct();
        product.setStock(product.getStock() - existingItem.getQuantity());
        productRepository.save(product);
        saleReturnItemRepository.delete(existingItem);
      }
    }

    for (SaleReturnItemData itemData : newItemsData) {
      Product product = productRepository
        .findById(itemData.getProductId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Product not found with ID: " + itemData.getProductId()
          )
        );

      SaleItem originalSaleItem = originalSale
        .getItems()
        .stream()
        .filter(origItem ->
          origItem.getProduct().getId().equals(product.getId())
        )
        .findFirst()
        .orElseThrow(() ->
          new IllegalArgumentException(
            "Product " + product.getName() + " was not in the original sale."
          )
        );

      if (itemData.getQuantity() > originalSaleItem.getQuantity()) {
        throw new IllegalArgumentException(
          "Cannot return more items of " +
          product.getName() +
          " than were originally purchased."
        );
      }

      Optional<SaleReturnItem> existingItemOpt = existingItems
        .stream()
        .filter(ei -> ei.getProduct().getId().equals(product.getId()))
        .findFirst();

      if (existingItemOpt.isPresent()) {
        SaleReturnItem existingItem = existingItemOpt.get();
        int quantityDifference =
          itemData.getQuantity() - existingItem.getQuantity();
        product.setStock(product.getStock() + quantityDifference);

        existingItem.setQuantity(itemData.getQuantity());
        existingItem.setUnitPrice(itemData.getUnitPrice());
        existingItem.setSubtotal(itemData.getSubtotal());
        saleReturnItemRepository.save(existingItem);
      } else {
        product.setStock(product.getStock() + itemData.getQuantity());
        SaleReturnItem newReturnItem = SaleReturnItem.builder()
          .saleReturn(saleReturn)
          .product(product)
          .quantity(itemData.getQuantity())
          .unitPrice(itemData.getUnitPrice())
          .subtotal(itemData.getSubtotal())
          .build();
        saleReturnItemRepository.save(newReturnItem);
      }
      productRepository.save(product);
    }
  }

  /**
   * Calculates the total amount for a list of sales return items.
   *
   * @param items A list of {@link SaleReturnItem}s.
   * @return The sum of subtotals of all items in the list as a {@link BigDecimal}.
   *         Returns {@link BigDecimal#ZERO} if the list is empty or null.
   */
  private BigDecimal calculateTotalReturnAmount(List<SaleReturnItem> items) {
    return items
      .stream()
      .map(SaleReturnItem::getSubtotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
