package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.Member;
import com.example.librarymanagementsystem.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class IssueController {

    @FXML private ComboBox<Book> bookCombo;
    @FXML private ComboBox<Member> memberCombo;
    @FXML private DatePicker issueDate;
    @FXML private DatePicker returnDate;

    private StackPane mainContent;

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    @FXML
    private void initialize() {
        loadBooks();
        loadMembers();
        issueDate.setValue(LocalDate.now());
        returnDate.setValue(LocalDate.now().plusDays(14));

        // Format Book ComboBox to show Title
        bookCombo.setConverter(new StringConverter<Book>() {
            @Override public String toString(Book book) {
                return book == null ? "" : book.getTitle();
            }
            @Override public Book fromString(String string) { return null; }
        });

        // Format Member ComboBox to show Name
        memberCombo.setConverter(new StringConverter<Member>() {
            @Override public String toString(Member member) {
                return member == null ? "" : member.getName();
            }
            @Override public Member fromString(String string) { return null; }
        });
    }

    private void loadBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books WHERE quantity>0")) {

            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bookCombo.setItems(books);
    }

    private void loadMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {

            while (rs.next()) {
                members.add(new Member(rs.getInt("id"), rs.getString("name"), rs.getString("contact")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        memberCombo.setItems(members);
    }

    @FXML
    private void issueBook() {
        Book selectedBook = bookCombo.getSelectionModel().getSelectedItem();
        Member selectedMember = memberCombo.getSelectionModel().getSelectedItem();
        LocalDate issue = issueDate.getValue();
        LocalDate ret = returnDate.getValue();

        if (selectedBook == null || selectedMember == null || issue == null || ret == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please select book, member, and dates.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO issues(book_id, member_id, issue_date, return_date) VALUES(?,?,?,?)");
             PreparedStatement psUpdate = conn.prepareStatement("UPDATE books SET quantity=quantity-1 WHERE id=?")) {

            ps.setInt(1, selectedBook.getId());
            ps.setInt(2, selectedMember.getId());
            ps.setString(3, issue.toString());
            ps.setString(4, ret.toString());
            ps.executeUpdate();

            psUpdate.setInt(1, selectedBook.getId());
            psUpdate.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Book Issued", "Book issued successfully.");
            loadBooks(); // refresh combo box

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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}