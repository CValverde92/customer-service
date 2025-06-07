package com.nttdata.customer_service.repository;

import com.nttdata.customer_service.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Mono<Boolean> existsByDocumentNumber(String documentNumber);

    Mono<Customer> findByDocumentNumber(String documentNumber);
}
