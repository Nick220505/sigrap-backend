package com.sigrap.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This exception is used in hexagonal architecture to indicate
 * that a domain entity or resource does not exist.
 * 
 * <p>This exception should be mapped to HTTP 404 NOT_FOUND status
 * by the global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
