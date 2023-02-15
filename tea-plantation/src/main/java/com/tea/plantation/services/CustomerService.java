package com.tea.plantation.services;

import com.tea.plantation.domain.Customer;
import com.tea.plantation.dto.CustomerDto;
import com.tea.plantation.mapper.CustomerMapper;
import com.tea.plantation.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends BasicService<Customer, CustomerDto, String> {
    public CustomerService(CustomerMapper mapper, CustomerRepository repository) {
        super(mapper, repository);
    }
}
