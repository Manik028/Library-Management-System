package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label; // <-- FIXED: Added missing Label import
import javafx.scene.layout.GridPane;
import java.util.Optional;

import java.io.IOException;
import java.sql.*;

public class BookController {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> colId;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TableColumn<Book, Integer> colQuantity;

    @FXML
    private TextField searchField;

    private StackPane mainContent;
    private ObservableList<Book> booksList = FXCollections.observableArrayList();

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadBooks();
    }

    private void loadBooks() {
        booksList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                booksList.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bookTable.setItems(booksList);
    }

    @FXML
    private void searchBooks() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book b : booksList) {
            if (b.getTitle().toLowerCase().contains(keyword) || b.getAuthor().toLowerCase().contains(keyword)) {
                filtered.add(b);
            }
        }
        bookTable.setItems(filtered);
    }

    @FXML
    private void addBook() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter the details of the new book:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    return new Book(0, titleField.getText().trim(), authorField.getText().trim(), quantity);
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Invalid Input", "Quantity must be a valid number!");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(book -> {
            if (book.getTitle().isEmpty() || book.getAuthor().isEmpty()) {
                showAlert(AlertType.WARNING, "Missing Data", "Title and Author cannot be empty.");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO books(title, author, quantity) VALUES(?,?,?)")) {

                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setInt(3, book.getQuantity());
                ps.executeUpdate();

                showAlert(AlertType.INFORMATION, "Success", "Book added successfully!");
                loadBooks();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Failed to add the book to the database.");
            }
        });
    }

    // FIXED: Upgraded the editBook method to use the popup UI instead of hardcoded demo values
    @FXML
    private void editBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a book to edit.");
            return;
        }

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Update the details for: " + selected.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selected.getTitle());
        TextField authorField = new TextField(selected.getAuthor());
        TextField quantityField = new TextField(String.valueOf(selected.getQuantity()));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    return new Book(selected.getId(), titleField.getText().trim(), authorField.getText().trim(), quantity);
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Invalid Input", "Quantity must be a valid number!");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(book -> {
            if (book.getTitle().isEmpty() || book.getAuthor().isEmpty()) {
                showAlert(AlertType.WARNING, "Missing Data", "Title and Author cannot be empty.");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE books SET title=?, author=?, quantity=? WHERE id=?")) {

                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setInt(3, book.getQuantity());
                ps.setInt(4, book.getId());
                ps.executeUpdate();

                showAlert(AlertType.INFORMATION, "Success", "Book updated successfully!");
                loadBooks();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Failed to update the book.");
            }
        });
    }

    @FXML
    private void deleteBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a book to delete.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            showAlert(AlertType.INFORMATION, "Book Deleted", "Book removed successfully.");
            loadBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/dashboard-cards.fxml"));
            Node dashboardCards = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setMainContent(mainContent);
            }

            mainContent.getChildren().clear();
            mainContent.getChildren().add(dashboardCards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}