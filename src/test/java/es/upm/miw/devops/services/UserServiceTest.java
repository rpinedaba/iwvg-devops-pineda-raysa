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

    @Test
    void testSearchWithoutFilters() {
        assertEquals(5, this.userService.search(null, null, null).size());
    }

    @Test
    void testSearchByFirstName() {
        assertEquals(1, this.userService.search("Daemon", null, null).size());
        assertEquals("1", this.userService.search("Daemon", null, null).getFirst().getId());
    }

    @Test
    void testSearchByFamilyName() {
        assertEquals(3, this.userService.search(null, "Targaryen", null).size());
        assertTrue(this.userService.search(null, "Targaryen", null)
                .stream().allMatch(user -> user.getFamilyName().contains("Targaryen")));
    }

    @Test
    void testSearchByBillable() {
        assertEquals(4, this.userService.search(null, null, true).size());
        assertEquals(1, this.userService.search(null, null, false).size());
        assertEquals("5", this.userService.search(null, null, false).getFirst().getId());
    }

    @Test
    void testSearchByAllFilters() {
        assertEquals(1, this.userService.search("Daemon", "Targaryen", true).size());
        assertEquals("1", this.userService.search("Daemon", "Targaryen", true).getFirst().getId());
    }
}
