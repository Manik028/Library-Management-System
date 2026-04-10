package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreateQuestionController {
    private StackPane mainContent;
    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML private Label lblSubject;
    @FXML private TextArea txtQuestion;
    @FXML private TextField txtOptA, txtOptB, txtOptC, txtOptD;
    @FXML private ComboBox<String> comboCorrect;

    @FXML
    public void initialize() {
        lblSubject.setText("Create " + UserSession.getCurrentSubject() + " Question");
    }

    @FXML
    void saveQuestion() {
        if (txtQuestion.getText().isEmpty() || txtOptA.getText().isEmpty() || txtOptB.getText().isEmpty() ||
                txtOptC.getText().isEmpty() || txtOptD.getText().isEmpty() || comboCorrect.getValue() == null) {
            showAlert("Missing Info", "Please fill all fields and select a correct option!");
            return;
        }

        String query = "INSERT INTO questions (subject, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, UserSession.getCurrentSubject());
            pst.setString(2, txtQuestion.getText());
            pst.setString(3, txtOptA.getText());
            pst.setString(4, txtOptB.getText());
            pst.setString(5, txtOptC.getText());
            pst.setString(6, txtOptD.getText());
            pst.setString(7, comboCorrect.getValue());
            pst.executeUpdate();

            showAlert("Success", "Question saved successfully!");

            // Clear form for the next question
            txtQuestion.clear(); txtOptA.clear(); txtOptB.clear(); txtOptC.clear(); txtOptD.clear(); comboCorrect.setValue(null);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save the question.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}