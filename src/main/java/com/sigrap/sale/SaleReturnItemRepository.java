package com.sigrap.sale;

import com.sigrap.product.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SaleReturnItem entities.
 * Provides methods to interact with the sales return items data in the database.
 */
@Repository
public interface SaleReturnItemRepository
  extends JpaRepository<SaleReturnItem, Integer> {
  /**
   * Find sales return items by the sales return they belong to.
   *
   * @param saleReturn The sales return to search for
   * @return List of sales return items belonging to the given sales return
   */
  List<SaleReturnItem> findBySaleReturn(SaleReturn saleReturn);

  /**
   * Find sales return items by product.
   *
   * @param product The product to search for
   * @return List of sales return items for the given product
   */
  List<SaleReturnItem> findByProduct(Product product);

  /**
   * Delete all sales return items belonging to a sales return.
   *
   * @param saleReturn The sales return whose items should be deleted
   */
  void deleteBySaleReturn(SaleReturn saleReturn);
}
