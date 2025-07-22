package hotel_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController implements Initializable {

    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtTotalPayment;
    @FXML
    private TextField txtCheckedIn;
    @FXML
    private TextField txtCheckedOut;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colFirstName;
    @FXML
    private TableColumn<Customer, String> colLastName;
    @FXML
    private TableColumn<Customer, String> colPhone;
    @FXML
    private TableColumn<Customer, Double> colTotalPayment;
    @FXML
    private TableColumn<Customer, String> colCheckedIn;
    @FXML
    private TableColumn<Customer, String> colCheckedOut;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private int selectedCustomerId = -1;  // for update/delete

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCustomerData();

        // When user selects a row, populate fields for update/delete
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCustomerId = newSelection.getId();
                txtFirstName.setText(newSelection.getFirstName());
                txtLastName.setText(newSelection.getLastName());
                txtPhone.setText(newSelection.getPhone());
                txtTotalPayment.setText(String.valueOf(newSelection.getTotalPayment()));
                txtCheckedIn.setText(newSelection.getCheckedIn());
                txtCheckedOut.setText(newSelection.getCheckedOut());
            }
        });
    }

    // Load all customers
    private void loadCustomerData() {
        customerList.clear();
        try {
            connect = Database.connectDb();
            String sql = "SELECT * FROM customer";
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                customerList.add(new Customer(
                        result.getInt("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("phone"),
                        result.getDouble("total_payment"),
                        result.getString("checked_in"),
                        result.getString("checked_out")
                ));
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            colTotalPayment.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
            colCheckedIn.setCellValueFactory(new PropertyValueFactory<>("checkedIn"));
            colCheckedOut.setCellValueFactory(new PropertyValueFactory<>("checkedOut"));

            customerTable.setItems(customerList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            connect = Database.connectDb();

            String sql = "INSERT INTO customer(first_name, last_name, phone, total_payment, checked_in, checked_out) VALUES (?, ?, ?, ?, ?, ?)";
            prepare = connect.prepareStatement(sql);

            prepare.setString(1, txtFirstName.getText());
            prepare.setString(2, txtLastName.getText());
            prepare.setString(3, txtPhone.getText());
            prepare.setDouble(4, Double.parseDouble(txtTotalPayment.getText()));
            prepare.setString(5, txtCheckedIn.getText());
            prepare.setString(6, txtCheckedOut.getText());

            prepare.executeUpdate();

            clearFields();
            loadCustomerData();

            showAlert(Alert.AlertType.INFORMATION, "Customer Added", "New customer added successfully.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomerId == -1) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a customer to update.");
            return;
        }
        try {
            connect = Database.connectDb();

            String sql = "UPDATE customer SET first_name=?, last_name=?, phone=?, total_payment=?, checked_in=?, checked_out=? WHERE id=?";
            prepare = connect.prepareStatement(sql);

            prepare.setString(1, txtFirstName.getText());
            prepare.setString(2, txtLastName.getText());
            prepare.setString(3, txtPhone.getText());
            prepare.setDouble(4, Double.parseDouble(txtTotalPayment.getText()));
            prepare.setString(5, txtCheckedIn.getText());
            prepare.setString(6, txtCheckedOut.getText());
            prepare.setInt(7, selectedCustomerId);

            prepare.executeUpdate();

            clearFields();
            loadCustomerData();

            showAlert(Alert.AlertType.INFORMATION, "Customer Updated", "Customer updated successfully.");

            selectedCustomerId = -1;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomerId == -1) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a customer to delete.");
            return;
        }
        try {
            connect = Database.connectDb();

            String sql = "DELETE FROM customer WHERE id=?";
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, selectedCustomerId);
            prepare.executeUpdate();

            clearFields();
            loadCustomerData();

            showAlert(Alert.AlertType.INFORMATION, "Customer Deleted", "Customer deleted successfully.");

            selectedCustomerId = -1;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            customerTable.setItems(customerList);
            return;
        }

        ObservableList<Customer> filteredList = FXCollections.observableArrayList();

        for (Customer c : customerList) {
            if (c.getFirstName().toLowerCase().contains(searchText)
                    || c.getLastName().toLowerCase().contains(searchText)
                    || c.getPhone().toLowerCase().contains(searchText)) {
                filteredList.add(c);
            }
        }

        customerTable.setItems(filteredList);
    }

    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtPhone.clear();
        txtTotalPayment.clear();
        txtCheckedIn.clear();
        txtCheckedOut.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Customer class to match DB columns
    public static class Customer {

        private int id;
        private String firstName;
        private String lastName;
        private String phone;
        private double totalPayment;
        private String checkedIn;
        private String checkedOut;

        public Customer(int id, String firstName, String lastName, String phone, double totalPayment, String checkedIn, String checkedOut) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.totalPayment = totalPayment;
            this.checkedIn = checkedIn;
            this.checkedOut = checkedOut;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhone() {
            return phone;
        }

        public double getTotalPayment() {
            return totalPayment;
        }

        public String getCheckedIn() {
            return checkedIn;
        }

        public String getCheckedOut() {
            return checkedOut;
        }
    }
}
