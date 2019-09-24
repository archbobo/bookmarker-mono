package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.User;

public interface UserRepository {
    User create(User user);
    User update(User user);
    User findById(String id);
    User findByEmail(String email);
    long deleteById(String id);
    boolean checkExistsByEmail(String email);
}
