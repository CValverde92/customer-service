package com.nttdata.customer_service.controller;

import com.nttdata.customer_service.api.CustomersApi;
import com.nttdata.customer_service.controller.exception.DuplicateResourceException;
import com.nttdata.customer_service.controller.exception.ResourceNotFoundException;
import com.nttdata.customer_service.model.CreateCustomerRequest;
import com.nttdata.customer_service.model.CustomerResponse;
import com.nttdata.customer_service.model.CustomerTypeResponse;
import com.nttdata.customer_service.model.UpdateCustomerRequest;
import com.nttdata.customer_service.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer API", description = "Manage banking customers")
public class CustomersApiController implements CustomersApi {

    private final CustomerService service;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(
            String authorization,
            String requestID,
            Mono<CreateCustomerRequest> createCustomerRequest,
            ServerWebExchange exchange) {
        return createCustomerRequest.flatMap(service::create)
                .map(ResponseEntity::ok)
                .onErrorResume(DuplicateResourceException.class,
                        e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(
            String customerId,
            String authorization,
            String requestID,
            ServerWebExchange exchange) {
        return service.delete(customerId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(ResourceNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomer(
            String customerId,
            String authorization,
            String requestID,
            ServerWebExchange exchange) {
        return service.getById(customerId)
                .map(ResponseEntity::ok)
                .onErrorResume(ResourceNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<CustomerTypeResponse>> getCustomerType(
            String customerId,
            String authorization,
            String requestID,
            ServerWebExchange exchange) {
        return service.getTypeById(customerId)
                .map(ResponseEntity::ok)
                .onErrorResume(ResourceNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> listCustomers(
            String authorization,
            String requestID,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(service.getAll()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(
            String customerId,
            String authorization,
            String requestID,
            Mono<UpdateCustomerRequest> updateCustomerRequest,
            ServerWebExchange exchange) {
        return updateCustomerRequest
                .flatMap(request -> service.update(customerId, request))
                .map(ResponseEntity::ok)
                .onErrorResume(ResourceNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(DuplicateResourceException.class,
                        e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
