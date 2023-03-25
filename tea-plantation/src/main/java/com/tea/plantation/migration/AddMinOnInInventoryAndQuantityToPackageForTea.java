package com.tea.plantation.migration;

import com.tea.plantation.domain.Tea;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "AddMinOnInInventoryAndQuantityToPackageForTea", order = "0003")
public class AddMinOnInInventoryAndQuantityToPackageForTea {
    private static final String QUANTITY_FIELD = "quantityToPackage";
    private static final String AMOUNT_FIELD = "minimalAmountInInventory";

    private final MongoTemplate template;

    @Execution
    public void update() {
        log.info("Add quantityToPackage and minimalAmountInInventory fields");
        List<Tea> documents = template.findAll(Tea.class);
        for (var doc : documents) {
            var min = ThreadLocalRandom.current().nextInt(10, 20);
            var toPackage = ThreadLocalRandom.current().nextInt(10);
            doc.setQuantityToPackage(toPackage);
            doc.setMinimalAmountInInventory(min);
            log.info("Set {} to [{}] and {} to [{}] for document with id[{}]",
                QUANTITY_FIELD, doc.getQuantityToPackage(), AMOUNT_FIELD, doc.getMinimalAmountInInventory(), doc.getId());
            template.save(doc);
        }
        log.info("Finished adding fields");
    }

    @RollbackExecution
    public void rollback() {
        MigrationUtils.unsetField(template, Tea.class, AMOUNT_FIELD);
        MigrationUtils.unsetField(template, Tea.class, QUANTITY_FIELD);
    }
}
