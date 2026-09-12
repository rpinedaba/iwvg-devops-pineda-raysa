package es.upm.miw.devops.services;

import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseSeederService {

    private final UserRepository userRepository;

    @Autowired
    public DatabaseSeederService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.seedDatabase();
    }

    public void seedDatabase() {
        User[] users = {
                new User("1", "Daemon", "Targaryen", "daemon@got.com", "12345678A", "Dragonstone", "Dragonstone", "Crownlands", "28001", Role.ADMIN, true),
                new User("2", "Rhaenyra", "Targaryen", "rhaenyra@got.com", "23456789B", "Red Keep", "King's Landing", "Crownlands", "28002", Role.MANAGER, true),
                new User("3", "Alicent", "Hightower", "alicent@got.com", "34567890C", "Hightower", "Oldtown", "Reach", "28003", Role.OPERATOR, false),
                new User("4", "Otto", "Hightower", "otto@got.com", "45678901D", "Hightower", "Oldtown", "Reach", "28004", Role.CUSTOMER, true),
                new User("5", "Aemond", "Targaryen", null, null, null, null, null, null, Role.CUSTOMER, true)
        };
        for (User user : users) {
            this.userRepository.save(user);
        }
    }

    public void deleteAll() {
        this.userRepository.deleteAll();
    }

    public void reSeedDatabase() {
        this.deleteAll();
        this.seedDatabase();
    }
}
