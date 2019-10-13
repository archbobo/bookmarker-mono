package com.spring2go.bookmarker.repository.impl;

import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Role insert(Role role) {
        return mongoTemplate.insert(role);
    }

    @Override
    public Role findByName(String name) {
        return mongoTemplate.findOne(
                query(where("name").is(name)),
                Role.class
        );
    }

    @Override
    public long deleteAll() {
        return mongoTemplate.remove(Role.class).all().getDeletedCount();
    }
}
