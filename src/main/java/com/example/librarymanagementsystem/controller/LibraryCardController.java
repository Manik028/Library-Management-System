package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.UserSession;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class LibraryCardController {
    private StackPane mainContent;
    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML private Label lblName;
    @FXML private Label lblId;
    @FXML private Label lblDob;
    @FXML private ImageView imgPhoto;
    @FXML private AnchorPane libraryCardNode; // Reference to the card part

    @FXML
    public void initialize() {
        lblName.setText(UserSession.getFullName() != null ? UserSession.getFullName().toUpperCase() : "UNKNOWN");
        lblId.setText(UserSession.getStudentId() != null ? UserSession.getStudentId() : "N/A");
        lblDob.setText(UserSession.getDob() != null ? UserSession.getDob() : "N/A");

        String photoUri = UserSession.getPhotoPath();
        if (photoUri != null && !photoUri.isEmpty()) {
            try {
                Image image = new Image(photoUri);
                imgPhoto.setImage(image);
            } catch (Exception e) {
                System.out.println("Could not load image: " + photoUri);
            }
        }
    }

    @FXML
    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Library Card");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        fileChooser.setInitialFileName(lblName.getText().replace(" ", "_") + "_LibraryCard.png");

        File file = fileChooser.showSaveDialog(libraryCardNode.getScene().getWindow());

        if (file != null) {
            try {
                // Use Transparent background for the snapshot so rounded corners look good
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(javafx.scene.paint.Color.TRANSPARENT);

                WritableImage snapshot = libraryCardNode.snapshot(params, null);

                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                System.out.println("Saved: " + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}