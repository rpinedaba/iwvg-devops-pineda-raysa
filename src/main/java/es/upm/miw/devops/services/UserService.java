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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String USER_ID = "User id: ";

    private static final Comparator<User> BY_NUMERIC_ID =
            Comparator.comparingInt((User user) -> user.getId().length())
                    .thenComparing(User::getId);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User read(String id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_ID + id));
    }

    public void delete(String id) {
        this.userRepository.delete(this.read(id));
    }

    public User update(String id, User user) {
        this.assertValidUser(user);
        this.read(id);
        user.setId(id);
        return this.userRepository.save(user);
    }

    public User updateActive(String id, Boolean active) {
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active status is required");
        }
        User user = this.read(id);
        user.setActive(active);
        return this.userRepository.save(user);
    }

    public List<User> updateActiveList(List<UserActiveDto> userActiveDtoList) {
        this.assertValidUserActiveList(userActiveDtoList);
        Map<String, User> databaseUsers = this.userRepository
                .findAllById(userActiveDtoList.stream().map(UserActiveDto::id).toList())
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        List<User> users = new ArrayList<>();
        for (UserActiveDto userActiveDto : userActiveDtoList) {
            User user = databaseUsers.get(userActiveDto.id());
            if (user == null) {
                throw new NotFoundException(USER_ID + userActiveDto.id());
            }
            user.setActive(userActiveDto.active());
            users.add(user);
        }
        return this.userRepository.saveAll(users);
    }

    public List<User> search(String firstName, String familyName, Boolean billable) {
        return this.userRepository.findAll().stream()
                .filter(user -> matches(user.getFirstName(), firstName))
                .filter(user -> matches(user.getFamilyName(), familyName))
                .filter(user -> matchesBillable(user, billable))
                .sorted(BY_NUMERIC_ID)
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

    private boolean matches(String value, String pattern) {
        return !StringUtils.hasText(pattern)
                || value.toLowerCase(Locale.ROOT).contains(pattern.toLowerCase(Locale.ROOT));
    }

    private boolean matchesBillable(User user, Boolean billable) {
        return billable == null || isBillableUser(user) == billable;
    }

    private boolean isBillableUser(User user) {
        return StringUtils.hasText(user.getFirstName())
                && StringUtils.hasText(user.getFamilyName())
                && StringUtils.hasText(user.getEmail())
                && StringUtils.hasText(user.getIdentity())
                && StringUtils.hasText(user.getAddress())
                && StringUtils.hasText(user.getCity())
                && StringUtils.hasText(user.getProvince())
                && StringUtils.hasText(user.getPostalCode());
    }
}
