package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.Tag;

public interface TagRepository {
    Tag findByName(String name);
}
