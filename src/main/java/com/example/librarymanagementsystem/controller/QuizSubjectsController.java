package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class QuizSubjectsController {
    private StackPane mainContent;

    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML
    void handleSubjectClick(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        UserSession.setCurrentSubject(clicked.getText()); // Save the subject they clicked

        // Route based on role
        String targetFxml = UserSession.getCurrentRole().equals("Admin") ? "create-question.fxml" : "practice-question.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/" + targetFxml));
            Node node = loader.load();

            Object controller = loader.getController();
            if(controller instanceof CreateQuestionController) ((CreateQuestionController) controller).setMainContent(mainContent);
            else if(controller instanceof PracticeQuestionController) ((PracticeQuestionController) controller).setMainContent(mainContent);

            mainContent.getChildren().clear();
            mainContent.getChildren().add(node);
        } catch (Exception e) { e.printStackTrace(); }
    }
}