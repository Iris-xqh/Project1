package com.example.web;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.domain.Customer;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

//	@GetMapping("/all")
//	public List<Customer> getAllCustomers() {
//
//	}
    @GetMapping("/all")
    public String getAllCustomers() {
        return "Hello World";
    }
}
