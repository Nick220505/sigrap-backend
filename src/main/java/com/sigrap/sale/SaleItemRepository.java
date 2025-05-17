package com.sigrap.sale;

import com.sigrap.product.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SaleItem entities.
 * Provides methods to interact with the sale items data in the database.
 */
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
  /**
   * Find sale items by sale.
   *
   * @param sale The sale to search for
   * @return List of sale items belonging to the given sale
   */
  List<SaleItem> findBySale(Sale sale);

  /**
   * Find sale items by product.
   *
   * @param product The product to search for
   * @return List of sale items for the given product
   */
  List<SaleItem> findByProduct(Product product);

  /**
   * Find sale items by sale and product.
   *
   * @param sale The sale to search for
   * @param product The product to search for
   * @return List of sale items for the given sale and product
   */
  List<SaleItem> findBySaleAndProduct(Sale sale, Product product);

  /**
   * Delete all sale items belonging to a sale.
   *
   * @param sale The sale whose items should be deleted
   */
  void deleteBySale(Sale sale);
}
