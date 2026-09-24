package es.upm.miw.devops.dtos;

import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;

public record UserDto(String firstName, String familyName, String email, String identity,
                      String address, String city, String province, String postalCode,
                      Role role, Boolean active) {

    public User toUser() {
        return new User(null, firstName, familyName, email, identity,
                address, city, province, postalCode, role, active);
    }
}
