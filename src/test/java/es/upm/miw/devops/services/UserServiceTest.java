package es.upm.miw.devops.services;

import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

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
    void testDeleteExistingUser() {
        this.userService.delete("1");
        assertEquals(4, this.userService.search(null, null, null).size());
        assertThrows(NotFoundException.class, () -> this.userService.read("1"));
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(NotFoundException.class, () -> this.userService.delete("non-existent-id"));
        assertEquals(5, this.userService.search(null, null, null).size());
    }

    @Test
    void testUpdateActiveExistingUser() {
        User updatedUser = this.userService.updateActive("1", false);
        assertNotNull(updatedUser);
        assertEquals("1", updatedUser.getId());
        assertFalse(updatedUser.getActive());
        assertFalse(this.userService.read("1").getActive());
    }

    @Test
    void testUpdateActiveNotFound() {
        assertThrows(NotFoundException.class, () -> this.userService.updateActive("non-existent-id", true));
    }

    @Test
    void testUpdateActiveNullValue() {
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> this.userService.updateActive("1", null));
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

    @Test
    void testUpdate() {
        User user = new User("1", "Aegon", "Targaryen", "aegon@got.com", "56789012E",
                "Red Keep", "King's Landing", "Crownlands", "28005", Role.MANAGER, false);
        User updatedUser = this.userService.update("1", user);
        assertNotNull(updatedUser);
        assertEquals("1", updatedUser.getId());
        User databaseUser = this.userService.read("1");
        assertEquals("Aegon", databaseUser.getFirstName());
        assertEquals("Targaryen", databaseUser.getFamilyName());
        assertEquals("aegon@got.com", databaseUser.getEmail());
        assertEquals("56789012E", databaseUser.getIdentity());
        assertEquals("Red Keep", databaseUser.getAddress());
        assertEquals("King's Landing", databaseUser.getCity());
        assertEquals("Crownlands", databaseUser.getProvince());
        assertEquals("28005", databaseUser.getPostalCode());
        assertEquals(Role.MANAGER, databaseUser.getRole());
        assertFalse(databaseUser.getActive());
    }

    @Test
    void testUpdateRemovesOptionalFields() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, true);
        this.userService.update("1", user);
        User databaseUser = this.userService.read("1");
        assertEquals("Aegon", databaseUser.getFirstName());
        assertNull(databaseUser.getEmail());
        assertNull(databaseUser.getIdentity());
        assertNull(databaseUser.getAddress());
        assertNull(databaseUser.getCity());
        assertNull(databaseUser.getProvince());
        assertNull(databaseUser.getPostalCode());
    }

    @Test
    void testUpdateFillsNullFields() {
        User user = new User("5", "Aemond", "Targaryen", "aemond@got.com", "56789012E",
                "Red Keep", "King's Landing", "Crownlands", "28005", Role.CUSTOMER, true);
        this.userService.update("5", user);
        User databaseUser = this.userService.read("5");
        assertEquals("aemond@got.com", databaseUser.getEmail());
        assertEquals("56789012E", databaseUser.getIdentity());
        assertEquals("Red Keep", databaseUser.getAddress());
        assertEquals("King's Landing", databaseUser.getCity());
        assertEquals("Crownlands", databaseUser.getProvince());
        assertEquals("28005", databaseUser.getPostalCode());
    }

    @Test
    void testUpdateNotFound() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, true);
        assertThrows(NotFoundException.class, () -> this.userService.update("non-existent-id", user));
    }

    @Test
    void testUpdateWithoutFirstName() {
        User user = new User(null, null, "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, true);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("1", user));
    }

    @Test
    void testUpdateWithoutFamilyName() {
        User user = new User(null, "Aegon", "   ", null, null,
                null, null, null, null, Role.MANAGER, true);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("1", user));
    }

    @Test
    void testUpdateWithoutRole() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, null, true);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("1", user));
    }

    @Test
    void testUpdateWithoutActive() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, null);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("1", user));
    }

    @Test
    void testUpdateIgnoresBodyId() {
        User user = new User("99", "Aegon", "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, true);
        User updatedUser = this.userService.update("1", user);
        assertEquals("1", updatedUser.getId());
        assertEquals("Aegon", this.userService.read("1").getFirstName());
        assertEquals(5, this.userService.search(null, null, null).size());
        assertThrows(NotFoundException.class, () -> this.userService.read("99"));
    }
}
