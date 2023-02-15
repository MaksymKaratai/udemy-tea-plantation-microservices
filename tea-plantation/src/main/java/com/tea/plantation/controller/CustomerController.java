package com.tea.plantation.controller;

import com.tea.plantation.dto.CustomerDto;
import com.tea.plantation.services.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CustomerController.CUSTOMER_API)
public class CustomerController extends CrudController<CustomerDto, String> {
    public static final String CUSTOMER_API = "/v1/customer";

    public CustomerController(CustomerService service) {
        super(service);
    }

    @Override
    public String location() {
        return CUSTOMER_API;
    }
}
