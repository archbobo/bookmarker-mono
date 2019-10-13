package com.spring2go.bookmarker.repository.impl;

import com.spring2go.bookmarker.model.Bookmark;
import com.spring2go.bookmarker.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class BookmarkRepositoryImpl implements BookmarkRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Bookmark> findByCreatedById(String createdById, Sort sort) {
        return mongoTemplate.find(
                query(where("createdBy").is(createdById)).with(sort),
                Bookmark.class
        );
    }

    @Override
    public List<Bookmark> findByTag(String tagName, Sort sort) {
        List<Bookmark> bookmarkList = mongoTemplate.find(
                query(where("tags").in(tagName)).with(sort),
                Bookmark.class
        );
        return bookmarkList;
    }

    @Override
    public List<Bookmark> findAll(Sort sort) {
        Query query = new Query();
        query.with(sort);
        return mongoTemplate.find(query, Bookmark.class);
    }

    @Override
    public Bookmark insert(Bookmark bookmark) {
        return mongoTemplate.insert(bookmark);
    }

    @Override
    public Bookmark findById(String id) {
        return mongoTemplate.findById(id, Bookmark.class);
    }

    @Override
    public long deleteById(String id) {
        return mongoTemplate.remove(
                query(where("id").is(id)),
                Bookmark.class
        ).getDeletedCount();
    }

    @Override
    public long deleteAll() {
        return mongoTemplate.remove(Bookmark.class).all().getDeletedCount();
    }
}
