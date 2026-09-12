package es.upm.miw.devops.services;

import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @BeforeEach
    void resetDatabase() {
        this.databaseSeederService.reSeedDatabase();
    }

    @Test
    void testRead() {
        User user = this.userService.read("1");
        assertNotNull(user);
        assertEquals("1", user.getId());
        assertEquals("Daemon", user.getFirstName());
        assertEquals("Targaryen", user.getFamilyName());
        assertEquals("daemon@got.com", user.getEmail());
        assertEquals("12345678A", user.getIdentity());
        assertEquals("Dragonstone", user.getAddress());
        assertEquals("Dragonstone", user.getCity());
        assertEquals("Crownlands", user.getProvince());
        assertEquals("28001", user.getPostalCode());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    void testReadUserWithNullFields() {
        User user = this.userService.read("5");
        assertNotNull(user);
        assertEquals("5", user.getId());
        assertEquals("Aemond", user.getFirstName());
        assertEquals("Targaryen", user.getFamilyName());
        assertNull(user.getEmail());
        assertNull(user.getIdentity());
        assertNull(user.getAddress());
        assertNull(user.getCity());
        assertNull(user.getProvince());
        assertNull(user.getPostalCode());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    void testReadNotFound() {
        assertThrows(NotFoundException.class, () -> this.userService.read("non-existent-id"));
    }
}
