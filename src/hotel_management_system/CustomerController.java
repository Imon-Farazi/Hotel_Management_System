package hotel_management_system;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private Button exitBtn, minimize, avaBtn, dbBtn, cusBtn, sinoutBtn;
    @FXML
    private TableView<Customer> table;
    @FXML
    private TableColumn<Customer, String> id, firstName, lastName, number, checkin, checkout;
    @FXML
    private TableColumn<Customer, Double> price;
    @FXML
    private TextField search;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up columns
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        number.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        checkin.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkout.setCellValueFactory(new PropertyValueFactory<>("checkOut"));

        loadCustomerData();

        // Set up search functionality
        FilteredList<Customer> filteredData = new FilteredList<>(customerList, p -> true);
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal.toLowerCase();
            filteredData.setPredicate(c -> {
                if (lower.isEmpty()) {
                    return true;
                }
                return c.getFirstName().toLowerCase().contains(lower)
                        || c.getLastName().toLowerCase().contains(lower)
                        || c.getPhoneNumber().toLowerCase().contains(lower)
                        || c.getCheckIn().toLowerCase().contains(lower)
                        || c.getCheckOut().toLowerCase().contains(lower)
                        || String.valueOf(c.getPrice()).contains(lower);
            });
        });

        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

    private void loadCustomerData() {
        customerList.clear();
        String query = "SELECT * FROM checkin";

        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("customer_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone");
                double price = rs.getDouble("price");
                String checkIn = rs.getString("checkin_date");
                String checkOut = rs.getString("checkout_date");

                customerList.add(new Customer(id, firstName, lastName, phoneNumber, price, checkIn, checkOut));
            }

            table.setItems(customerList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers: " + e.getMessage());
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
    private void openAva(ActionEvent event) {
        loadScene("Rooms.fxml", avaBtn);
    }

    @FXML
    private void openDb(ActionEvent event) {
        loadScene("DashBoard.fxml", dbBtn);
    }

    @FXML
    private void openCus(ActionEvent event) {
        // Already on customer panel, do nothing or reload
        loadCustomerData(); // optional refresh
    }

    @FXML
    private void sinout(ActionEvent event) {
        loadScene("FXMLDocument.fxml", sinoutBtn);
    }

    private void loadScene(String fxmlFile, Button sourceBtn) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) sourceBtn.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load " + fxmlFile + ": " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
