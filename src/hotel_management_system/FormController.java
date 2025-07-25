package hotel_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FormController implements Initializable {

    @FXML
    private AnchorPane checkinForm;

    @FXML
    private Button exitBtn, miniBtn, resetBtn, receiptBtn, checkinBtn;

    @FXML
    private Label customerNumber;

    @FXML
    private TextField firstName, lastName, phone, email;

    @FXML
    private DatePicker checkin_date, checkout_date;
    @FXML
    private Label totalDays;
    @FXML
    private Label total;
    private long stayDuration = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadNextCustomerID();
    }

    // Exit button
    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }

    // Minimize button
    @FXML
    private void minimize(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    // Reset form fields
    @FXML
    private void reset(ActionEvent event) {
        firstName.clear();
        lastName.clear();
        phone.clear();
        email.clear();
        checkin_date.setValue(null);
        checkout_date.setValue(null);
        loadNextCustomerID(); // Reload next ID
    }

    // Show receipt in an alert
    @FXML
    private void receipt(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt.fxml"));
            AnchorPane pane = loader.load();
            totalDays();
            ReceiptController controller = loader.getController();
            String receiptText = String.format("""
                ------------ Receipt ------------
                                               
                Customer ID : %s
                Name        : %s %s
                Phone       : %s
                Email       : %s
                Check-In    : %s
                Check-Out   : %s
                Duration    : %d Day(s)
                                               
                                               
                                   Verifyed by 
                                   imonFarazi
                                               
                ---------------------------------
                """,
                    customerNumber.getText(),
                    firstName.getText(),
                    lastName.getText(),
                    phone.getText(),
                    email.getText(),
                    checkin_date.getValue(),
                    checkout_date.getValue(),
                    stayDuration
            );

            controller.setReceiptText(receiptText);

            Stage stage = new Stage();
            stage.setTitle("Receipt");
            stage.setScene(new Scene(pane));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check-in (Insert into DB)
    @FXML
    private void checkin(ActionEvent event) {
        Connection conn = Database.connectDb();

        if (conn == null) {
            showAlert("Database Error", "Could not connect to the database.");
            return;
        }

        String sql = "INSERT INTO checkin (first_name, last_name, phone, email, checkin_date, checkout_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, firstName.getText());
            stmt.setString(2, lastName.getText());
            stmt.setString(3, phone.getText());
            stmt.setString(4, email.getText());
            stmt.setDate(5, java.sql.Date.valueOf(checkin_date.getValue()));
            stmt.setDate(6, java.sql.Date.valueOf(checkout_date.getValue()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    customerNumber.setText(String.valueOf(generatedId));
                }
                showAlert("Success", "Customer checked in successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("SQL Error", "Failed to insert data: " + e.getMessage());
        }
    }

    public void totalDays() {
        if (checkin_date.getValue() == null || checkout_date.getValue() == null) {
            showAlert("Error", "Please select both check-in and check-out dates.");
            return;
        }

        if (checkout_date.getValue().isBefore(checkin_date.getValue())) {
            showAlert("Error", "Invalid Check-Out Date. It must be after Check-In Date.");
            totalDays.setText("0 Day(s)");
            stayDuration = 0;
        } else {
            stayDuration = java.time.temporal.ChronoUnit.DAYS.between(checkin_date.getValue(), checkout_date.getValue());
            totalDays.setText(stayDuration + " Day(s)");
        }
    }

    // Load next auto-increment customer ID from DB
    private void loadNextCustomerID() {
        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_SCHEMA = 'hotel_data' AND TABLE_NAME = 'checkin'")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int nextId = rs.getInt("AUTO_INCREMENT");
                customerNumber.setText(String.valueOf(nextId));
            } else {
                customerNumber.setText("N/A");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            customerNumber.setText("Error");
        }
    }

    // Show info dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
