package com.spring2go.bookmarker.repository;

import com.spring2go.bookmarker.model.Role;

public interface RoleRepository {
    Role findByName(String name);
}
