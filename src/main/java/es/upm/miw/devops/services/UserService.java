package es.upm.miw.devops.services;

import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

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

    public void delete(String id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User id: " + id));
        this.userRepository.delete(user);
    }

    public List<User> search(String firstName, String familyName, Boolean billable) {
        return this.userRepository.findAll().stream()
                .filter(user -> matchesFirstName(user, firstName))
                .filter(user -> matchesFamilyName(user, familyName))
                .filter(user -> matchesBillable(user, billable))
                .toList();
    }

    private boolean matchesFirstName(User user, String firstName) {
        return !StringUtils.hasText(firstName)
                || containsIgnoreCase(user.getFirstName(), firstName);
    }

    private boolean matchesFamilyName(User user, String familyName) {
        return !StringUtils.hasText(familyName)
                || containsIgnoreCase(user.getFamilyName(), familyName);
    }

    private boolean matchesBillable(User user, Boolean billable) {
        if (billable == null) {
            return true;
        }
        boolean isBillable = isBillableUser(user);
        return isBillable == billable;
    }

    private boolean isBillableUser(User user) {
        return user != null
                && StringUtils.hasText(user.getFirstName())
                && StringUtils.hasText(user.getFamilyName())
                && StringUtils.hasText(user.getEmail())
                && StringUtils.hasText(user.getIdentity())
                && StringUtils.hasText(user.getAddress())
                && StringUtils.hasText(user.getCity())
                && StringUtils.hasText(user.getProvince())
                && StringUtils.hasText(user.getPostalCode());
    }

    private boolean containsIgnoreCase(String value, String pattern) {
        return value != null
                && pattern != null
                && value.toLowerCase(Locale.ROOT).contains(pattern.toLowerCase(Locale.ROOT));
    }
}
