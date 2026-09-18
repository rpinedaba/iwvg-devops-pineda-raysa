package es.upm.miw.devops.functionaltests;

import es.upm.miw.devops.models.Role;
import es.upm.miw.devops.models.User;
import es.upm.miw.devops.rest.UserResource;
import es.upm.miw.devops.services.DatabaseSeederService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

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
                .uri(UserResource.USERS + UserResource.ID_ID, "1")
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
                .uri(UserResource.USERS + UserResource.ID_ID, "5")
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
                .uri(UserResource.USERS + UserResource.ID_ID, "999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        this.webTestClient.delete()
                .uri(UserResource.USERS + UserResource.ID_ID, "1")
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
                .uri(UserResource.USERS + UserResource.ID_ID, "999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActive() {
        this.webTestClient.put()
                .uri(UserResource.USERS + UserResource.ID_ID + "/active", "1")
                .bodyValue(java.util.Map.of("active", false))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertNotNull(user);
                    assertEquals("1", user.getId());
                    assertFalse(user.getActive());
                });

        this.webTestClient.get()
                .uri(UserResource.USERS + UserResource.ID_ID, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> assertFalse(user.getActive()));
    }

    @Test
    void testUpdateActiveNotFound() {
        this.webTestClient.put()
                .uri(UserResource.USERS + UserResource.ID_ID + "/active", "999")
                .bodyValue(java.util.Map.of("active", false))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveNullValue() {
        this.webTestClient.put()
                .uri(UserResource.USERS + UserResource.ID_ID + "/active", "1")
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
}
