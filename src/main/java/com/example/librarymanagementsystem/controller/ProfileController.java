package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
public class ProfileController {
    @FXML private TextField txtFullName, txtStudentId;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpDOB;
    @FXML private ImageView imgProfilePreview;
    private String updatedPhotoPath;
    private StackPane mainContent;

    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML
    public void initialize() {
        txtFullName.setText(UserSession.getFullName());
        txtStudentId.setText(UserSession.getStudentId());

        if (UserSession.getDob() != null && !UserSession.getDob().isEmpty()) {
            dpDOB.setValue(LocalDate.parse(UserSession.getDob()));
        }

        // FIX FOR CIRCULAR IMAGE
        Circle clip = new Circle(60, 60, 60); // Centers the clip (X=60, Y=60) with Radius 60
        imgProfilePreview.setClip(clip);

        updatedPhotoPath = UserSession.getPhotoPath();
        if (updatedPhotoPath != null && !updatedPhotoPath.isEmpty()) {
            try {
                imgProfilePreview.setImage(new Image(updatedPhotoPath));
            } catch (Exception e) {
                System.out.println("Could not load image preview.");
            }
        }
    }

    @FXML
    private void chooseNewPhoto() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(imgProfilePreview.getScene().getWindow());
        if (file != null) {
            updatedPhotoPath = file.toURI().toString();
            imgProfilePreview.setImage(new Image(updatedPhotoPath));
        }
    }

    @FXML
    private void handleSave() {
        boolean isPasswordEmpty = txtPassword.getText().isEmpty();

        // Smart query: Only updates the password if the user actually typed a new one
        String query = isPasswordEmpty ?
                "UPDATE users SET full_name=?, student_id=?, dob=?, photo_path=? WHERE username=?" :
                "UPDATE users SET full_name=?, student_id=?, dob=?, photo_path=?, password=? WHERE username=?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtStudentId.getText());
            pst.setString(3, dpDOB.getValue() != null ? dpDOB.getValue().toString() : null);
            pst.setString(4, updatedPhotoPath);

            if (isPasswordEmpty) {
                pst.setString(5, UserSession.getCurrentUsername());
            } else {
                pst.setString(5, txtPassword.getText());
                pst.setString(6, UserSession.getCurrentUsername());
            }

            if (pst.executeUpdate() > 0) {
                UserSession.setFullName(txtFullName.getText());
                UserSession.setStudentId(txtStudentId.getText());
                UserSession.setDob(dpDOB.getValue() != null ? dpDOB.getValue().toString() : null);
                UserSession.setPhotoPath(updatedPhotoPath);
                new Alert(Alert.AlertType.INFORMATION, "Profile Updated!").showAndWait();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void backToDashboard(ActionEvent event) {
        try {
            // Safely find the mainContent StackPane from the current scene window
            StackPane targetPane = (StackPane) ((Node) event.getSource()).getScene().lookup("#mainContent");

            if (targetPane != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/dashboard-cards.fxml"));
                targetPane.getChildren().setAll((Node) loader.load());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}