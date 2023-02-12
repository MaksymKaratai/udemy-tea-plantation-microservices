package com.tea.plantation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tea.plantation.domain.Customer;
import com.tea.plantation.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static com.tea.plantation.controller.CustomerController.CUSTOMER_API;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MongoBeansStub.class)
@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CustomerRepository customerRepository;

    @Test
    void getById() throws Exception {
        var customer = testCustomer();
        var id = "1";
        customer.setId(id);
        var customerJson = objectMapper.writeValueAsString(customer);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        mockMvc.perform(get(CUSTOMER_API + "/" + id))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(content().json(customerJson));
    }

    @Test
    void create() throws Exception {
        var customer = testCustomer();
        var id = "1";
        var customerJson = objectMapper.writeValueAsString(customer);
        customer.setId(id);

        when(customerRepository.save(any())).thenReturn(customer);

        mockMvc.perform(post(CUSTOMER_API).contentType(MediaType.APPLICATION_JSON).content(customerJson))
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists(LOCATION));
    }

    @Test
    void deleteById() throws Exception {
        var id = "1";

        doNothing().when(customerRepository).deleteById(id);

        mockMvc.perform(delete(CUSTOMER_API + "/" + id))
                .andDo(log())
                .andExpect(status().isNoContent());
    }

    private Customer testCustomer() {
        return Customer.builder().name("Vanco shop").build();
    }
}