package es.upm.miw.devops.services;

import es.upm.miw.devops.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseSeederServiceTest {

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void resetDatabase() {
        this.databaseSeederService.reSeedDatabase();
    }

    @Test
    void testSeedDatabaseDoesNotDuplicateWhenAlreadySeeded() {
        long initialCount = this.userRepository.count();

        this.databaseSeederService.seedDatabase();

        assertEquals(initialCount, this.userRepository.count());
    }

    @Test
    void testReSeedDatabase() {
        this.databaseSeederService.reSeedDatabase();

        assertEquals(5L, this.userRepository.count());
    }
}
