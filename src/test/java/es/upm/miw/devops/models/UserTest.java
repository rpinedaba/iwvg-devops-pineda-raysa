package es.upm.miw.devops.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User("7", "Arya", "Stark", "arya@got.com", "77665544E",
                "Winterfell", "Winterfell", "North", "28007", Role.MANAGER, true);

        assertAll(
                () -> assertEquals("7", user.getId()),
                () -> assertEquals("Arya", user.getFirstName()),
                () -> assertEquals("Stark", user.getFamilyName()),
                () -> assertEquals("arya@got.com", user.getEmail()),
                () -> assertEquals("77665544E", user.getIdentity()),
                () -> assertEquals("Winterfell", user.getAddress()),
                () -> assertEquals("Winterfell", user.getCity()),
                () -> assertEquals("North", user.getProvince()),
                () -> assertEquals("28007", user.getPostalCode()),
                () -> assertEquals(Role.MANAGER, user.getRole()),
                () -> assertTrue(user.getActive())
        );
    }

    @Test
    void testSetters() {
        User user = new User();

        user.setId("8");
        user.setFirstName("Jon");
        user.setFamilyName("Snow");
        user.setEmail("jon@got.com");
        user.setIdentity("88776655F");
        user.setAddress("Castle Black");
        user.setCity("Wall");
        user.setProvince("North");
        user.setPostalCode("28008");
        user.setRole(Role.ADMIN);
        user.setActive(false);

        assertAll(
                () -> assertEquals("8", user.getId()),
                () -> assertEquals("Jon", user.getFirstName()),
                () -> assertEquals("Snow", user.getFamilyName()),
                () -> assertEquals("jon@got.com", user.getEmail()),
                () -> assertEquals("88776655F", user.getIdentity()),
                () -> assertEquals("Castle Black", user.getAddress()),
                () -> assertEquals("Wall", user.getCity()),
                () -> assertEquals("North", user.getProvince()),
                () -> assertEquals("28008", user.getPostalCode()),
                () -> assertEquals(Role.ADMIN, user.getRole()),
                () -> assertFalse(user.getActive())
        );
    }

    @Test
    void testDefaultConstructorAndEquals() {
        User user1 = new User();
        User user2 = new User();
        user1.setId("9");
        user2.setId("9");

        assertAll(
                () -> assertEquals(user1, user2),
                () -> assertEquals(user1.hashCode(), user2.hashCode()),
                () -> assertEquals(user1, user1),
                () -> assertNotEquals(user1, null),
                () -> assertNotEquals(user1, new Object())
        );
    }

    @Test
    void testDifferentUsersAreNotEqual() {
        User user1 = new User("9", "Bran", "Stark", "bran@got.com", "99887766G",
                "Winterfell", "Winterfell", "North", "28009", Role.CUSTOMER, true);
        User user3 = new User("10", "Ned", "Stark", "ned@got.com", "11223344H",
                "Winterfell", "Winterfell", "North", "28010", Role.MANAGER, false);

        assertNotEquals(user1, user3);
    }

    @Test
    void testToString() {
        User user = new User("11", "Sansa", "Stark", "sansa@got.com", "55667788I",
                "Winterfell", "Winterfell", "North", "28011", Role.OPERATOR, true);

        String text = user.toString();

        assertAll(
                () -> assertTrue(text.contains("id='11'")),
                () -> assertTrue(text.contains("firstName='Sansa'")),
                () -> assertTrue(text.contains("familyName='Stark'")),
                () -> assertTrue(text.contains("role=OPERATOR")),
                () -> assertTrue(text.contains("active=true"))
        );
    }
}
