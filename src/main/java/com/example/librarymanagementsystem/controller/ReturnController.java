package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnController {

    @FXML
    private TableView<IssueRecord> issueTable;
    @FXML
    private TableColumn<IssueRecord, Integer> colIssueId;
    @FXML
    private TableColumn<IssueRecord, String> colBookTitle;
    @FXML
    private TableColumn<IssueRecord, String> colMemberName;
    @FXML
    private TableColumn<IssueRecord, String> colDueDate;
    @FXML
    private TableColumn<IssueRecord, Double> colFine;

    private StackPane mainContent;
    private ObservableList<IssueRecord> issueList = FXCollections.observableArrayList();

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    @FXML
    private void initialize() {
        colIssueId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        loadIssuedBooks();
    }


    private void loadIssuedBooks() {
        issueList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT issues.id as issue_id, books.title as book_title, members.name as member_name, issues.return_date " +
                             "FROM issues " +
                             "JOIN books ON issues.book_id = books.id " +
                             "JOIN members ON issues.member_id = members.id")) {

            while (rs.next()) {
                String returnDateStr = rs.getString("return_date");
                String displayDate = "No Date Set";
                double fine = 0;

                // FIX: Check if returnDateStr is null before trying to parse it
                if (returnDateStr != null && !returnDateStr.isEmpty()) {
                    try {
                        java.time.LocalDate due = java.time.LocalDate.parse(returnDateStr);
                        displayDate = due.toString();

                        // Calculate fine only if the date exists and is in the past
                        if (due.isBefore(java.time.LocalDate.now())) {
                            fine = java.time.temporal.ChronoUnit.DAYS.between(due, java.time.LocalDate.now()) * 2.0;
                        }
                    } catch (java.time.format.DateTimeParseException e) {
                        System.err.println("Skipping invalid date format: " + returnDateStr);
                    }
                }

                issueList.add(new IssueRecord(
                        rs.getInt("issue_id"),
                        rs.getString("book_title"),
                        rs.getString("member_name"),
                        displayDate,
                        fine
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        issueTable.setItems(issueList);
    }
    @FXML
    private void returnBook() {
        IssueRecord selected = issueTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an issued book.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDelete = conn.prepareStatement("DELETE FROM issues WHERE id=?");
             PreparedStatement psUpdate = conn.prepareStatement(
                     "UPDATE books SET quantity=quantity+1 WHERE title=?")) {

            psDelete.setInt(1, selected.getIssueId());
            psDelete.executeUpdate();

            psUpdate.setString(1, selected.getBookTitle());
            psUpdate.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Book Returned", "Book returned successfully. Fine: " + selected.getFine());
            loadIssuedBooks();

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

    // Inner class for TableView
    public static class IssueRecord {
        private final Integer issueId;
        private final String bookTitle;
        private final String memberName;
        private final String dueDate;
        private final Double fine;

        public IssueRecord(Integer issueId, String bookTitle, String memberName, String dueDate, Double fine) {
            this.issueId = issueId;
            this.bookTitle = bookTitle;
            this.memberName = memberName;
            this.dueDate = dueDate;
            this.fine = fine;
        }

        public Integer getIssueId() { return issueId; }
        public String getBookTitle() { return bookTitle; }
        public String getMemberName() { return memberName; }
        public String getDueDate() { return dueDate; }
        public Double getFine() { return fine; }
    }
}