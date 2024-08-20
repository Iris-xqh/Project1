package com.example.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.domain.Customer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class CustomerController {
    @Autowired
    CustomerRepository customerRepository;

    //test api works
    @GetMapping
    public String test() {
        return "API works";
    }

    // get all customers
    @GetMapping("/customers")
    public Iterable<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // get a customer by id
    @GetMapping("/customers/{id}")
    public Optional<Customer> getCustomerById(@PathVariable("id") long id) {
        return customerRepository.findById(id);
    }

    //creata a new customer
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if(customer.getName() == null || customer.getEmail() == null || customer.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        customerRepository.save(customer);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    //update a customer
    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer, @PathVariable("id") long id) {
        if(customer.getId() != id || customer.getName() == null || customer.getEmail() == null || customer.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        customerRepository.save(customer);
        return ResponseEntity.ok().build();
    }

    //delete a customer
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}


