package hotel_management_system;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashBoardController implements Initializable {

    @FXML
    private Button close;

    @FXML
    private Button minimize;

    @FXML
    private Button avabtn;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button customerBtn;

    @FXML
    private Button signOutBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed
    }

    // General method to open a new window and close current one
    private void openNewWindow(String fxmlFile, Button btn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hotel_management_system/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            Stage currentStage = (Stage) btn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load " + fxmlFile, e.getMessage());
        }
    }

    @FXML
    private void handAvaRoom(ActionEvent event) {
        openNewWindow("Rooms.fxml", avabtn);
    }

    @FXML
    private void openDashboard(ActionEvent event) {
        openNewWindow("DashBoard.fxml", dashboardBtn);
    }

    @FXML
    private void openCustomer(ActionEvent event) {
        openNewWindow("Customer.fxml", customerBtn);
    }

    @FXML
    private void signOut(ActionEvent event) {
        openNewWindow("FXMLDocument.fxml", signOutBtn);  // login page
    }

    @FXML
    private void handelExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void mini(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
