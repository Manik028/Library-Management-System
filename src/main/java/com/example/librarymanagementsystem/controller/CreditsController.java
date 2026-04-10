package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import java.io.IOException;

public class CreditsController {

    @FXML private ImageView imgSajeb;
    @FXML private ImageView imgManik;

    @FXML
    public void initialize() {
        // Cut the images into perfect circles (120x120 images = 60 radius)
        Circle clipSajeb = new Circle(60, 60, 60);
        imgSajeb.setClip(clipSajeb);

        Circle clipManik = new Circle(60, 60, 60);
        imgManik.setClip(clipManik);

        // LOAD IMAGES: Make sure to rename your photos and put them in your project folder!
        try {
            // Update these paths if your images are saved somewhere else
            imgSajeb.setImage(new Image(getClass().getResourceAsStream("/com/example/librarymanagementsystem/images/sajeb.jpg")));
            imgManik.setImage(new Image(getClass().getResourceAsStream("/com/example/librarymanagementsystem/images/manik.jpg")));
        } catch (Exception e) {
            System.out.println("Could not load team photos. Check file paths.");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/welcome.fxml"));
            // Keep it maximized!
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}