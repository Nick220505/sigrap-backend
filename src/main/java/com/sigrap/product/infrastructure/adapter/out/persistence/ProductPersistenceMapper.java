package com.sigrap.product.infrastructure.adapter.out.persistence;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.model.ProductName;
import com.sigrap.product.domain.model.ProductPrice;
import com.sigrap.product.domain.model.ProductStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

/**
 * MapStruct mapper for converting between domain Product and ProductJpaEntity.
 * This mapper handles the translation between the domain model (pure POJOs)
 * and the persistence model (JPA entities).
 * 
 * The mapper uses custom methods to convert between value objects (ProductId, ProductName, etc.)
 * and their primitive representations used in the JPA entity.
 */
@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    /**
     * Converts a domain Product to a JPA entity.
     * Maps value objects to their primitive representations.
     *
     * @param product the domain product
     * @return the JPA entity representation
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "productIdToLong")
    @Mapping(target = "name", source = "name", qualifiedByName = "productNameToString")
    @Mapping(target = "costPrice", source = "costPrice", qualifiedByName = "productPriceToBigDecimal")
    @Mapping(target = "salePrice", source = "salePrice", qualifiedByName = "productPriceToBigDecimal")
    @Mapping(target = "stock", source = "stock", qualifiedByName = "productStockToInteger")
    @Mapping(target = "minimumStockThreshold", source = "minimumStockThreshold", qualifiedByName = "productStockToInteger")
    @Mapping(target = "categoryId", source = "categoryId", qualifiedByName = "categoryIdToLong")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductJpaEntity toJpaEntity(Product product);

    /**
     * Converts a JPA entity to a domain Product.
     * Maps primitive values to value objects.
     * Uses a custom implementation to handle the immutable domain entity.
     *
     * @param entity the JPA entity
     * @return the domain product
     */
    default Product toDomain(ProductJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ProductId id = longToProductId(entity.getId());
        ProductName name = stringToProductName(entity.getName());
        ProductPrice costPrice = bigDecimalToProductPrice(entity.getCostPrice());
        ProductPrice salePrice = bigDecimalToProductPrice(entity.getSalePrice());
        ProductStock stock = integerToProductStock(entity.getStock());
        ProductStock minimumStockThreshold = integerToProductStock(entity.getMinimumStockThreshold());
        CategoryId categoryId = longToCategoryId(entity.getCategoryId());
        
        return new Product(
            id,
            name,
            entity.getDescription(),
            costPrice,
            salePrice,
            stock,
            minimumStockThreshold,
            categoryId,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts a ProductId value object to its Long representation.
     *
     * @param id the product ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("productIdToLong")
    default Long productIdToLong(ProductId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a ProductId value object.
     *
     * @param id the Long value
     * @return the ProductId value object, or null if the Long is null
     */
    @Named("longToProductId")
    default ProductId longToProductId(Long id) {
        return id != null ? new ProductId(id) : null;
    }

    /**
     * Converts a ProductName value object to its String representation.
     *
     * @param name the product name value object
     * @return the String value, or null if the name is null
     */
    @Named("productNameToString")
    default String productNameToString(ProductName name) {
        return name != null ? name.value() : null;
    }

    /**
     * Converts a String to a ProductName value object.
     *
     * @param name the String value
     * @return the ProductName value object, or null if the String is null
     */
    @Named("stringToProductName")
    default ProductName stringToProductName(String name) {
        return name != null ? new ProductName(name) : null;
    }

    /**
     * Converts a ProductPrice value object to its BigDecimal representation.
     *
     * @param price the product price value object
     * @return the BigDecimal value, or null if the price is null
     */
    @Named("productPriceToBigDecimal")
    default BigDecimal productPriceToBigDecimal(ProductPrice price) {
        return price != null ? price.value() : null;
    }

    /**
     * Converts a BigDecimal to a ProductPrice value object.
     *
     * @param price the BigDecimal value
     * @return the ProductPrice value object, or null if the BigDecimal is null
     */
    @Named("bigDecimalToProductPrice")
    default ProductPrice bigDecimalToProductPrice(BigDecimal price) {
        return price != null ? new ProductPrice(price) : null;
    }

    /**
     * Converts a ProductStock value object to its Integer representation.
     *
     * @param stock the product stock value object
     * @return the Integer value, or null if the stock is null
     */
    @Named("productStockToInteger")
    default Integer productStockToInteger(ProductStock stock) {
        return stock != null ? stock.value() : null;
    }

    /**
     * Converts an Integer to a ProductStock value object.
     *
     * @param stock the Integer value
     * @return the ProductStock value object, or null if the Integer is null
     */
    @Named("integerToProductStock")
    default ProductStock integerToProductStock(Integer stock) {
        return stock != null ? new ProductStock(stock) : null;
    }

    /**
     * Converts a CategoryId value object to its Long representation.
     *
     * @param id the category ID value object
     * @return the Long value, or null if the ID is null
     */
    @Named("categoryIdToLong")
    default Long categoryIdToLong(CategoryId id) {
        return id != null ? id.value() : null;
    }

    /**
     * Converts a Long to a CategoryId value object.
     *
     * @param id the Long value
     * @return the CategoryId value object, or null if the Long is null
     */
    @Named("longToCategoryId")
    default CategoryId longToCategoryId(Long id) {
        return id != null ? new CategoryId(id) : null;
    }
}
