/**
 * Comprehensive auditing system for tracking and recording user actions.
 *
 * <p>This package provides a centralized, aspect-oriented approach to audit logging,
 * separating the cross-cutting concern of auditing from business logic. It includes:</p>
 *
 * <ul>
 *   <li>An aspect-based mechanism to capture and log audit events</li>
 *   <li>An event-driven architecture for asynchronous audit processing</li>
 *   <li>Rich context capture including user, action, entity details, and HTTP metadata</li>
 *   <li>Comprehensive query capabilities for security and compliance reporting</li>
 * </ul>
 *
 * <p>The design follows separation of concerns principles, allowing business logic
 * to remain clean while audit trail generation happens automatically.</p>
 */
package com.sigrap.audit;
