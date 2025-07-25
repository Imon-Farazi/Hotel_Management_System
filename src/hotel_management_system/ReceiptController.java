package hotel_management_system;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ReceiptController {

    @FXML
    private Label receiptLabel;

    @FXML
    private AnchorPane root;

    public void setReceiptText(String text) {
        receiptLabel.setText(text);
    }

    @FXML
    private void printReceipt() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(root.getScene().getWindow())) {
            boolean success = job.printPage(root);
            if (success) {
                job.endJob();
            } else {
                showAlert("Error", "Failed to print receipt.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
