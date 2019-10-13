package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.Role;

public interface RoleRepository {
    Role insert(Role role);
    Role findByName(String name);
    long deleteAll();
}
