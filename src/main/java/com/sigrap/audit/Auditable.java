package com.sigrap.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as auditable, causing an audit log entry to be automatically
 * generated when the method is called.
 *
 * <p>This annotation works with the AuditAspect to intercept method calls and
 * generate audit logs without polluting business logic with audit code.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@literal @}Auditable(action = "CREATE", entity = "PRODUCT")
 * public ProductInfo create(ProductData productData) {
 *     // Business logic...
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
  /**
   * The action being performed.
   * Examples: "CREATE", "UPDATE", "DELETE", "VIEW".
   */
  String action();

  /**
   * The entity type being affected.
   * Examples: "USER", "PRODUCT", "ORDER".
   */
  String entity();

  /**
   * The parameter name or SpEL expression that identifies the entity ID.
   * Leave empty if the entity ID should be extracted from the result.
   *
   * <p>Simple parameter name: "productId"</p>
   * <p>Expression accessing a property: "product.id"</p>
   * <p>SpEL expression: "#request.id"</p>
   */
  String entityIdParam() default "";

  /**
   * Whether to capture detailed information about the method call.
   * When true, method arguments and result will be included in the audit log.
   * Set to false for sensitive operations or when details are too large.
   */
  boolean captureDetails() default false;
}
