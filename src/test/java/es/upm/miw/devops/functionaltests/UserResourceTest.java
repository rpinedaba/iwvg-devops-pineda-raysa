package es.upm.miw.devops.functionaltests;

import es.upm.miw.devops.dtos.UserActiveDto;
import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.rest.UserResource;
import es.upm.miw.devops.services.DatabaseSeederService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserResourceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @BeforeEach
    void resetDatabase() {
        this.databaseSeederService.reSeedDatabase();
    }

    @Test
    void testRead() {
        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
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
                });
    }

    @Test
    void testReadUserWithNullFields() {
        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
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
                });
    }

    @Test
    void testReadNotFound() {
        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        this.webTestClient.delete()
                .uri(UserResource.USER + UserResource.ID_ID, "1")
                .exchange()
                .expectStatus().isOk();

        this.webTestClient.get()
                .uri(UserResource.USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertEquals(4, users.size()));
    }

    @Test
    void testDeleteNotFound() {
        this.webTestClient.delete()
                .uri(UserResource.USER + UserResource.ID_ID, "999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActive() {
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID + "/active", "2")
                .bodyValue(false)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals("2", user.getId());
                    assertFalse(user.getActive());
                });

        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> assertFalse(user.getActive()));
    }

    @Test
    void testUpdateActiveNotFound() {
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID + "/active", "999")
                .bodyValue(false)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveNullValue() {
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID + "/active", "1")
                .bodyValue(java.util.Map.of())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testSearchWithoutFilters() {
        this.webTestClient.get()
                .uri(UserResource.USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertEquals(5, users.size()));
    }

    @Test
    void testSearchByFirstName() {
        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(UserResource.USERS)
                        .queryParam("firstName", "Daemon")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> {
                    assertEquals(1, users.size());
                    assertEquals("1", users.getFirst().getId());
                });
    }

    @Test
    void testSearchByFamilyName() {
        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(UserResource.USERS)
                        .queryParam("familyName", "Targaryen")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertEquals(3, users.size()));
    }

    @Test
    void testSearchByBillable() {
        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(UserResource.USERS)
                        .queryParam("billable", "true")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertEquals(4, users.size()));

        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(UserResource.USERS)
                        .queryParam("billable", "false")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> {
                    assertEquals(1, users.size());
                    assertEquals("5", users.getFirst().getId());
                });
    }

    @Test
    void testSearchByAllFilters() {
        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(UserResource.USERS)
                        .queryParam("firstName", "Daemon")
                        .queryParam("familyName", "Targaryen")
                        .queryParam("billable", "true")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> {
                    assertEquals(1, users.size());
                    assertEquals("1", users.getFirst().getId());
                });
    }

    @Test
    void testUpdate() {
        User user = new User("2", "Aegon", "Targaryen", "aegon@got.com", "56789012E",
                "Red Keep", "King's Landing", "Crownlands", "28005", Role.MANAGER, false);
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(updatedUser -> {
                    assertNotNull(updatedUser);
                    assertEquals("2", updatedUser.getId());
                    assertEquals("Aegon", updatedUser.getFirstName());
                    assertEquals(Role.MANAGER, updatedUser.getRole());
                    assertFalse(updatedUser.getActive());
                });

        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(databaseUser -> assertEquals("Aegon", databaseUser.getFirstName()));
    }

    @Test
    void testUpdateNotFound() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, Role.MANAGER, true);
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID, "999")
                .bodyValue(user)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateBadRequest() {
        User user = new User(null, "Aegon", "Targaryen", null, null,
                null, null, null, null, null, true);
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID, "1")
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateActiveList() {
        this.webTestClient.patch()
                .uri(UserResource.USER)
                .bodyValue(List.of(new UserActiveDto("2", false), new UserActiveDto("3", true)))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertEquals(2, users.size()));

        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> assertFalse(user.getActive()));
    }

    @Test
    void testUpdateActiveListNotFound() {
        this.webTestClient.patch()
                .uri(UserResource.USER)
                .bodyValue(List.of(new UserActiveDto("2", false), new UserActiveDto("999", false)))
                .exchange()
                .expectStatus().isNotFound();

        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> assertTrue(user.getActive()));
    }

    @Test
    void testUpdateActiveListBadRequest() {
        this.webTestClient.patch()
                .uri(UserResource.USER)
                .bodyValue(List.of(new UserActiveDto("1", null)))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateAdminCanNotBeDeactivated() {
        User user = new User("1", "Daemon", "Targaryen", "daemon@got.com", "12345678A",
                "Dragonstone", "Dragonstone", "Crownlands", "28001", Role.ADMIN, false);
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID, "1")
                .bodyValue(user)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testUpdateCanNotSaveAnInactiveAdmin() {
        User user = new User("2", "Rhaenyra", "Targaryen", null, null,
                null, null, null, null, Role.ADMIN, false);
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .bodyValue(user)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testUpdateActiveAdminCanNotBeDeactivated() {
        this.webTestClient.put()
                .uri(UserResource.USER + UserResource.ID_ID + "/active", "1")
                .bodyValue(false)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testUpdateActiveListAdminCanNotBeDeactivated() {
        this.webTestClient.patch()
                .uri(UserResource.USER)
                .bodyValue(List.of(new UserActiveDto("2", false), new UserActiveDto("1", false)))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        this.webTestClient.get()
                .uri(UserResource.USER + UserResource.ID_ID, "2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> assertTrue(user.getActive()));
    }
}
