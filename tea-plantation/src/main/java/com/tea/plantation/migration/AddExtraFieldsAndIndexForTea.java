package com.tea.plantation.migration;

import com.tea.plantation.domain.Tea;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "addExtraFieldsAndIndexForTea", order = "0002", transactional = false)//disable transactions due to indexes
public class AddExtraFieldsAndIndexForTea {
    final String UPC_FIELD = "upc";
    final String UUID_FIELD = "uuid";
    final String PRICE_FIELD = "price";

    final MongoTemplate mongoTemplate;

    @Execution
    public void update() {
        addFields();
        MigrationUtils.dropIndex(mongoTemplate, Tea.class, Tea.UUID_INDEX);
        // recreate due to different type
        MigrationUtils.createIndex(mongoTemplate, Tea.class, UUID_FIELD, Tea.UUID_INDEX);
        MigrationUtils.createIndex(mongoTemplate, Tea.class, UPC_FIELD, Tea.UPC_INDEX);
    }

    private void addFields() {
        log.info("Add upc and price fields");
        List<Tea> documents = mongoTemplate.findAll(Tea.class);
        Faker faker = new Faker();
        for (var doc : documents) {
            doc.setUpc(String.valueOf(faker.barcode().ean8()));
            doc.setPrice(new BigDecimal(faker.commerce().price()));
            log.info("Set upc[{}] and price[{}] for document with id[{}]", doc.getUpc(), doc.getPrice(), doc.getId());
            mongoTemplate.save(doc);
        }
        log.info("Finished adding fields");
    }

    @RollbackExecution
    public void rollback() {
        MigrationUtils.dropIndex(mongoTemplate, Tea.class, Tea.UPC_INDEX);
        MigrationUtils.unsetField(mongoTemplate, Tea.class, UPC_FIELD);
        MigrationUtils.unsetField(mongoTemplate, Tea.class, PRICE_FIELD);
    }
}
