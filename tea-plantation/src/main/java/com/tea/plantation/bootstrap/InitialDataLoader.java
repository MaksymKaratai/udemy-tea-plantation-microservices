package com.tea.plantation.bootstrap;

import com.tea.plantation.domain.Customer;
import com.tea.plantation.domain.Tea;
import com.tea.plantation.repository.CustomerRepository;
import com.tea.plantation.repository.TeaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class InitialDataLoader implements CommandLineRunner {
    private final TeaRepository teaRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        if (teaRepository.count() == 0) {
            loadTeaData();
        }
        if (customerRepository.count() == 0) {
            loadCustomerData();
        }
    }

    private void loadTeaData() {
        log.info("Initializing data examples of Tea");
        var teaList = teaRepository.saveAll(teaData());
        log.info("Loaded next objects: [{}]", teaList);
    }

    public static List<Tea> teaData() {
        var blackRock = Tea.builder()
                .name("Black Rock")
                .type("Pu-erh")
                .upc(72527273070L)
                .build();
        var spicyLunch = Tea.builder()
                .name("Spicy lunch")
                .type("Rooibos")
                .upc(72527273071L)
                .build();
        var matchaGreenPowder = Tea.builder()
                .name("Match green powder")
                .type("Match")
                .upc(72527273072L)
                .build();
        return List.of(blackRock, spicyLunch, matchaGreenPowder);
    }

    private void loadCustomerData() {
        log.info("Initializing data examples of Customer");
        List<Customer> list = customerData();
        var customerList = customerRepository.saveAll(list);
        log.info("Loaded next objects: [{}]", customerList);
    }

    public static List<Customer> customerData() {
        var vancoShop = Customer.builder().name("Vanco shop").build();
        var osmantusShop = Customer.builder().name("Osmantus shop").build();
        var oceanPlas = Customer.builder().name("OceanPlas").build();
        return List.of(vancoShop, osmantusShop, oceanPlas);
    }
}
