package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    User create(User user);
    User update(User user);
    User findById(String id);
    User findByEmail(String email);
    List<User> findByIds(Collection<String> ids);
    long deleteById(String id);
    boolean checkExistsByEmail(String email);
}
