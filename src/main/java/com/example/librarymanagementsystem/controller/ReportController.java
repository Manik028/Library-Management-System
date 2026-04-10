package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportController {

    @FXML private Label totalBooksIssued;
    @FXML private Label activeMembers;
    @FXML private Label overdueBooks;

    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;

    private StackPane mainContent;

    @FXML
    private void initialize() {
        loadStats();
        loadCharts();
    }

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    @FXML
    private void backToDashboard() {
        if(mainContent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarymanagementsystem/view/dashboard-cards.fxml"));
                Node dashboardNode = loader.load();

                Object controller = loader.getController();
                if (controller instanceof DashboardController) {
                    ((DashboardController) controller).setMainContent(mainContent);
                }

                mainContent.getChildren().clear();
                mainContent.getChildren().add(dashboardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Total Issues (All time)
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS count FROM issues");
            if (rs1.next()) totalBooksIssued.setText(String.valueOf(rs1.getInt("count")));

            // 2. Total Members
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS count FROM members");
            if (rs2.next()) activeMembers.setText(String.valueOf(rs2.getInt("count")));

            // 3. Overdue Books (Return date is before current date)
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) AS count FROM issues WHERE return_date < DATE('now')");
            if (rs3.next()) overdueBooks.setText(String.valueOf(rs3.getInt("count")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCharts() {
        // --- 1. BAR CHART: ISSUES PER MONTH ---
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Issues");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT strftime('%m', issue_date) AS month, COUNT(*) AS count FROM issues GROUP BY month")) {

            while (rs.next()) {
                String monthNum = rs.getString("month");
                String monthName = getMonthName(monthNum);
                int count = rs.getInt("count");
                series.getData().add(new XYChart.Data<>(monthName, count));
            }
        } catch (Exception e) { e.printStackTrace(); }

        barChart.getData().add(series);

        // --- 2. PIE CHART: ISSUED VS AVAILABLE ---
        pieChart.getData().clear();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {
            int totalIssued = 0;
            int totalOnShelves = 0;

            // Count how many books are currently issued (count rows in issues)
            try (Statement stmt1 = conn.createStatement();
                 ResultSet rs1 = stmt1.executeQuery("SELECT COUNT(*) FROM issues")) {
                if (rs1.next()) totalIssued = rs1.getInt(1);
            }

            // Sum up the quantity column in books (what's actually available in library)
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery("SELECT SUM(quantity) FROM books")) {
                if (rs2.next()) totalOnShelves = rs2.getInt(1);
            }

            if (totalIssued > 0 || totalOnShelves > 0) {
                pieData.add(new PieChart.Data("Currently Issued", totalIssued));
                pieData.add(new PieChart.Data("Available on Shelves", totalOnShelves));
            } else {
                pieData.add(new PieChart.Data("No Data", 1));
            }

        } catch (Exception e) { e.printStackTrace(); }

        pieChart.setData(pieData);
    }

    /** Helper to turn "03" into "March" */
    private String getMonthName(String monthNum) {
        if (monthNum == null) return "Unknown";
        switch (monthNum) {
            case "01": return "Jan"; case "02": return "Feb"; case "03": return "Mar";
            case "04": return "Apr"; case "05": return "May"; case "06": return "Jun";
            case "07": return "Jul"; case "08": return "Aug"; case "09": return "Sep";
            case "10": return "Oct"; case "11": return "Nov"; case "12": return "Dec";
            default: return monthNum;
        }
    }
}