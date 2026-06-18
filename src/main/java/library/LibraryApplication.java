package library;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * MAIN APPLICATION — LibraryApplication
 *
 * A JavaFX desktop application for managing a small library.
 *
 * Scenes:
 *   Login      — authenticate against the MySQL `user` table
 *   Home       — navigation hub
 *   Dashboard  — search / buy / rent / sell / return books
 *   Profile    — display logged-in user info
 *   Settings   — toggle dark mode
 *   All Books  — TableView of the full catalogue
 *
 * Dependencies: JavaFX 21, MySQL Connector/J 9.x (see pom.xml)
 * Database    : MySQL `library` schema (see sql/library.sql)
 */
public class LibraryApplication extends Application {

    // -----------------------------------------------------------------------
    //  Application state
    // -----------------------------------------------------------------------
    private Stage           primaryStage;
    private String          currentUsername;
    private boolean         darkModeEnabled = false;
    private Scene           currentScene;
    private final DatabaseManager db = new DatabaseManager();

    // -----------------------------------------------------------------------
    //  Entry point
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("📚 Library Management System");
        showLoginScene();
    }

    // -----------------------------------------------------------------------
    //  Theme helper
    // -----------------------------------------------------------------------

    private void applyTheme(Scene scene) {
        if (darkModeEnabled) {
            scene.getRoot().setStyle(
                    "-fx-base: #2b2b2b; -fx-background: #2b2b2b; -fx-text-fill: white;");
        } else {
            scene.getRoot().setStyle("");
        }
    }

    // -----------------------------------------------------------------------
    //  Scenes
    // -----------------------------------------------------------------------

    /** Login screen — validates credentials via DatabaseManager. */
    private void showLoginScene() {
        Label userLabel  = new Label("Username:");
        Label passLabel  = new Label("Password:");
        TextField     usernameField  = new TextField();
        PasswordField passwordField  = new PasswordField();
        Button        loginButton    = new Button("🔓 Login");

        usernameField.setPromptText("Enter username");
        passwordField.setPromptText("Enter password");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");

        loginButton.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Validation", "Please enter both username and password.");
                return;
            }
            if (db.checkCredentials(user, pass)) {
                currentUsername = user;
                showHomeScene();
            } else {
                showAlert("Login Failed", "Incorrect username or password.");
            }
        });

        VBox layout = new VBox(14, userLabel, usernameField, passLabel, passwordField, loginButton);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");

        currentScene = new Scene(layout, 420, 280);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    /** Home / navigation hub. */
    private void showHomeScene() {
        Label welcome = new Label("Welcome, " + currentUsername + "!");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button btnDashboard = new Button("📚 Dashboard");
        Button btnAllBooks  = new Button("📋 All Books");
        Button btnProfile   = new Button("👤 Profile");
        Button btnSettings  = new Button("⚙ Settings");
        Button btnLogout    = new Button("🚪 Logout");

        for (Button b : new Button[]{btnDashboard, btnAllBooks, btnProfile, btnSettings, btnLogout}) {
            b.setMinWidth(180);
            b.setStyle("-fx-font-size: 14px;");
        }

        btnDashboard.setOnAction(e -> showDashboardScene());
        btnAllBooks .setOnAction(e -> showAllBooksScene());
        btnProfile  .setOnAction(e -> showProfileScene());
        btnSettings .setOnAction(e -> showSettingsScene());
        btnLogout   .setOnAction(e -> { currentUsername = null; showLoginScene(); });

        VBox layout = new VBox(14, welcome, btnDashboard, btnAllBooks, btnProfile, btnSettings, btnLogout);
        layout.setPadding(new Insets(35));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #e3f2fd;");

        currentScene = new Scene(layout, 500, 440);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
    }

    /**
     * Dashboard — core library operations:
     * Search, Buy, Rent, Sell, Return.
     */
    private void showDashboardScene() {
        TextField bookNameField = new TextField();
        bookNameField.setPromptText("Enter book name…");

        Button btnSearch = new Button("🔍 Search");
        Button btnBuy    = new Button("🛒 Buy");
        Button btnRent   = new Button("💘 Rent");
        Button btnSell   = new Button("➕ Sell / Add");
        Button btnReturn = new Button("🚚 Return");
        Button btnBack   = backButton();

        TextArea infoArea = new TextArea();
        infoArea.setEditable(false);
        infoArea.setStyle("-fx-font-family: monospace;");
        infoArea.setPrefWidth(320);

        btnSearch.setOnAction(e -> {
            Book b = db.findBook(bookNameField.getText().trim());
            if (b != null) {
                infoArea.setText(String.format(
                        "Name     : %s%nOwner    : %s%nPrice    : $%.2f%nQuantity : %d",
                        b.getName(), b.getOwner(), b.getPrice(), b.getQuantity()));
            } else {
                infoArea.setText("Book not found.");
            }
        });

        btnBuy.setOnAction(e -> {
            String name = bookNameField.getText().trim();
            boolean ok = db.decrementQuantity(name);
            showAlert("Buy Book", ok ? "✅ Book purchased: " + name : "❌ Book not available.");
        });

        btnRent.setOnAction(e -> {
            String name = bookNameField.getText().trim();
            boolean ok = db.decrementQuantity(name);
            showAlert("Rent Book", ok ? "✅ Book rented: " + name : "❌ Book not available.");
        });

        btnSell.setOnAction(e -> showSellDialog());

        btnReturn.setOnAction(e -> {
            String name = bookNameField.getText().trim();
            boolean ok = db.incrementQuantity(name);
            showAlert("Return Book", ok ? "✅ Book returned: " + name : "❌ Book not found.");
        });

        VBox left = new VBox(12, new Label("Book Name:"), bookNameField,
                btnSearch, btnBuy, btnRent, btnSell, btnReturn, btnBack);
        left.setPadding(new Insets(20));

        VBox right = new VBox(10, new Label("📄 Book Info:"), infoArea);
        right.setPadding(new Insets(20));

        HBox layout = new HBox(30, left, right);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #fafafa;");

        currentScene = new Scene(layout, 820, 520);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
    }

    /** Profile screen — shows current user info. */
    private void showProfileScene() {
        Label title = new Label("👤 User Profile");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("📋 Username:"),    0, 0); grid.add(new Label(currentUsername), 1, 0);
        grid.add(new Label("📅 Member Since:"), 0, 1); grid.add(new Label("2024"),            1, 1);
        grid.add(new Label("⭐ Role:"),         0, 2); grid.add(new Label("Regular User"),     1, 2);

        VBox layout = new VBox(22, title, grid, backButton());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(35));

        currentScene = new Scene(layout, 450, 360);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
    }

    /** Settings screen — toggle light / dark mode. */
    private void showSettingsScene() {
        Label title = new Label("⚙ Settings");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        CheckBox darkModeBox = new CheckBox("🌙 Enable Dark Mode");
        darkModeBox.setSelected(darkModeEnabled);

        Button saveBtn = new Button("💾 Save");
        saveBtn.setOnAction(e -> {
            darkModeEnabled = darkModeBox.isSelected();
            applyTheme(currentScene);
            showAlert("Settings", "Settings saved.");
        });

        VBox layout = new VBox(22, title, darkModeBox, saveBtn, backButton());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(35));

        currentScene = new Scene(layout, 420, 320);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
    }

    /** All Books — TableView populated from the database. */
    private void showAllBooksScene() {
        TableView<Book> table = new TableView<>();

        TableColumn<Book, String>  nameCol  = new TableColumn<>("Book Name");
        TableColumn<Book, String>  ownerCol = new TableColumn<>("Owner");
        TableColumn<Book, Integer> qtyCol   = new TableColumn<>("Quantity");
        TableColumn<Book, Double>  priceCol = new TableColumn<>("Price ($)");

        nameCol .setCellValueFactory(new PropertyValueFactory<>("name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        qtyCol  .setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameCol, ownerCol, qtyCol, priceCol);

        List<Book> books = db.fetchAllBooks();
        table.getItems().addAll(books);

        Label header = new Label("📚 All Books (" + books.size() + " total)");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox layout = new VBox(12, header, table, backButton());
        layout.setPadding(new Insets(20));

        currentScene = new Scene(layout, 640, 460);
        applyTheme(currentScene);
        primaryStage.setScene(currentScene);
    }

    // -----------------------------------------------------------------------
    //  Sell dialog
    // -----------------------------------------------------------------------

    /** Opens a dialog to collect book details before inserting into the DB. */
    private void showSellDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sell / Add Book");

        TextField nameField  = new TextField(); nameField.setPromptText("Book name");
        TextField ownerField = new TextField(); ownerField.setPromptText("Owner");
        TextField qtyField   = new TextField(); qtyField.setPromptText("Quantity");
        TextField priceField = new TextField(); priceField.setPromptText("Price");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        grid.add(new Label("Name:"),     0, 0); grid.add(nameField,  1, 0);
        grid.add(new Label("Owner:"),    0, 1); grid.add(ownerField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2); grid.add(qtyField,   1, 2);
        grid.add(new Label("Price:"),    0, 3); grid.add(priceField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    String name  = nameField.getText().trim();
                    String owner = ownerField.getText().trim();
                    int    qty   = Integer.parseInt(qtyField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    boolean ok = db.addBook(name, owner, qty, price);
                    showAlert("Sell Book", ok ? "✅ Book added: " + name : "❌ Failed to add book.");
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Quantity and Price must be numbers.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // -----------------------------------------------------------------------
    //  Helpers
    // -----------------------------------------------------------------------

    private Button backButton() {
        Button btn = new Button("⬅ Back");
        btn.setOnAction(e -> showHomeScene());
        return btn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
