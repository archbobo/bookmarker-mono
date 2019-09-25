package com.spring2go.bookmarker.repository.impl;

import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User create(User user) {
        return mongoTemplate.insert(user);
    }

    @Override
    public User update(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public User findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    @Override
    public User findByEmail(String email) {
        return mongoTemplate.findOne(
                query(where("email").is(email)),
                User.class
        );
    }

    @Override
    public List<User> findByIds(Collection<String> ids) {
        return mongoTemplate.find(
                query(where("id").in(ids)),
                User.class
        );
    }

    @Override
    public long deleteById(String id) {
        return mongoTemplate.remove(
                query(where("id").is(id)),
                User.class
        ).getDeletedCount();
    }

    @Override
    public boolean checkExistsByEmail(String email) {
        return mongoTemplate.exists(
                query(where("email").is(email)),
                User.class
        );
    }
}
