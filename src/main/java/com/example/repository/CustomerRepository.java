package com.example.repository;

import com.example.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

// extends CrudRepository<Customer, Long> to use the CRUD methods
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // find a customer by name, using the Optional class to avoid null pointer exceptions
    //use JPA query methods to find a customer by name
    Optional<Customer> findByName(String name);
}
