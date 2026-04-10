package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.Main;
import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button btnSignUp;
    @FXML private Label titleLabel;

    @FXML
    public void initialize() {
        String roleAttempt = UserSession.getLoginAttemptRole();
        titleLabel.setText(roleAttempt + " Portal");

        // Hide Sign Up button if they clicked Admin on the welcome screen
        if (roleAttempt != null && roleAttempt.equals("Admin")) {
            btnSignUp.setVisible(false);
            btnSignUp.setManaged(false);
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String expectedRole = UserSession.getLoginAttemptRole();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info", "Please enter all fields!");
            return;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, expectedRole); // Ensures they log into the correct portal
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // 1. Set standard session data
                UserSession.setCurrentUsername(username);
                UserSession.setCurrentRole(expectedRole);

                // 2. Grab ID card and Profile details
                UserSession.setFullName(rs.getString("full_name"));
                UserSession.setDob(rs.getString("dob"));
                UserSession.setStudentId(rs.getString("student_id"));

                // ADDED: Fixes the blank image issue
                UserSession.setPhotoPath(rs.getString("photo_path"));

                // ADDED: Fixes the password bug on the Profile page
                UserSession.setPassword(rs.getString("password"));

                // 3. TRACK ACTIVITY: Log the login date for the Activity Graph
                logUserActivity(username, connection);

                // 4. Proceed to dashboard
                loadDashboard(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or wrong portal.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database.");
        }
    }

    /**
     * Helper method to record the login for the Active Days graph.
     */
    private void logUserActivity(String username, Connection connection) {
        String logSql = "INSERT INTO login_logs (username) VALUES (?)";
        try (PreparedStatement pstLog = connection.prepareStatement(logSql)) {
            pstLog.setString(1, username);
            pstLog.executeUpdate();
        } catch (Exception e) {
            System.err.println("Could not log activity for " + username + ": " + e.getMessage());
        }
    }

    @FXML
    void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/signup.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBackToWelcome(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/welcome.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/dashboard.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(fxmlLoader.load());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}