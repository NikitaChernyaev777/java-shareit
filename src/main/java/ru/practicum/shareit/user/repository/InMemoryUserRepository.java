package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final AtomicLong newUserId = new AtomicLong(0);

    @Override
    public User save(User user) {
        user.setId(generateUserId());
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public User update(User user) {
        User existingUser = usersById.get(user.getId());

        if (existingUser != null && !existingUser.getEmail().equals(user.getEmail())) {
            usersByEmail.remove(existingUser.getEmail());
        }

        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);

        return user;
    }

    @Override
    public void deleteById(Long userId) {
        User deletedUser = usersById.remove(userId);
        if (deletedUser != null) {
            usersByEmail.remove(deletedUser.getEmail());
        }
    }

    private Long generateUserId() {
        return newUserId.incrementAndGet();
    }
}