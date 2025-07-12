package hotel_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class FXMLDocumentController implements Initializable {

    @FXML
    private StackPane stackform;

    @FXML
    private AnchorPane mainform;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginbtn;

    @FXML
    private Button close;

    // Database-related fields
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    public void login(){
        String user = username.getText();
        String pass = password.getText();
        String sql ="SELECT * FROM admin WHERE username ='"+user+"' and password = '"+pass+"'";
        connect = Database.connectDb();
        
        try{
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, user );
            prepare.setString(2, pass);
            
            result = prepare.executeQuery();
            
            Alert alert;
            
            if(result.next()){
                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Login..!");
                alert.showAndWait();
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Username/Password..!");
                alert.showAndWait();
            }
        
        }catch (Exception e){
            e.printStackTrace();
        }
        
        
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DB connection
        connect = Database.connectDb();
    }
}
