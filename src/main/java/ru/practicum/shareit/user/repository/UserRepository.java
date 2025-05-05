package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    User update(User user);

    void deleteById(Long userId);
}