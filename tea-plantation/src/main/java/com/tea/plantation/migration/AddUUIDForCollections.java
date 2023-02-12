package com.tea.plantation.migration;

import com.mongodb.client.result.UpdateResult;
import com.tea.plantation.domain.Customer;
import com.tea.plantation.domain.Identifiable;
import com.tea.plantation.domain.Tea;
import com.tea.plantation.domain.UUIDEntity;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "addUUIDForCollections", order = "0001")
public class AddUUIDForCollections {
    public static final String UUID_FIELD = "uuid";
    public static final String UPC_FIELD = "upc";
    public static final String UUID_INDEX = "uuidIndex";

    private final MongoTemplate mongoTemplate;

    @Execution
    public void addUUID() {
        addUUID(Tea.class);
        createIndex(Tea.class);
        unsetField(Tea.class, UPC_FIELD);
        addUUID(Customer.class);
        createIndex(Customer.class);
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

    private <E extends UUIDEntity> void createIndex(Class<E> documentClass) {
        TextIndexDefinition index = new TextIndexDefinitionBuilder().onField(UUID_FIELD).named(UUID_INDEX).build();
        mongoTemplate.indexOps(documentClass).ensureIndex(index);
        log.info("Created index for class[{}] with name[{}]", documentClass, UUID_INDEX);
    }

    @RollbackExecution
    public void rollbackAddUUID() {
        log.warn("Running rollback for AddUUIDForCollections!");
        dropIndex(Customer.class);
        dropIndex(Tea.class);
        unsetField(Customer.class, UUID_FIELD);
        unsetField(Tea.class, UUID_FIELD);
        log.warn("Finished rollback for AddUUIDForCollections");
    }

    private <E extends UUIDEntity> void dropIndex(Class<E> documentClass) {
        IndexOperations indexOperations = mongoTemplate.indexOps(documentClass);
        boolean present = indexOperations.getIndexInfo().stream().anyMatch(index -> UUID_INDEX.equals(index.getName()));
        if (present) {
            indexOperations.dropIndex(UUID_INDEX);
        }
    }

    private <E extends UUIDEntity> void unsetField(Class<E> documentClass, String field) {
        var query = new Query();
        query.addCriteria(Criteria.where(field).exists(true));
        var update = new Update();
        update.unset(field);
        UpdateResult result = mongoTemplate.updateMulti(query, update, documentClass);
        log.info("Finished unset for field[{}] for class[{}], modified[{}]", field, documentClass, result.getModifiedCount());
    }
}
