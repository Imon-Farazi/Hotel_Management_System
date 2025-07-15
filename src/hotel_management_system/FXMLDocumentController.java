package hotel_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginbtn;

    // Database-related
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    @FXML
    private StackPane stackform;
    @FXML
    private AnchorPane mainform;
    @FXML
    private Button close;

    @FXML
    public void login() {
        String user = username.getText().trim();
        String pass = password.getText().trim();

        Alert alert;

        if (user.isEmpty() || pass.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            connect = Database.connectDb();

            if (connect == null) {
                throw new Exception("Database connection failed.");
            }

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, user);
            prepare.setString(2, pass);
            result = prepare.executeQuery();

            if (result.next()) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome, " + user + "!");
                alert.showAndWait();

                // Load Dashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Close login window
                Stage loginStage = (Stage) loginbtn.getScene().getWindow();
                loginStage.close();

            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect username or password.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            try {
                if (result != null) result.close();
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Optional: test database on startup or leave empty
        // connect = Database.connectDb();
    }
}
