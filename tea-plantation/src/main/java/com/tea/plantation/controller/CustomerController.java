package com.tea.plantation.controller;

import com.tea.plantation.domain.Customer;
import com.tea.plantation.repository.CustomerRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CustomerController.CUSTOMER_API)
public class CustomerController extends CrudController<Customer, String> {
    public static final String CUSTOMER_API = "/v1/customer";

    public CustomerController(CustomerRepository repo) {
        super(repo);
    }

    @Override
    public String location() {
        return CUSTOMER_API;
    }
}
