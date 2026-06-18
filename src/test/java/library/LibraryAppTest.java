package library;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Library Application.
 *
 * DatabaseManager tests require a running MySQL instance with the `library`
 * schema loaded from sql/library.sql.  All other tests are pure-Java and
 * run without any external dependencies.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryAppTest {

    // -----------------------------------------------------------------------
    //  Book model
    // -----------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("Book: constructor sets all fields correctly")
    void testBookConstructor() {
        Book book = new Book("Clean Code", "Publisher A", 5, 49.99);
        assertEquals("Clean Code",  book.getName());
        assertEquals("Publisher A", book.getOwner());
        assertEquals(5,             book.getQuantity());
        assertEquals(49.99,         book.getPrice(), 0.001);
    }

    @Test
    @Order(2)
    @DisplayName("Book: convenience constructor sets price to 0.0")
    void testBookConvenienceConstructor() {
        Book book = new Book("Atomic Habits", "Publisher B", 3);
        assertEquals(0.0, book.getPrice(), 0.001);
    }

    @Test
    @Order(3)
    @DisplayName("Book: setters update fields correctly")
    void testBookSetters() {
        Book book = new Book("Test Book", "Owner", 1, 10.0);
        book.setName("Updated Title");
        book.setOwner("New Owner");
        book.setQuantity(10);
        book.setPrice(25.50);

        assertEquals("Updated Title", book.getName());
        assertEquals("New Owner",     book.getOwner());
        assertEquals(10,              book.getQuantity());
        assertEquals(25.50,           book.getPrice(), 0.001);
    }

    @Test
    @Order(4)
    @DisplayName("Book: toString contains key fields")
    void testBookToString() {
        Book book = new Book("Rich Dad Poor Dad", "Publisher", 7, 60.0);
        String str = book.toString();
        assertTrue(str.contains("Rich Dad Poor Dad"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("60.00"));
    }

    // -----------------------------------------------------------------------
    //  DatabaseManager — connection (requires running MySQL)
    // -----------------------------------------------------------------------

    /**
     * This test is intentionally marked as @Disabled when no DB is available.
     * Remove the @Disabled annotation and run `mvn test` with MySQL running.
     */
    @Test
    @Order(5)
    @DisplayName("DatabaseManager: returns false for invalid credentials")
    @Disabled("Requires running MySQL instance — remove @Disabled to run")
    void testInvalidCredentials() {
        DatabaseManager db = new DatabaseManager();
        assertFalse(db.checkCredentials("nonexistent_user", "wrong_password"));
    }

    @Test
    @Order(6)
    @DisplayName("DatabaseManager: fetchAllBooks returns list (requires DB)")
    @Disabled("Requires running MySQL instance — remove @Disabled to run")
    void testFetchAllBooks() {
        DatabaseManager db = new DatabaseManager();
        assertNotNull(db.fetchAllBooks());
    }
}
