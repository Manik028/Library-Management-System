package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.Main;
import com.example.librarymanagementsystem.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignupController {

    @FXML private TextField txtUsername, txtFullName, txtStudentId;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpDOB;
    @FXML private Label lblPhotoPath;

    private String selectedPhotoURI = "";

    @FXML
    void choosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            selectedPhotoURI = file.toURI().toString();
            lblPhotoPath.setText(file.getName());
        }
    }

    @FXML
    void registerUser(ActionEvent event) {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty() || txtFullName.getText().isEmpty() ||
                dpDOB.getValue() == null || txtStudentId.getText().isEmpty() || selectedPhotoURI.isEmpty()) {
            showAlert("Error", "Please fill all fields and select a photo!");
            return;
        }

        String query = "INSERT INTO users (username, password, role, full_name, dob, student_id, photo_path) VALUES (?, ?, 'Librarian', ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, txtUsername.getText());
            pst.setString(2, txtPassword.getText());
            pst.setString(3, txtFullName.getText());
            pst.setString(4, dpDOB.getValue().toString());
            pst.setString(5, txtStudentId.getText());
            pst.setString(6, selectedPhotoURI);
            pst.executeUpdate();

            showAlert("Success", "Library Card Generated! You can now log in.");
            goBack(event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Username taken or database error.");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/login.fxml"));

            // THE FIX: Grab the current scene and swap the root to keep it Maximized!
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}