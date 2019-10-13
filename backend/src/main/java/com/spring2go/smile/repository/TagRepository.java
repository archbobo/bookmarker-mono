package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.Tag;

import java.util.List;

public interface TagRepository {
    Tag insert(Tag tag);
    Tag findByName(String name);
    List<Tag> fingAll();
    long deleteAll();
}
