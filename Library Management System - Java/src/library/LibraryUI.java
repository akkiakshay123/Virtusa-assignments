package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LibraryUI extends JFrame {

    private LibraryManager manager;
    private JTable bookTable;
    private DefaultTableModel bookModel;

    public LibraryUI() {

        manager = new LibraryManager();

        setTitle("Smart Library Dashboard");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabPane = new JTabbedPane();

        tabPane.add("Books", buildBookPanel());
        tabPane.add("Users", buildUserPanel());
        tabPane.add("Records", buildRecordPanel());

        add(tabPane);
        setVisible(true);
    }

    // ================= BOOK PANEL =================
    private JPanel buildBookPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        bookModel = new DefaultTableModel(
                new String[]{"Book ID", "Title", "Author", "Status"}, 0
        );

        bookTable = new JTable(bookModel);

        JTextField titleField = new JTextField(10);
        JTextField authorField = new JTextField(10);

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Remove");
        JButton reloadBtn = new JButton("Reload");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Title"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author"));
        inputPanel.add(authorField);
        inputPanel.add(addBtn);

        JPanel actionPanel = new JPanel();
        actionPanel.add(deleteBtn);
        actionPanel.add(reloadBtn);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // 🔘 Actions

        addBtn.addActionListener(e -> {
            try {
                manager.insertBook(titleField.getText(), authorField.getText());
                refreshBooks();
            } catch (Exception ex) {
                displayError(ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) bookModel.getValueAt(row, 0);
                try {
                    manager.removeBook(id);
                    refreshBooks();
                } catch (Exception ex) {
                    displayError(ex);
                }
            }
        });

        reloadBtn.addActionListener(e -> refreshBooks());

        refreshBooks();
        return panel;
    }

    // ================= USER PANEL =================
    private JPanel buildUserPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel userModel = new DefaultTableModel(
                new String[]{"User ID", "Name", "Email"}, 0
        );

        JTable userTable = new JTable(userModel);

        JTextField nameField = new JTextField(10);
        JTextField emailField = new JTextField(10);

        JButton addUserBtn = new JButton("Add");
        JButton removeUserBtn = new JButton("Remove");
        JButton refreshBtn = new JButton("Reload");

        JPanel top = new JPanel();
        top.add(new JLabel("Name"));
        top.add(nameField);
        top.add(new JLabel("Email"));
        top.add(emailField);
        top.add(addUserBtn);

        JPanel bottom = new JPanel();
        bottom.add(removeUserBtn);
        bottom.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        // 🔘 Actions

        addUserBtn.addActionListener(e -> {
            try {
                manager.createUser(nameField.getText(), emailField.getText());
                loadUsers(userModel);
            } catch (Exception ex) {
                displayError(ex);
            }
        });

        removeUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) userModel.getValueAt(row, 0);
                try {
                    manager.removeUser(id);
                    loadUsers(userModel);
                } catch (Exception ex) {
                    displayError(ex);
                }
            }
        });

        refreshBtn.addActionListener(e -> loadUsers(userModel));

        loadUsers(userModel);
        return panel;
    }

    // ================= RECORD PANEL =================
    private JPanel buildRecordPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel recordModel = new DefaultTableModel(
                new String[]{"Record ID", "Book", "User", "Issue Date", "Due Date", "Return Date"}, 0
        );

        JTable recordTable = new JTable(recordModel);

        JButton refreshBtn = new JButton("Reload");

        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadRecords(recordModel));

        loadRecords(recordModel);
        return panel;
    }

    // ================= DATA LOAD =================

    private void refreshBooks() {
        try {
            bookModel.setRowCount(0);

            ResultSet rs = DBConnection.getConnection()
                    .createStatement()
                    .executeQuery("SELECT * FROM books");

            while (rs.next()) {
                bookModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available") ? "Available" : "Issued"
                });
            }

        } catch (Exception e) {
            displayError(e);
        }
    }

    private void loadUsers(DefaultTableModel model) {
        try {
            model.setRowCount(0);

            ResultSet rs = DBConnection.getConnection()
                    .createStatement()
                    .executeQuery("SELECT * FROM users");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email")
                });
            }

        } catch (Exception e) {
            displayError(e);
        }
    }

    private void loadRecords(DefaultTableModel model) {
        try {
            model.setRowCount(0);

            ResultSet rs = manager.fetchRecords();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("record_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date")
                });
            }

        } catch (Exception e) {
            displayError(e);
        }
    }

    // ================= ERROR =================

    private void displayError(Exception e) {
        JOptionPane.showMessageDialog(this,
                "Something went wrong:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}