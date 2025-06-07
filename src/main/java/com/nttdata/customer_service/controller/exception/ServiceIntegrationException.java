package com.nttdata.customer_service.controller.exception;

public class ServiceIntegrationException extends RuntimeException {
    public ServiceIntegrationException(String message) {
        super(message);
    }
}
