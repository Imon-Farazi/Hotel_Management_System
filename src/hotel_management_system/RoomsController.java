package hotel_management_system;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author I_Farazi
 */
public class RoomsController implements Initializable {

    @FXML
    private Button exitBtn;
    @FXML
    private Button avaRoonBtn;
    @FXML
    private Button dbBtn;
    @FXML
    private Button cusBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button miniBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Optional: Initialize if needed
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void openAvaR(ActionEvent event) {
    }

    @FXML
    private void openDb(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) dbBtn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("DashBoard.fxml", e);
        }
    }

    @FXML
    private void openCus(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) cusBtn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Customer.fxml", e);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("FXMLDocument.fxml", e);
        }
    }

    private void showError(String file, IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load " + file + ": " + e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void mini(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
