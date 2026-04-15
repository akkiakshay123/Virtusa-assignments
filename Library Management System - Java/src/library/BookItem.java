package library;

public class BookItem {

    private int id;
    private String name;
    private String writer;
    private boolean inStock;

    public BookItem(int id, String name, String writer) {
        this.id = id;
        this.name = name;
        this.writer = writer;
        this.inStock = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWriter() {
        return writer;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateWriter(String writer) {
        this.writer = writer;
    }

    public void setStock(boolean status) {
        this.inStock = status;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + writer + " | " + (inStock ? "Available" : "Issued");
    }
}