package com.example.librarymanagementsystem; // Make sure this matches your package

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // 1. This is where your app loads the first screen (like login.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.setMaximized(true);


        // ---> PASTE THE MUSIC CODE RIGHT HERE <---

        // --- BACKGROUND MUSIC SETUP ---
        try {
            java.net.URL audioUrl = getClass().getResource("/com/example/librarymanagementsystem/audio/music.wav");

            if (audioUrl != null) {
                String safeUri = audioUrl.toURI().toString();

                javafx.scene.media.Media backgroundMusic = new javafx.scene.media.Media(safeUri);
                javafx.scene.media.MediaPlayer mediaPlayer = new javafx.scene.media.MediaPlayer(backgroundMusic);

                mediaPlayer.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
                mediaPlayer.setVolume(0.5);
                mediaPlayer.play();
            } else {
                System.err.println("Could not find music.mp3. Check your audio folder!");
            }
        } catch (Exception e) {
            System.err.println("Error playing background music:");
            e.printStackTrace();
        }
        // ------------------------------
        stage.setMaximized(true);

        // 2. Finally, show the window
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}