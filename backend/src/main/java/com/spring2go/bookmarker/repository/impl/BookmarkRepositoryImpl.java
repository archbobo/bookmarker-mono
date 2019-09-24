package com.spring2go.bookmarker.repository.impl;

import com.spring2go.bookmarker.model.Bookmark;
import com.spring2go.bookmarker.model.Tag;
import com.spring2go.bookmarker.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        Tag tag = mongoTemplate.findOne(
                query(where("name").is(tagName)),
                Tag.class
        );
        if (tag == null) return new ArrayList<>();
        List<Bookmark> bookmarkList = mongoTemplate.find(
                query(where("tagIds").in(tag.getId())).with(sort),
                Bookmark.class
        );
        return bookmarkList;
    }
}
