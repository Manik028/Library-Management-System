package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Member;
import com.example.librarymanagementsystem.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class MemberController {

    @FXML private TableView<MemberRecord> memberTable;
    @FXML private TableColumn<MemberRecord, Integer> colId;
    @FXML private TableColumn<MemberRecord, String> colName;
    @FXML private TableColumn<MemberRecord, String> colContact;
    @FXML private TableColumn<MemberRecord, String> colIssuedBook;
    @FXML private TableColumn<MemberRecord, String> colIssueDate;
    @FXML private TableColumn<MemberRecord, String> colReturnDate;

    @FXML private TextField searchField;
    private StackPane mainContent;
    private ObservableList<MemberRecord> memberList = FXCollections.observableArrayList();

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colIssuedBook.setCellValueFactory(new PropertyValueFactory<>("issuedBook"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        loadMembers();
    }

    private void loadMembers() {
        memberList.clear();
        String sql = "SELECT m.id, m.name, m.contact, b.title as issued_book, i.issue_date, i.return_date " +
                "FROM members m " +
                "LEFT JOIN issues i ON m.id = i.member_id " +
                "LEFT JOIN books b ON i.book_id = b.id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                memberList.add(new MemberRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("issued_book") == null ? "None" : rs.getString("issued_book"),
                        rs.getString("issue_date") == null ? "-" : rs.getString("issue_date"),
                        rs.getString("return_date") == null ? "-" : rs.getString("return_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        memberTable.setItems(memberList);
    }

    @FXML
    private void searchMembers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadMembers();
            return;
        }

        ObservableList<MemberRecord> filtered = FXCollections.observableArrayList();
        for (MemberRecord m : memberList) {
            if (m.getName().toLowerCase().contains(keyword) || m.getContact().toLowerCase().contains(keyword)) {
                filtered.add(m);
            }
        }
        memberTable.setItems(filtered);
    }

    @FXML
    private void addMember() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter the new member's details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Full Name");
        TextField contactField = new TextField(); contactField.setPromptText("Email or Phone");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Contact:"), 0, 1); grid.add(contactField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) return new Member(0, nameField.getText().trim(), contactField.getText().trim());
            return null;
        });

        Optional<Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            if (member.getName().isEmpty() || member.getContact().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Data", "Name and Contact cannot be empty.");
                return;
            }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO members(name, contact) VALUES(?,?)")) {
                ps.setString(1, member.getName());
                ps.setString(2, member.getContact());
                ps.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully!");
                loadMembers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void removeMember() {
        MemberRecord selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to remove.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM members WHERE id=?")) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Member Removed", "Member deleted successfully.");
            loadMembers();
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
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Inner class to combine Member data with Issue data ---
    public static class MemberRecord {
        private final Integer id;
        private final String name;
        private final String contact;
        private final String issuedBook;
        private final String issueDate;
        private final String returnDate;

        public MemberRecord(Integer id, String name, String contact, String issuedBook, String issueDate, String returnDate) {
            this.id = id;
            this.name = name;
            this.contact = contact;
            this.issuedBook = issuedBook;
            this.issueDate = issueDate;
            this.returnDate = returnDate;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getContact() { return contact; }
        public String getIssuedBook() { return issuedBook; }
        public String getIssueDate() { return issueDate; }
        public String getReturnDate() { return returnDate; }
    }
}