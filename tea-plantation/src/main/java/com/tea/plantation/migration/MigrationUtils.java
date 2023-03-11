package com.tea.plantation.migration;

import com.mongodb.client.result.UpdateResult;
import com.tea.common.domain.UUIDEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MigrationUtils {
    static <E extends UUIDEntity> void createIndex(MongoTemplate mongoTemplate,
                                                   Class<E> documentClass, String field, String name) {
        Index index = new Index().on(field, Sort.Direction.ASC).unique().named(name);
        mongoTemplate.indexOps(documentClass).ensureIndex(index);
        log.info("Created index for class[{}] with name[{}]", documentClass, name);
    }

    static <E extends UUIDEntity> void dropIndex(MongoTemplate mongoTemplate, Class<E> documentClass, String name) {
        IndexOperations indexOperations = mongoTemplate.indexOps(documentClass);
        boolean present = indexOperations.getIndexInfo().stream().anyMatch(index -> name.equals(index.getName()));
        if (present) {
            indexOperations.dropIndex(name);
        }
    }

    static <E extends UUIDEntity> void unsetField(MongoTemplate mongoTemplate, Class<E> documentClass, String field) {
        var query = new Query();
        query.addCriteria(Criteria.where(field).exists(true));
        var update = new Update();
        update.unset(field);
        UpdateResult result = mongoTemplate.updateMulti(query, update, documentClass);
        log.info("Finished unset for field[{}] for class[{}], modified[{}]", field, documentClass, result.getModifiedCount());
    }
}
