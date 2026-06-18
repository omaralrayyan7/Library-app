package library;

/**
 * MODEL — Book
 *
 * JavaFX-compatible model class used by the TableView in the All Books scene.
 * Properties follow JavaBean conventions (getName / setName) so that
 * PropertyValueFactory can bind columns automatically.
 */
public class Book {

    private String name;
    private String owner;
    private int    quantity;
    private double price;

    // -----------------------------------------------------------------------
    //  Constructors
    // -----------------------------------------------------------------------

    public Book(String name, String owner, int quantity, double price) {
        this.name     = name;
        this.owner    = owner;
        this.quantity = quantity;
        this.price    = price;
    }

    /** Convenience constructor used by the All-Books TableView (no price column). */
    public Book(String name, String owner, int quantity) {
        this(name, owner, quantity, 0.0);
    }

    // -----------------------------------------------------------------------
    //  Getters & Setters
    // -----------------------------------------------------------------------

    public String getName()           { return name; }
    public void   setName(String n)   { this.name = n; }

    public String getOwner()          { return owner; }
    public void   setOwner(String o)  { this.owner = o; }

    public int  getQuantity()         { return quantity; }
    public void setQuantity(int q)    { this.quantity = q; }

    public double getPrice()          { return price; }
    public void   setPrice(double p)  { this.price = p; }

    @Override
    public String toString() {
        return String.format("Book{name='%s', owner='%s', qty=%d, price=%.2f}",
                name, owner, quantity, price);
    }
}
