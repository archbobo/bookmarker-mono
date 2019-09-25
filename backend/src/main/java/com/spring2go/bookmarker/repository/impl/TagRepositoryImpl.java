package com.spring2go.bookmarker.repository.impl;

import com.spring2go.bookmarker.model.Tag;
import com.spring2go.bookmarker.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class TagRepositoryImpl implements TagRepository {
    @Autowired
    private  MongoTemplate mongoTemplate;

    @Override
    public Tag insert(Tag tag) {
        return mongoTemplate.insert(tag);
    }

    @Override
    public Tag findByName(String name) {
        return mongoTemplate.findOne(
                query(where("name").is(name)),
                Tag.class
        );
    }
}
