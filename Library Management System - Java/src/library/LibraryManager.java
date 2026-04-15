package library;

import java.sql.*;
import java.time.LocalDate;

public class LibraryManager {

    private static final int MAX_DAYS = 14;
    private static final int DAILY_FINE = 5;

    // Utility method for DB connection
    private Connection connectDB() throws Exception {
        return DBConnection.getConnection();
    }

    // ------------------- BOOK OPERATIONS -------------------

    public void insertBook(String bookTitle, String bookAuthor) throws Exception {
        try (Connection con = connectDB()) {
            String query = "INSERT INTO books(title, author, available) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, bookTitle);
            stmt.setString(2, bookAuthor);
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
            System.out.println("✔ Book successfully added.");
        }
    }

    public void displayBooks() throws Exception {
        try (Connection con = connectDB();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                String status = rs.getBoolean("available") ? "In Stock" : "Checked Out";

                System.out.printf("%d | %s | %s | %s\n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        status);
            }
        }
    }

    public void removeBook(int id) throws Exception {
        try (Connection con = connectDB()) {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM books WHERE book_id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ------------------- USER OPERATIONS -------------------

    public void createUser(String username, String userEmail) throws Exception {
        try (Connection con = connectDB()) {
            String sql = "INSERT INTO users(name, email) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, userEmail);
            stmt.executeUpdate();

            System.out.println("✔ User added.");
        }
    }

    public void removeUser(int uid) throws Exception {
        try (Connection con = connectDB()) {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM users WHERE user_id = ?");
            stmt.setInt(1, uid);
            stmt.executeUpdate();
        }
    }

    public ResultSet fetchUsers() throws Exception {
        Connection con = connectDB();
        Statement stmt = con.createStatement();
        return stmt.executeQuery("SELECT * FROM users");
    }

    // ------------------- ISSUE / RETURN -------------------

    public void lendBook(int bookId, int userId) throws Exception {
        try (Connection con = connectDB()) {

            // Check availability
            PreparedStatement checkStmt = con.prepareStatement(
                    "SELECT available FROM books WHERE book_id = ?"
            );
            checkStmt.setInt(1, bookId);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next() || !rs.getBoolean("available")) {
                System.out.println("⚠ Book currently unavailable.");
                return;
            }

            LocalDate issue = LocalDate.now();
            LocalDate deadline = issue.plusDays(MAX_DAYS);

            PreparedStatement issueStmt = con.prepareStatement(
                    "INSERT INTO records(book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?)"
            );

            issueStmt.setInt(1, bookId);
            issueStmt.setInt(2, userId);
            issueStmt.setDate(3, Date.valueOf(issue));
            issueStmt.setDate(4, Date.valueOf(deadline));

            issueStmt.executeUpdate();

            PreparedStatement updateBook = con.prepareStatement(
                    "UPDATE books SET available = false WHERE book_id = ?"
            );
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            System.out.println("✔ Book issued.");
        }
    }

    public void submitReturn(int recordId) throws Exception {
        try (Connection con = connectDB()) {

            PreparedStatement fetchRecord = con.prepareStatement(
                    "SELECT book_id, due_date FROM records WHERE record_id = ? AND return_date IS NULL"
            );
            fetchRecord.setInt(1, recordId);

            ResultSet rs = fetchRecord.executeQuery();

            if (!rs.next()) {
                System.out.println("⚠ Record not found.");
                return;
            }

            int bookId = rs.getInt("book_id");
            LocalDate due = rs.getDate("due_date").toLocalDate();
            LocalDate returnedOn = LocalDate.now();

            long penalty = calculateFine(due, returnedOn);

            PreparedStatement updateRec = con.prepareStatement(
                    "UPDATE records SET return_date = ? WHERE record_id = ?"
            );
            updateRec.setDate(1, Date.valueOf(returnedOn));
            updateRec.setInt(2, recordId);
            updateRec.executeUpdate();

            PreparedStatement updateBook = con.prepareStatement(
                    "UPDATE books SET available = true WHERE book_id = ?"
            );
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            System.out.println("✔ Returned successfully. Fine: Rs " + penalty);
        }
    }

    // ------------------- HELPER -------------------

    private long calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            return (returnDate.toEpochDay() - dueDate.toEpochDay()) * DAILY_FINE;
        }
        return 0;
    }

    // ------------------- RECORDS -------------------

    public ResultSet fetchRecords() throws Exception {
        Connection con = connectDB();
        Statement stmt = con.createStatement();

        String sql = """
                SELECT r.record_id, b.title, u.name,
                       r.issue_date, r.due_date, r.return_date
                FROM records r
                INNER JOIN books b ON r.book_id = b.book_id
                INNER JOIN users u ON r.user_id = u.user_id
                """;

        return stmt.executeQuery(sql);
    }

    public String exportBooks() throws Exception {
        StringBuilder output = new StringBuilder();

        try (Connection con = connectDB();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                output.append(String.format("%d | %s | %s | %s",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available") ? "Available" : "Unavailable"
                )).append("\n");
            }
        }

        return output.toString();
    }
}