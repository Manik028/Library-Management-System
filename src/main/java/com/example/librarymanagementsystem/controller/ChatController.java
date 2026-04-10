package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {
    private StackPane mainContent;
    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML private VBox userListContainer, chatBox;
    @FXML private ListView<String> listUsers;
    @FXML private Label lblChatHeader;
    @FXML private TextField txtMessage;
    @FXML private ScrollPane scrollPane;

    private String chatPartner = "admin"; // Default for Librarian
    private String currentUser = UserSession.getCurrentUsername();
    private Timer timer;

    @FXML
    public void initialize() {
        if (UserSession.getCurrentRole().equals("Admin")) {
            loadUserList();
            listUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    chatPartner = newVal;
                    lblChatHeader.setText("Chatting with: " + chatPartner);
                    refreshMessages();
                }
            });
        } else {
            userListContainer.setVisible(false);
            userListContainer.setManaged(false);
            lblChatHeader.setText("Admin Support");
            refreshMessages();
        }

        // Auto-refresh messages every 3 seconds
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() { Platform.runLater(() -> refreshMessages()); }
        }, 0, 3000);
    }

    private void loadUserList() {
        listUsers.getItems().clear();
        String query = "SELECT DISTINCT username FROM users WHERE role = 'Librarian'";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) { listUsers.getItems().add(rs.getString("username")); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void sendMessage() {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty() || (UserSession.getCurrentRole().equals("Admin") && listUsers.getSelectionModel().getSelectedItem() == null)) return;

        String query = "INSERT INTO messages (sender, receiver, message_text) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, currentUser);
            pst.setString(2, chatPartner);
            pst.setString(3, msg);
            pst.executeUpdate();
            txtMessage.clear();
            refreshMessages();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshMessages() {
        if (chatPartner == null) return;
        chatBox.getChildren().clear();
        String query = "SELECT * FROM messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY timestamp ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, currentUser); pst.setString(2, chatPartner);
            pst.setString(3, chatPartner); pst.setString(4, currentUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                boolean isMe = rs.getString("sender").equals(currentUser);
                addMessageToUI(rs.getString("message_text"), isMe);
            }
            scrollPane.setVvalue(1.0); // Scroll to bottom
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addMessageToUI(String message, boolean isMe) {
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(250);
        lbl.setStyle("-fx-padding: 10; -fx-background-radius: 15; -fx-background-color: " + (isMe ? "#00dbde" : "#e1e1e1") + "; -fx-text-fill: black;");

        HBox hb = new HBox(lbl);
        hb.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        chatBox.getChildren().add(hb);
    }
}