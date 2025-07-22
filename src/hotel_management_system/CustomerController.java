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

public class CustomerController implements Initializable {

    @FXML
    private Button exitBtn;
    @FXML
    private Button minimize;
    @FXML
    private Button avaBtn;
    @FXML
    private Button dbBtn;
    @FXML
    private Button cusBtn;
    @FXML
    private Button sinoutBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Optional: Initialization logic
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
    private void openAva(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Rooms.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) avaBtn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Rooms.fxml", e);
        }
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
    }

    @FXML
    private void sinout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) sinoutBtn.getScene().getWindow();
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
}
