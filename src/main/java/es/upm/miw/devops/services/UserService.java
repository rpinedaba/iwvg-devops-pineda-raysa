package es.upm.miw.devops.services;

import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User read(String id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User id: " + id));
    }
}
