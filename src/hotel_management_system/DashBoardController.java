/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hotel_management_system;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;           // For IOException
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;   // For Alert dialog
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author I_Farazi-Ni
 */
public class DashBoardController implements Initializable {

    @FXML
    private Button close;
    @FXML
    private Button minimize;
    @FXML
    private Button avabtn;
    @FXML
    private Button dbBtn;
    @FXML
    private Button cusBtn;
    @FXML
    private Button logOutBtn;
    @FXML
    private Label bookingLvl;
    @FXML
    private Label incomeLvl;
    @FXML
    private Label t_income;
    @FXML

    private AreaChart<String, Number> incomedata;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDashboardStats();
        loadIncomeChart();
    }

    @FXML

    private void handAvaRoom(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Rooms.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage handAvaRoomStage = (Stage) avabtn.getScene().getWindow();
            handAvaRoomStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load Rooms.fxml: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void mini(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void openDb(ActionEvent event) {
    }

    @FXML
    private void openCus(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage handAvaRoomStage = (Stage) avabtn.getScene().getWindow();
            handAvaRoomStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load Customer.fxml: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadIncomeChart() {
        String query = "SELECT checkin_date, SUM(price) AS daily_income FROM checkin GROUP BY checkin_date ORDER BY checkin_date";

        try (Connection connect = Database.connectDb(); PreparedStatement stmt = connect.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Daily Income");

            while (rs.next()) {
                String date = rs.getString("checkin_date");
                double income = rs.getDouble("daily_income");

                series.getData().add(new XYChart.Data<>(date, income));
            }

            incomedata.getData().clear();
            incomedata.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Chart Error", "Failed to load income chart: " + e.getMessage());
        }
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage handAvaRoomStage = (Stage) avabtn.getScene().getWindow();
            handAvaRoomStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to Log Out" + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadDashboardStats() {
        String today = java.time.LocalDate.now().toString(); // Format: YYYY-MM-DD

        String todayIncomeQuery = "SELECT SUM(price) FROM checkin WHERE checkin_date = ?";
        String todayBookingQuery = "SELECT COUNT(*) FROM checkin WHERE checkin_date = ?";
        String totalIncomeQuery = "SELECT SUM(price) FROM checkin";

        try (Connection connect = Database.connectDb()) {

            // Today's Income
            try (PreparedStatement stmt = connect.prepareStatement(todayIncomeQuery)) {
                stmt.setString(1, today);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double income = rs.getDouble(1);
                    incomeLvl.setText(String.format("%.2f", income));
                } else {
                    incomeLvl.setText("0.00");
                }
            }

            // Today's Bookings
            try (PreparedStatement stmt = connect.prepareStatement(todayBookingQuery)) {
                stmt.setString(1, today);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int bookings = rs.getInt(1);
                    bookingLvl.setText(String.valueOf(bookings));
                } else {
                    bookingLvl.setText("0");
                }
            }

            // Total Income
            try (PreparedStatement stmt = connect.prepareStatement(totalIncomeQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double totalIncome = rs.getDouble(1);
                    t_income.setText(String.format("%.2f", totalIncome));
                } else {
                    t_income.setText("0.00");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load dashboard stats: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
