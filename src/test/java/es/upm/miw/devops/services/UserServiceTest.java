package es.upm.miw.devops.services;

import es.upm.miw.devops.dtos.UserActiveDto;
import es.upm.miw.devops.exceptions.NotFoundException;
import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @Autowired
    private UserRepository userRepository;

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
        User updatedUser = this.userService.updateActive("2", false);
        assertNotNull(updatedUser);
        assertEquals("2", updatedUser.getId());
        assertFalse(updatedUser.getActive());
        assertFalse(this.userService.read("2").getActive());
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
        User user = new User("2", "Aegon", "Targaryen", "aegon@got.com", "56789012E",
                "Red Keep", "King's Landing", "Crownlands", "28005", Role.MANAGER, false);
        User updatedUser = this.userService.update("2", user);
        assertNotNull(updatedUser);
        assertEquals("2", updatedUser.getId());
        User databaseUser = this.userService.read("2");
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

    @Test
    void testUpdateActiveList() {
        List<User> updatedUsers = this.userService.updateActiveList(
                List.of(new UserActiveDto("2", false), new UserActiveDto("3", true)));
        assertEquals(2, updatedUsers.size());
        assertFalse(this.userService.read("2").getActive());
        assertTrue(this.userService.read("3").getActive());
    }

    @Test
    void testUpdateActiveListNotFound() {
        List<UserActiveDto> userActiveDtoList = List.of(new UserActiveDto("non-existent-id", false));
        assertThrows(NotFoundException.class, () -> this.userService.updateActiveList(userActiveDtoList));
    }

    @Test
    void testUpdateActiveListNotFoundDoesNotUpdateAnyUser() {
        List<UserActiveDto> userActiveDtoList =
                List.of(new UserActiveDto("2", false), new UserActiveDto("non-existent-id", false));
        assertThrows(NotFoundException.class, () -> this.userService.updateActiveList(userActiveDtoList));
        assertTrue(this.userService.read("2").getActive());
    }

    @Test
    void testUpdateActiveListWithoutId() {
        List<UserActiveDto> userActiveDtoList = List.of(new UserActiveDto("   ", false));
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActiveList(userActiveDtoList));
    }

    @Test
    void testUpdateActiveListWithoutActive() {
        List<UserActiveDto> userActiveDtoList = List.of(new UserActiveDto("1", null));
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActiveList(userActiveDtoList));
    }

    @Test
    void testUpdateActiveListEmpty() {
        List<UserActiveDto> userActiveDtoList = List.of();
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActiveList(userActiveDtoList));
    }

    @Test
    void testSearchOrderedById() {
        assertEquals(List.of("1", "2", "3", "4", "5"), this.searchedIds());
        this.userService.updateActive("2", false);
        assertEquals(List.of("1", "2", "3", "4", "5"), this.searchedIds());
    }

    @Test
    void testSearchOrderedByIdWithTwoDigits() {
        this.userRepository.save(new User("10", "Helaena", "Targaryen", null, null,
                null, null, null, null, Role.CUSTOMER, true));
        assertEquals(List.of("1", "2", "3", "4", "5", "10"), this.searchedIds());
    }

    @Test
    void testSearchByBillableWithoutIdentity() {
        this.updateBillableUserWithout(User::setIdentity);
        assertEquals(List.of("1", "5"), this.searchedIds(false));
    }

    @Test
    void testSearchByBillableWithoutAddress() {
        this.updateBillableUserWithout(User::setAddress);
        assertEquals(List.of("1", "5"), this.searchedIds(false));
    }

    @Test
    void testSearchByBillableWithoutCity() {
        this.updateBillableUserWithout(User::setCity);
        assertEquals(List.of("1", "5"), this.searchedIds(false));
    }

    @Test
    void testSearchByBillableWithoutProvince() {
        this.updateBillableUserWithout(User::setProvince);
        assertEquals(List.of("1", "5"), this.searchedIds(false));
    }

    @Test
    void testSearchByBillableWithoutPostalCode() {
        this.updateBillableUserWithout(User::setPostalCode);
        assertEquals(List.of("1", "5"), this.searchedIds(false));
    }

    private void updateBillableUserWithout(BiConsumer<User, String> emptyField) {
        User user = new User("1", "Daemon", "Targaryen", "daemon@got.com", "12345678A",
                "Dragonstone", "Dragonstone", "Crownlands", "28001", Role.ADMIN, true);
        emptyField.accept(user, null);
        this.userService.update("1", user);
    }

    private List<String> searchedIds() {
        return this.searchedIds(null);
    }

    private List<String> searchedIds(Boolean billable) {
        return this.userService.search(null, null, billable).stream()
                .map(User::getId)
                .toList();
    }

    @Test
    void testUpdateActiveAdminCanNotBeDeactivated() {
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActive("1", false));
        assertTrue(this.userService.read("1").getActive());
    }

    @Test
    void testUpdateActiveAdminCanBeActivated() {
        assertTrue(this.userService.updateActive("1", true).getActive());
    }

    @Test
    void testUpdateAdminCanNotBeDeactivated() {
        User user = new User("1", "Daemon", "Targaryen", "daemon@got.com", "12345678A",
                "Dragonstone", "Dragonstone", "Crownlands", "28001", Role.ADMIN, false);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("1", user));
        assertTrue(this.userService.read("1").getActive());
    }

    @Test
    void testUpdateCanNotSaveAnInactiveAdmin() {
        User user = new User("2", "Rhaenyra", "Targaryen", null, null,
                null, null, null, null, Role.ADMIN, false);
        assertThrows(ResponseStatusException.class, () -> this.userService.update("2", user));
        assertTrue(this.userService.read("2").getActive());
    }

    @Test
    void testUpdateAdminCanBeDeactivatedWhenTheRoleChanges() {
        User user = new User("1", "Daemon", "Targaryen", null, null,
                null, null, null, null, Role.CUSTOMER, false);
        User updatedUser = this.userService.update("1", user);
        assertEquals(Role.CUSTOMER, updatedUser.getRole());
        assertFalse(this.userService.read("1").getActive());
    }

    @Test
    void testUpdateActiveListAdminCanNotBeDeactivatedWhenRepeated() {
        List<UserActiveDto> userActiveDtoList =
                List.of(new UserActiveDto("1", true), new UserActiveDto("1", false));
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActiveList(userActiveDtoList));
        assertTrue(this.userService.read("1").getActive());
    }

    @Test
    void testUpdateActiveListAdminCanNotBeDeactivated() {
        List<UserActiveDto> userActiveDtoList =
                List.of(new UserActiveDto("2", false), new UserActiveDto("1", false));
        assertThrows(ResponseStatusException.class, () -> this.userService.updateActiveList(userActiveDtoList));
        assertTrue(this.userService.read("1").getActive());
        assertTrue(this.userService.read("2").getActive());
    }
}
