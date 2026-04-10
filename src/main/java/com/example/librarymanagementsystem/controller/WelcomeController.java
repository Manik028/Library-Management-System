package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.Main;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.io.IOException;

public class WelcomeController {

    @FXML
    void goToLibrarianLogin(ActionEvent event) {
        UserSession.setLoginAttemptRole("Librarian");
        loadLoginPage(event);
    }

    @FXML
    void goToAdminLogin(ActionEvent event) {
        UserSession.setLoginAttemptRole("Admin");
        loadLoginPage(event);
    }
    @FXML
    void showCreditsPage(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/credits.fxml"));
            // Keep it maximized!
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoginPage(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/login.fxml"));

            // THE FIX: Grabs the current scene and swaps the root content to keep it Maximized!
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(fxmlLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}