package com.nttdata.customer_service.service;

import com.nttdata.customer_service.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<CustomerResponse> create(CreateCustomerRequest request);

    Mono<CustomerResponse> getById(String id);

    Flux<CustomerResponse> getAll();

    Mono<CustomerResponse> update(String id, UpdateCustomerRequest request);

    Mono<Void> delete(String id);

    Mono<CustomerTypeResponse> getTypeById(String id);

}
