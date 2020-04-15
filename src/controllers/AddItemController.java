/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Fouad
 */
public class AddItemController implements Initializable, Requests {

    ManageConnection connection = new ManageConnection();
    JSONObject createdItemJson;
    @FXML
    private Button close_additem;
    @FXML
    private TextField itemTitle;
    @FXML
    private TextArea itemDescription;
    @FXML
    private Button saveBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void closeAddItem(ActionEvent event) {
        HomeController.dialog.close();
    }

    @FXML
    private void saveBtnPressed(ActionEvent event) {
        if (itemTitle.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "item title cannot be empty", ButtonType.OK).show();
        } else {
            connection.connectToServer(ConnectToServerController.ip);
            sendRequestToServer();
            connection.closeConnection();
            closeAddItem(event);
        }
    }

    @Override
    public void sendRequestToServer() {
        createdItemJson = new JSONObject();
        createdItemJson.put("functionNumber", "12");
        createdItemJson.put("title", itemTitle.getText().trim());
        createdItemJson.put("desc", itemDescription.getText().trim());
        createdItemJson.put("listID", HomeController.listID);
        createdItemJson.put("username", LoginController.username);
        connection.ps.println(createdItemJson);
    }

    @Override
    public void recieveResponseFromServer() {
    }

}
