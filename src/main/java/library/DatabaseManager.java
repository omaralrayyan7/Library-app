package library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATABASE MANAGER — handles all MySQL interactions for the Library Application.
 *
 * Connection: jdbc:mysql://localhost:3306/library
 * Username  : root
 * Password  : (empty — change for your environment)
 *
 * Tables used:
 *   user  (username VARCHAR, password INT)
 *   book  (bookname VARCHAR, price DOUBLE, quantity INT, owner VARCHAR)
 */
public class DatabaseManager {

    // -----------------------------------------------------------------------
    //  Connection settings — update if your MySQL credentials differ
    // -----------------------------------------------------------------------
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // -----------------------------------------------------------------------
    //  Connection factory
    // -----------------------------------------------------------------------

    /**
     * Opens and returns a new MySQL connection.
     * Callers should use try-with-resources to auto-close.
     */
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // -----------------------------------------------------------------------
    //  Authentication
    // -----------------------------------------------------------------------

    /**
     * Returns true if the given username and password match a row in the
     * {@code user} table.  Password is stored as an integer in the original
     * schema; we cast the string input for compatibility.
     */
    public boolean checkCredentials(String username, String password) {
        String sql = "SELECT 1 FROM user WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[DB] checkCredentials error: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------------------
    //  Book queries
    // -----------------------------------------------------------------------

    /**
     * Returns the first book whose name exactly matches {@code bookname},
     * or {@code null} if not found.
     */
    public Book findBook(String bookname) {
        String sql = "SELECT * FROM book WHERE bookname = ? LIMIT 1";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookname);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getString("bookname"),
                        rs.getString("owner"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("[DB] findBook error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all books in the catalogue.
     */
    public List<Book> fetchAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book";
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getString("bookname"),
                        rs.getString("owner"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) {
            System.err.println("[DB] fetchAllBooks error: " + e.getMessage());
        }
        return books;
    }

    // -----------------------------------------------------------------------
    //  Book transactions
    // -----------------------------------------------------------------------

    /**
     * Decrements quantity by 1.  Returns true on success, false if the book
     * is not found or already has quantity = 0.
     */
    public boolean decrementQuantity(String bookname) {
        String sql = "UPDATE book SET quantity = quantity - 1 WHERE bookname = ? AND quantity > 0";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookname);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] decrementQuantity error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Increments quantity by 1 (book returned).  Returns true on success.
     */
    public boolean incrementQuantity(String bookname) {
        String sql = "UPDATE book SET quantity = quantity + 1 WHERE bookname = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookname);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] incrementQuantity error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new book row (sell / add to catalogue).
     * Returns true on success.
     */
    public boolean addBook(String bookname, String owner, int quantity, double price) {
        String sql = "INSERT INTO book (bookname, owner, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookname);
            stmt.setString(2, owner);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] addBook error: " + e.getMessage());
            return false;
        }
    }
}
