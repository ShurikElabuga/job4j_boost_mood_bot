package ru.job4j.refactoring.repository;

import ru.job4j.refactoring.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findByClientId(Long clientId);

    void add(User user);
}
