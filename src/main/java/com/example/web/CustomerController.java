package com.example.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.domain.Customer;

@RestController
@RequestMapping("/api")
public class CustomerController {

        //create a list of hard coded customers
        private ArrayList<Customer> customers = new ArrayList<>(Arrays.asList(
                new Customer(1, "John Doe", "xxx@outlook.com", "111password"),
                new Customer(2, "Perry Wang", "jjj@gmail.com","222password"),
                new Customer(3, "Tom Smith", "sss@yaahoo.com", "333password")));

        //test api works
        @GetMapping
        public String test() {
            return "API works";
        }

        // get all customers
        @GetMapping("/customers")
        public ArrayList<Customer> getAllCustomers() {
            return this.customers;
        }

        // get a customer by id
        @GetMapping("/customers/{id}")
        public Customer getCustomerById(@PathVariable Long id) {
            for(int i = 0; i < customers.size(); i++) {
                if(customers.get(i).getId() == id) {
                    return customers.get(i);
                }
            }
            return null;
        }
	}


