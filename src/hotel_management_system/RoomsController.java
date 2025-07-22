package hotel_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class RoomsController implements Initializable {

    private TextField txtRoomNumber;
    private TextField txtRoomType;
    private TextField txtPrice;

    private TextField searchField;

    private TableView<Room> roomTable;
    private TableColumn<Room, Integer> colId;
    private TableColumn<Room, String> colRoomNumber;
    private TableColumn<Room, String> colRoomType;
    private TableColumn<Room, Double> colPrice;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private ObservableList<Room> roomList = FXCollections.observableArrayList();

    private int selectedRoomId = -1;  // for update/delete
    @FXML
    private Button exitBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRoomData();

        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRoomId = newSelection.getId();
                txtRoomNumber.setText(newSelection.getRoomNumber());
                txtRoomType.setText(newSelection.getRoomType());
                txtPrice.setText(String.valueOf(newSelection.getPrice()));
            }
        });
    }

    private void loadRoomData() {
        roomList.clear();
        try {
            connect = Database.connectDb();
            String sql = "SELECT * FROM rooms";
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                roomList.add(new Room(
                        result.getInt("id"),
                        result.getString("room_number"),
                        result.getString("room_type"),
                        result.getDouble("price")
                ));
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
            colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            roomTable.setItems(roomList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAdd() {
        try {
            connect = Database.connectDb();

            String sql = "INSERT INTO rooms(room_number, room_type, price) VALUES (?, ?, ?)";
            prepare = connect.prepareStatement(sql);

            prepare.setString(1, txtRoomNumber.getText());
            prepare.setString(2, txtRoomType.getText());
            prepare.setDouble(3, Double.parseDouble(txtPrice.getText()));

            prepare.executeUpdate();

            clearFields();
            loadRoomData();

            showAlert(Alert.AlertType.INFORMATION, "Room Added", "New room added successfully.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUpdate() {
        if (selectedRoomId == -1) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a room to update.");
            return;
        }
        try {
            connect = Database.connectDb();

            String sql = "UPDATE rooms SET room_number=?, room_type=?, price=? WHERE id=?";
            prepare = connect.prepareStatement(sql);

            prepare.setString(1, txtRoomNumber.getText());
            prepare.setString(2, txtRoomType.getText());
            prepare.setDouble(3, Double.parseDouble(txtPrice.getText()));
            prepare.setInt(4, selectedRoomId);

            prepare.executeUpdate();

            clearFields();
            loadRoomData();

            showAlert(Alert.AlertType.INFORMATION, "Room Updated", "Room updated successfully.");

            selectedRoomId = -1;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        if (selectedRoomId == -1) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a room to delete.");
            return;
        }
        try {
            connect = Database.connectDb();

            String sql = "DELETE FROM rooms WHERE id=?";
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, selectedRoomId);
            prepare.executeUpdate();

            clearFields();
            loadRoomData();

            showAlert(Alert.AlertType.INFORMATION, "Room Deleted", "Room deleted successfully.");

            selectedRoomId = -1;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            roomTable.setItems(roomList);
            return;
        }

        ObservableList<Room> filteredList = FXCollections.observableArrayList();

        for (Room r : roomList) {
            if (r.getRoomNumber().toLowerCase().contains(searchText)
                    || r.getRoomType().toLowerCase().contains(searchText)) {
                filteredList.add(r);
            }
        }

        roomTable.setItems(filteredList);
    }

    private void clearFields() {
        txtRoomNumber.clear();
        txtRoomType.clear();
        txtPrice.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Room class for table rows
    public static class Room {

        private int id;
        private String roomNumber;
        private String roomType;
        private double price;

        public Room(int id, String roomNumber, String roomType, double price) {
            this.id = id;
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getRoomType() {
            return roomType;
        }

        public double getPrice() {
            return price;
        }
    }

    private void mini(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleExit() {
        System.exit(0); // Or close the window if preferred
    }

}
