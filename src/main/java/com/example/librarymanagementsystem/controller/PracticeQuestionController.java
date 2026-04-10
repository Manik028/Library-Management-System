package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PracticeQuestionController {
    private StackPane mainContent;
    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML private Label lblSubject, lblQuestion;
    @FXML private Button btnA, btnB, btnC, btnD, btnNext;

    private List<String[]> questionsList = new ArrayList<>();
    private int currentIndex = 0;
    private String correctOption = "";

    // NEW: Variable to track the score!
    private int score = 0;

    @FXML
    public void initialize() {
        lblSubject.setText(UserSession.getCurrentSubject() + " Practice");
        loadQuestionsFromDB();

        if (!questionsList.isEmpty()) {
            displayQuestion();
        } else {
            lblQuestion.setText("No questions available for this subject yet! Ask an Admin to create some.");
            btnA.setVisible(false); btnB.setVisible(false); btnC.setVisible(false); btnD.setVisible(false);
        }
    }

    private void loadQuestionsFromDB() {
        String query = "SELECT * FROM questions WHERE subject = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, UserSession.getCurrentSubject());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                questionsList.add(new String[]{
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void displayQuestion() {
        String[] qData = questionsList.get(currentIndex);
        lblQuestion.setText(qData[0]); // The question text is now visible!

        btnA.setText("A)  " + qData[1]);
        btnB.setText("B)  " + qData[2]);
        btnC.setText("C)  " + qData[3]);
        btnD.setText("D)  " + qData[4]);
        correctOption = qData[5];

        // Reset Styles and Re-enable buttons
        Button[] btns = {btnA, btnB, btnC, btnD};
        for (Button b : btns) {
            b.setDisable(false);
            b.setStyle("-fx-font-size: 16px; -fx-padding: 15; -fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-text-fill: black;");
        }
        btnNext.setVisible(false);
    }

    @FXML
    void checkAnswer(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        String chosenId = clicked.getId(); // e.g., "btnA"
        String chosenLetter = chosenId.replace("btn", ""); // Gets "A", "B", "C", or "D"

        String correctStyle = "-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 10;";
        String wrongStyle = "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 10;";

        if (chosenLetter.equals(correctOption)) {
            clicked.setStyle(correctStyle);
            score++; // NEW: Increase score if they get it right!
        } else {
            clicked.setStyle(wrongStyle);
            // Highlight the correct one so they learn
            if(correctOption.equals("A")) btnA.setStyle(correctStyle);
            if(correctOption.equals("B")) btnB.setStyle(correctStyle);
            if(correctOption.equals("C")) btnC.setStyle(correctStyle);
            if(correctOption.equals("D")) btnD.setStyle(correctStyle);
        }

        // Lock all buttons so they can't change their answer
        btnA.setDisable(true); btnB.setDisable(true); btnC.setDisable(true); btnD.setDisable(true);
        btnNext.setVisible(true);
    }

    @FXML
    void loadNextQuestion() {
        currentIndex++;
        if (currentIndex < questionsList.size()) {
            displayQuestion();
        } else {
            // NEW: Show the final score when the quiz ends!
            lblQuestion.setText("Quiz Complete!\n\nYour Score: " + score + " out of " + questionsList.size());

            // Hide the options and the next button
            btnA.setVisible(false); btnB.setVisible(false); btnC.setVisible(false); btnD.setVisible(false); btnNext.setVisible(false);
        }
    }
}