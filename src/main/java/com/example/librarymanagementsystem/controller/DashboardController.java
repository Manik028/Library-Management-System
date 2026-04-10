package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.UserSession;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane mainContent;
    @FXML private Button btnProfileCircle, btnActivityCircle;

    // Sidebar Buttons
    @FXML private Button btnIssueBook;
    @FXML private Button btnQuiz;
    @FXML private Button btnChat;
    @FXML private Button btnLibraryCard;
    @FXML private Button btnReports;

    // Center Card Buttons
    @FXML private Button cardIssue;
    @FXML private Button cardQuiz;
    @FXML private Button cardChat;
    @FXML private Button cardLibraryCard;

    private StackPane parentMainContent;

    public void setMainContent(StackPane mainContent) {
        this.parentMainContent = mainContent;
    }

    private StackPane getActiveContentPane() {
        return mainContent != null ? mainContent : parentMainContent;
    }

    @FXML
    public void initialize() {
        if (mainContent != null) {
            showDashboardCards();
        }

        String role = UserSession.getCurrentRole();

        if (role != null && role.equals("Librarian")) {
            // LIBRARIAN ROLE
            if (btnIssueBook != null) { btnIssueBook.setVisible(false); btnIssueBook.setManaged(false); }
            if (cardIssue != null) { cardIssue.setVisible(false); cardIssue.setManaged(false); }

            if (btnReports != null) { btnReports.setVisible(false); btnReports.setManaged(false); }

            if (btnQuiz != null) btnQuiz.setText("Practice Questions");
            if (cardQuiz != null) cardQuiz.setText("PRACTICE QUIZ");

            if (btnChat != null) btnChat.setText("Chat with Admin");
            if (cardChat != null) cardChat.setText("CHAT ADMIN");

        } else if (role != null && role.equals("Admin")) {
            // ADMIN ROLE
            if (btnLibraryCard != null) { btnLibraryCard.setVisible(false); btnLibraryCard.setManaged(false); }
            if (cardLibraryCard != null) { cardLibraryCard.setVisible(false); cardLibraryCard.setManaged(false); }

            if (btnQuiz != null) btnQuiz.setText("Create Questions");
            if (cardQuiz != null) cardQuiz.setText("CREATE QUIZ");

            if (btnChat != null) btnChat.setText("Chat with Users");
            if (cardChat != null) cardChat.setText("CHAT USERS");
        }
        boolean isLibrarian = "Librarian".equals(UserSession.getCurrentRole());
        if(btnProfileCircle != null) btnProfileCircle.setVisible(isLibrarian);
        if(btnActivityCircle != null) btnActivityCircle.setVisible(isLibrarian);
    }
    @FXML private void showProfilePage() { loadFXMLWithController("profile.fxml"); }
    @FXML private void showActivityPage() { loadFXMLWithController("activity.fxml"); }
    @FXML private void showDashboardCards() { loadFXMLWithController("dashboard-cards.fxml"); }
    @FXML private void showBooksPage() { loadFXMLWithController("books.fxml"); }
    @FXML private void showMembersPage() { loadFXMLWithController("members.fxml"); }
    @FXML private void showIssuePage() { loadFXMLWithController("issue.fxml"); }
    @FXML private void showReturnPage() { loadFXMLWithController("return.fxml"); }
    @FXML private void showReportsPage() { loadFXMLWithController("reports.fxml"); }
    @FXML private void showQuizPage() { loadFXMLWithController("quiz-subjects.fxml"); }
    @FXML private void showLibraryCard() { loadFXMLWithController("library-card.fxml"); }
    @FXML private void showChatPage() { loadFXMLWithController("chat.fxml"); }

    @FXML
    private void logout() {
        try {
            UserSession.cleanUserSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/welcome.fxml"));
            Parent root = loader.load();

            StackPane pane = getActiveContentPane();
            if (pane != null) {
                // THE FIX: Swap the root to keep the Welcome screen Maximized!
                Scene scene = pane.getScene();
                scene.setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("Failed to load welcome screen.");
            e.printStackTrace();
        }
    }

    private void loadFXMLWithController(String fxmlFile) {
        StackPane targetPane = getActiveContentPane();
        if (targetPane == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/" + fxmlFile));
            Node newNode = loader.load();

            Object controller = loader.getController();

            if(controller instanceof BookController) ((BookController) controller).setMainContent(targetPane);
            else if(controller instanceof MemberController) ((MemberController) controller).setMainContent(targetPane);
            else if(controller instanceof IssueController) ((IssueController) controller).setMainContent(targetPane);
            else if(controller instanceof ReturnController) ((ReturnController) controller).setMainContent(targetPane);
            else if(controller instanceof ReportController) ((ReportController) controller).setMainContent(targetPane);
            else if(controller instanceof DashboardController) ((DashboardController) controller).setMainContent(targetPane);
            else if(controller instanceof QuizSubjectsController) ((QuizSubjectsController) controller).setMainContent(targetPane);
            else if(controller instanceof CreateQuestionController) ((CreateQuestionController) controller).setMainContent(targetPane);
            else if(controller instanceof PracticeQuestionController) ((PracticeQuestionController) controller).setMainContent(targetPane);
            else if(controller instanceof LibraryCardController) ((LibraryCardController) controller).setMainContent(targetPane);
            else if(controller instanceof ChatController) ((ChatController) controller).setMainContent(targetPane);

            if (!targetPane.getChildren().isEmpty()) {
                Node oldNode = targetPane.getChildren().get(0);
                newNode.setTranslateX(targetPane.getWidth());
                targetPane.getChildren().add(newNode);

                TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.8), newNode);
                slideIn.setToX(0);

                TranslateTransition slideOut = new TranslateTransition(Duration.seconds(0.8), oldNode);
                slideOut.setToX(-targetPane.getWidth());

                slideOut.setOnFinished(event -> targetPane.getChildren().remove(oldNode));

                ParallelTransition parallelTransition = new ParallelTransition(slideIn, slideOut);
                parallelTransition.play();

            } else {
                targetPane.getChildren().add(newNode);
            }

        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}