package com.tea.plantation.migration;

import com.tea.plantation.domain.Customer;
import com.tea.plantation.domain.Tea;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "initialDataSetup", order = "0000")
public class PlantationInitialMigration {
    private final MongoTemplate mongoTemplate;

    @Execution
    public void initialize() {
        log.info("Run initialization migration for MongoDB");

        log.info("Creating collection for [{}] class", Tea.class);
        mongoTemplate.createCollection(Tea.class);
        List<Tea> teaData = teaData();
        mongoTemplate.insert(teaData, Tea.class);

        log.info("Creating collection for [{}] class", Customer.class);
        mongoTemplate.createCollection(Customer.class);
        List<Customer> customers = customerData();
        mongoTemplate.insert(customers, Customer.class);
        log.info("Finished initialization migration");
    }

    @RollbackExecution
    public void rollback() {
        log.info("Running rollback for initialization migration");
        mongoTemplate.dropCollection(Customer.class);
        mongoTemplate.dropCollection(Tea.class);
        log.info("Finished rollback for initialization migration");
    }

    public static List<Tea> teaData() {
        var blackRock = Tea.builder()
                .name("Black Rock")
                .type("Pu-erh")
                .build();
        var spicyLunch = Tea.builder()
                .name("Spicy lunch")
                .type("Rooibos")
                .build();
        var matchaGreenPowder = Tea.builder()
                .name("Match green powder")
                .type("Match")
                .build();
        return List.of(blackRock, spicyLunch, matchaGreenPowder);
    }

    public static List<Customer> customerData() {
        var vancoShop = Customer.builder().name("Vanco shop").build();
        var osmantusShop = Customer.builder().name("Osmantus shop").build();
        var oceanPlas = Customer.builder().name("OceanPlas").build();
        return List.of(vancoShop, osmantusShop, oceanPlas);
    }
}
