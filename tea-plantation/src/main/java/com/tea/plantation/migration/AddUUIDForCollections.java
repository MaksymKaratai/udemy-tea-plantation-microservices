package com.tea.plantation.migration;

import com.tea.common.domain.Identifiable;
import com.tea.common.domain.UUIDEntity;
import com.tea.plantation.domain.Tea;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.UUID;

/**
 *  When creating an index inside a transaction, the index to create must be on either:
 *    a non-existing collection. The collection is created as part of the operation.
 *    a new empty collection created earlier in the same transaction.
 *  Disable transactions due to that :)
 */
@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "addUUIDForCollections", order = "0001", transactional = false)
public class AddUUIDForCollections {
    public static final String UUID_FIELD = "uuid";
    public static final String UPC_FIELD = "upc";
    public static final String UUID_INDEX = "uuidIndex";

    private final MongoTemplate mongoTemplate;

    @Execution
    public void addUUID() {
        addUUID(Tea.class);
        MigrationUtils.createIndex(mongoTemplate, Tea.class, UUID_FIELD, UUID_INDEX);
        MigrationUtils.unsetField(mongoTemplate, Tea.class, UPC_FIELD);
    }

    private <ID, E extends UUIDEntity & Identifiable<ID>> void addUUID(Class<E> documentClass) {
        log.info("Add uuid for [{}]", documentClass);
        List<E> documents = mongoTemplate.findAll(documentClass);
        for (var doc : documents) {
            doc.setUuid(UUID.randomUUID());
            log.info("Set uuid[{}] for document with id[{}]", doc.getUuid(), doc.getId());
            mongoTemplate.save(doc);
        }
        log.info("Finished collection[{}] update", documentClass);
    }

    @RollbackExecution
    public void rollbackAddUUID() {
        log.warn("Running rollback for AddUUIDForCollections!");
        MigrationUtils.dropIndex(mongoTemplate, Tea.class, UUID_INDEX);
        MigrationUtils.unsetField(mongoTemplate, Tea.class, UUID_FIELD);
        log.warn("Finished rollback for AddUUIDForCollections");
    }
}
