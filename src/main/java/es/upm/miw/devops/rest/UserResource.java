package es.upm.miw.devops.rest;

import es.upm.miw.devops.models.User;
import es.upm.miw.devops.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    public static final String USERS = "/user";
    public static final String ID_ID = "/{id}";

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(UserResource.ID_ID)
    public User read(@PathVariable String id) {
        return this.userService.read(id);
    }
}
