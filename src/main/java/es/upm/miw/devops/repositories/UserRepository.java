package es.upm.miw.devops.repositories;

import es.upm.miw.devops.models.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public Optional<User> findById(String id) {
        return Optional.ofNullable(this.users.get(id));
    }

    public User save(User user) {
        this.users.put(user.getId(), user);
        return user;
    }

    public void deleteById(String id) {
        this.users.remove(id);
    }

    public Map<String, User> findAll() {
        return this.users;
    }

    public void deleteAll() {
        this.users.clear();
    }
}
