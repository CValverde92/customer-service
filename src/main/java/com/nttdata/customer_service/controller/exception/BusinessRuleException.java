package com.nttdata.customer_service.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class BusinessRuleException extends ResponseStatusException {

    public BusinessRuleException(String reason) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, reason);
        log.error("Violación de regla de negocio: {}", reason); // Log automático
    }

    public BusinessRuleException(String reason, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, reason, cause);
    }
}
