package com.dtao.alien.config;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexCreator {

    private final MongoTemplate mongoTemplate;

    public MongoIndexCreator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        Index ttlIndex = new Index()
                .on("expiresAt", Sort.Direction.ASC)
                .expire(0);
        mongoTemplate.indexOps("email_verifications").createIndex(ttlIndex);
    }
}
