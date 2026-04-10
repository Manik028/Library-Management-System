package com.example.librarymanagementsystem.controller;
import javafx.event.ActionEvent;

import com.example.librarymanagementsystem.util.DBConnection;
import com.example.librarymanagementsystem.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ActivityController {
    @FXML private Label lblReadCount, lblActiveDays, lblMotivation;
    @FXML private AreaChart<String, Number> progressChart;
    private StackPane mainContent;
    public void setMainContent(StackPane mainContent) { this.mainContent = mainContent; }

    @FXML
    public void initialize() {
        int read = getCount("SELECT COUNT(*) FROM issues WHERE member_name=? AND status='Returned'", UserSession.getFullName());
        int days = getCount("SELECT COUNT(DISTINCT login_date) FROM login_logs WHERE username=?", UserSession.getCurrentUsername());

        lblReadCount.setText(String.valueOf(read));
        lblActiveDays.setText(String.valueOf(days));

        // Custom Motivation Logic
        if(days > 15) {
            lblMotivation.setText("Great work bro! You are killing it! 🔥");
        } else if(days > 5) {
            lblMotivation.setText("Keep up the work, bro! 👍");
        } else if(days > 0) {
            lblMotivation.setText("Welcome back bro! Let's get to work.");
        } else {
            lblMotivation.setText("You are absent for a few days! Come back bro. 😢");
        }

        // Build Graph
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Last Month", read / 2));
        series.getData().add(new XYChart.Data<>("This Month", read));
        progressChart.getData().add(series);
    }

    private int getCount(String sql, String param) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, param);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
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