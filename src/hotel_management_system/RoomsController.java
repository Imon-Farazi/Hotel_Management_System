package hotel_management_system;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
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

public class RoomsController implements Initializable {

    @FXML
    private Button exitBtn, avaRoonBtn, dbBtn, cusBtn, logoutBtn, miniBtn;
    @FXML
    private Button addBtn, delbtn, clbtn, upbtn, checkbtn;

    @FXML
    private TextField roomNumberField;
    @FXML
    private ComboBox<String> roomTypeBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private TextField priceField;

    @FXML
    private TableColumn<Room, String> roomNumberCol;
    @FXML
    private TableColumn<Room, String> roomTypeCol;
    @FXML
    private TableColumn<Room, String> roomStatusCol;
    @FXML
    private TableColumn<Room, Double> priceCol;
    @FXML
    private TableView<Room> roomTable;

    private final ObservableList<Room> roomList = FXCollections.observableArrayList();
    @FXML
    private TextField searchIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roomTypeBox.getItems().addAll("Single", "Double", "Suite");
        statusBox.getItems().addAll("Available", "Booked", "Maintenance");

        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        loadRoomData();
        FilteredList<Room> filteredData = new FilteredList<>(roomList, p -> true);

        searchIcon.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(room -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (room.getRoomNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (room.getRoomType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (room.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return String.valueOf(room.getPricePerNight()).contains(lowerCaseFilter);
                }
            });
        });

        SortedList<Room> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(roomTable.comparatorProperty());
        roomTable.setItems(sortedData);

        // Fill fields on row click
        roomTable.setOnMouseClicked(event -> {
            Room selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomNumberField.setText(selected.getRoomNumber());
                roomTypeBox.setValue(selected.getRoomType());
                statusBox.setValue(selected.getStatus());
                priceField.setText(String.valueOf(selected.getPricePerNight()));
            }
        });
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
        loadPage("DashBoard.fxml", dbBtn);
    }

    @FXML
    private void openCus(ActionEvent event) {
        loadPage("Customer.fxml", cusBtn);
    }

    @FXML
    private void logout(ActionEvent event) {
        loadPage("FXMLDocument.fxml", logoutBtn);
    }

    private void loadPage(String fxmlFile, Button triggerButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) triggerButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load " + fxmlFile + ": " + e.getMessage());
        }
    }

    @FXML
    private void mini(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void addRoom(ActionEvent event) {
        String number = roomNumberField.getText();
        String type = roomTypeBox.getValue();
        String status = statusBox.getValue();
        String priceText = priceField.getText();

        if (number.isEmpty() || type == null || status == null || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
            return;
        }

        String sql = "INSERT INTO rooms (room_number, room_type, status, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);
            stmt.setString(2, type);
            stmt.setString(3, status);
            stmt.setDouble(4, price);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully.");
            loadRoomData();
            clearFields(null);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add room: " + e.getMessage());
        }
    }

    @FXML
    private void updateRoom(ActionEvent event) {
        String roomNumber = roomNumberField.getText();

        if (roomNumber.isEmpty()) {
            Room selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomNumber = selected.getRoomNumber();
            } else {
                showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter or select a Room Number.");
                return;
            }
        }

        String type = roomTypeBox.getValue();
        String status = statusBox.getValue();
        String priceText = priceField.getText();

        if (type == null && status == null && priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Update", "No new data provided to update.");
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE rooms SET ");
        boolean hasPrevious = false;

        if (type != null) {
            queryBuilder.append("room_type = ?");
            hasPrevious = true;
        }
        if (status != null) {
            if (hasPrevious) {
                queryBuilder.append(", ");
            }
            queryBuilder.append("status = ?");
            hasPrevious = true;
        }
        if (!priceText.isEmpty()) {
            if (hasPrevious) {
                queryBuilder.append(", ");
            }
            queryBuilder.append("price = ?");
        }

        queryBuilder.append(" WHERE room_number = ?");

        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            int index = 1;
            if (type != null) {
                stmt.setString(index++, type);
            }
            if (status != null) {
                stmt.setString(index++, status);
            }
            if (!priceText.isEmpty()) {
                stmt.setDouble(index++, Double.parseDouble(priceText));
            }
            stmt.setString(index, roomNumber);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room updated successfully.");
                loadRoomData();
                clearFields(null);
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No room found with that number.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid number for price.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update room: " + e.getMessage());
        }
    }

    @FXML
    private void deleteRoom(ActionEvent event) {
        String roomNumber = roomNumberField.getText();

        if (roomNumber.isEmpty()) {
            Room selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                roomNumber = selected.getRoomNumber();
            } else {
                showAlert(Alert.AlertType.WARNING, "Missing Data", "Please enter or select a Room Number.");
                return;
            }
        }

        String sql = "DELETE FROM rooms WHERE room_number = ?";

        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room deleted successfully.");
                loadRoomData();
                clearFields(null);
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No room found with that number.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete room: " + e.getMessage());
        }
    }

    @FXML
    private void clearFields(ActionEvent event) {
        roomNumberField.clear();
        roomTypeBox.setValue(null);
        statusBox.setValue(null);
        priceField.clear();
        roomTable.getSelectionModel().clearSelection();
    }

    private void loadRoomData() {
        roomList.clear();
        String query = "SELECT * FROM rooms";

        try (Connection conn = Database.connectDb(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String number = rs.getString("room_number");
                String type = rs.getString("room_type");
                String status = rs.getString("status");
                double price = rs.getDouble("price");

                roomList.add(new Room(number, type, status, price));
            }

            roomTable.setItems(roomList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load rooms: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Room {

        private final SimpleStringProperty roomNumber;
        private final SimpleStringProperty roomType;
        private final SimpleStringProperty status;
        private final SimpleDoubleProperty pricePerNight;

        public Room(String roomNumber, String roomType, String status, double pricePerNight) {
            this.roomNumber = new SimpleStringProperty(roomNumber);
            this.roomType = new SimpleStringProperty(roomType);
            this.status = new SimpleStringProperty(status);
            this.pricePerNight = new SimpleDoubleProperty(pricePerNight);
        }

        public String getRoomNumber() {
            return roomNumber.get();
        }

        public String getRoomType() {
            return roomType.get();
        }

        public String getStatus() {
            return status.get();
        }

        public double getPricePerNight() {
            return pricePerNight.get();
        }
    }
}
