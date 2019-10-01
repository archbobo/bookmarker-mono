package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.Bookmark;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookmarkRepository {
    List<Bookmark> findByCreatedById(String createdById, Sort sort);
    List<Bookmark> findByTag(String tagName, Sort sort);
    List<Bookmark> findAll(Sort sort);
    Bookmark insert(Bookmark bookmark);
    Bookmark findById(String id);
    long deleteById(String id);
    long deleteAll();
}
