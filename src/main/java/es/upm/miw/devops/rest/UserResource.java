package es.upm.miw.devops.rest;

import es.upm.miw.devops.dtos.UserActiveDto;
import es.upm.miw.devops.dtos.UserDto;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserResource {

    public static final String USER = "/user";
    public static final String USERS = "/users";
    public static final String ID_ID = "/{id}";

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(UserResource.USERS)
    public List<User> search(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) Boolean billable) {
        return this.userService.search(firstName, familyName, billable);
    }

    @GetMapping(UserResource.USER + UserResource.ID_ID)
    public User read(@PathVariable String id) {
        return this.userService.read(id);
    }

    @DeleteMapping(UserResource.USER + UserResource.ID_ID)
    public void delete(@PathVariable String id) {
        this.userService.delete(id);
    }

    @PutMapping(UserResource.USER + UserResource.ID_ID)
    public User update(@PathVariable String id, @RequestBody UserDto userDto) {
        return this.userService.update(id, userDto.toUser());
    }

    @PatchMapping(UserResource.USER)
    public List<User> updateActiveList(@RequestBody List<UserActiveDto> userActiveDtoList) {
        return this.userService.updateActiveList(userActiveDtoList);
    }

    @PutMapping(UserResource.USER + UserResource.ID_ID + "/active")
    public User updateActive(@PathVariable String id, @RequestBody Boolean active) {
        return this.userService.updateActive(id, active);
    }
}
