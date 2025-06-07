package com.nttdata.customer_service.service.impl;

import com.nttdata.customer_service.controller.exception.BusinessRuleException;
import com.nttdata.customer_service.controller.exception.DuplicateResourceException;
import com.nttdata.customer_service.controller.exception.ResourceNotFoundException;
import com.nttdata.customer_service.controller.exception.ServiceIntegrationException;
import com.nttdata.customer_service.mapper.CustomerMapper;
import com.nttdata.customer_service.model.*;
import com.nttdata.customer_service.model.dto.request.AccountRequest;
import com.nttdata.customer_service.model.enums.AccountType;
import com.nttdata.customer_service.repository.CustomerRepository;
import com.nttdata.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<CustomerResponse> create(CreateCustomerRequest request) {
        log.info("Creating customer with document: {}", request.getDocumentNumber());
        return repository.existsByDocumentNumber(request.getDocumentNumber())
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicateResourceException("Customer already exists"))
                        : saveCustomerWithAccount(request));
    }

    @Override
    public Mono<CustomerResponse> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found")))
                .map(mapper::toResponse);
    }

    @Override
    public Flux<CustomerResponse> getAll() {
        return repository.findAll().map(mapper::toResponse);
    }

    @Override
    public Mono<CustomerResponse> update(String id, UpdateCustomerRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found")))
                .flatMap(existing -> {
                    mapper.updateFromRequest(request, existing);
                    return repository.save(existing);
                }).map(mapper::toResponse);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<CustomerTypeResponse> getTypeById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found")))
                .map(customer -> new CustomerTypeResponse()
                        .customerId(customer.getId())
                        .type(customer.getType())
                );

    }

    //Implementation
    private Mono<CustomerResponse> saveCustomerWithAccount(CreateCustomerRequest request) {
        return validateAccountRules(request)
                .then(proceedWithCustomerSave(request));
    }

    private Mono<Void> validateAccountRules(CreateCustomerRequest request) {
        if (request.getType() == CustomerType.PERSONAL) {
            return webClientBuilder.build()
                    .get()
                    .uri("http://account-service/api/accounts/customers/{doc}/count",
                            request.getDocumentNumber())
                    .retrieve()
                    .onStatus(HttpStatus::isError,
                            response -> Mono.error(new ServiceIntegrationException("Error validating accounts")))
                    .bodyToMono(Integer.class)
                    .flatMap(count -> count >= 1
                            ? Mono.error(new BusinessRuleException(
                            "Personal customer can only have 1 account (current: " + count + ")"))
                            : Mono.empty());
        }
        return Mono.empty();
    }

    private Mono<CustomerResponse> proceedWithCustomerSave(CreateCustomerRequest request) {
        return repository.save(mapper.toEntity(request))
                .flatMap(savedCustomer -> {
                    if (savedCustomer.getType() == CustomerType.BUSINESS) {
                        return createBusinessAccount(savedCustomer)
                                .thenReturn(mapper.toResponse(savedCustomer));
                    }
                    return Mono.just(mapper.toResponse(savedCustomer));
                });
    }

    private Mono<Void> createBusinessAccount(Customer customer) {
        AccountRequest accountRequest = new AccountRequest(
                customer.getId(),
                AccountType.CHECKING,
                0.0
        );

        return webClientBuilder.build()
                .post()
                .uri("http://account-service/api/accounts")
                .header("Authorization", "Bearer {token}")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(accountRequest)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new ServiceIntegrationException("Error creating account")))
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Account created for business customer: {}", customer.getId()));
    }

}
