package es.upm.miw.devops.services;

import es.upm.miw.devops.dtos.UserActiveDto;
import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    public User update(String id, User user) {
        this.assertValidUser(user);
        this.read(id);
        user.setId(id);
        return this.userRepository.save(user);
    }

    public User updateActive(String id, Boolean active) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User id: " + id));
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active status is required");
        }
        user.setActive(active);
        return this.userRepository.save(user);
    }

    public List<User> updateActiveList(List<UserActiveDto> userActiveDtoList) {
        this.assertValidUserActiveList(userActiveDtoList);
        List<User> users = new ArrayList<>();
        for (UserActiveDto userActiveDto : userActiveDtoList) {
            User user = this.read(userActiveDto.id());
            user.setActive(userActiveDto.active());
            users.add(user);
        }
        return this.userRepository.saveAll(users);
    }

    public List<User> search(String firstName, String familyName, Boolean billable) {
        return this.userRepository.findAll().stream()
                .filter(user -> matchesFirstName(user, firstName))
                .filter(user -> matchesFamilyName(user, familyName))
                .filter(user -> matchesBillable(user, billable))
                .toList();
    }

    private void assertValidUser(User user) {
        if (!StringUtils.hasText(user.getFirstName()) || !StringUtils.hasText(user.getFamilyName())
                || user.getRole() == null || user.getActive() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "First name, family name, role and active are required");
        }
    }

    private void assertValidUserActiveList(List<UserActiveDto> userActiveDtoList) {
        if (userActiveDtoList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User list is required");
        }
        if (userActiveDtoList.stream().anyMatch(userActiveDto ->
                !StringUtils.hasText(userActiveDto.id()) || userActiveDto.active() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id and active are required");
        }
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
