/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import todoclient.TodoClient;

/**
 * FXML Controller class
 *
 * @author mostafa
 */
public class ItemController implements Initializable, Requests {

    ManageConnection connection = new ManageConnection();
    private JSONArray response;
    private JSONObject item;
    private JSONObject online;
    public static Stage dialog;
    @FXML
    private TextField itemTitle_txt;
    @FXML
    private CheckBox todoCheckBox;
    @FXML
    private CheckBox inProgressCheckBox;
    @FXML
    private CheckBox doneCheckBox;
    @FXML
    private TextArea description_txt;
    @FXML
    private TextArea allComments_txt;
    @FXML
    private TextArea comment_txt;
    @FXML
    private Label title_validate;
    @FXML
    private Button addCollaborators;
    @FXML
    private Label collaorate_name;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (connection.connectToServer(ConnectToServerController.ip)) {
            setOnline();
            sendRequestToServer();

        }
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        if (!itemTitle_txt.getText().trim().equals("")) {
            JSONObject updateItem = new JSONObject();
            updateItem.put("functionNumber", "16");
            updateItem.put("itemID", HomeController.itemID);
            updateItem.put("comment", allComments_txt.getText());
            updateItem.put("desc", description_txt.getText());
            updateItem.put("updater", LoginController.username);
            if (inProgressCheckBox.isSelected()) {
                updateItem.put("status", "inprogress");
            } else if (doneCheckBox.isSelected()) {
                updateItem.put("status", "done");
            } else {
                updateItem.put("status", "todo");
            }
            updateItem.put("title", itemTitle_txt.getText());
            connection.ps.println(updateItem);
            goToHome(event);
        } else {
            title_validate.setText("*");
        }
    }

    @FXML
    private void addComment(ActionEvent event) {
        allComments_txt.appendText("\n" + LoginController.username + " : " + comment_txt.getText());
        comment_txt.clear();
    }

    @Override
    public void sendRequestToServer() {
        item = new JSONObject();
        item.put("functionNumber", "14");
        item.put("itemID", HomeController.itemID);
        connection.ps.println(item);
        recieveResponseFromServer();
    }

    @Override
    public void recieveResponseFromServer() {
        try {
            response = new JSONArray(connection.dis.readLine());
            setItemDetail(response.getJSONObject(0));

            if (response.getJSONObject(1).length() != 0) {
                if (response.getJSONObject(1).getBoolean("ACCEPT_STATE")) {
                    collaorate_name.setText("Item Assigned To "+response.getJSONObject(1).getString("USERNAME")+" Created By "+response.getJSONObject(1).getString("CREATOR"));
                    addCollaborators.setText("Assigned");
                } else {
                    addCollaborators.setText("Pending Request");
                }
                addCollaborators.setDisable(true);
                if (!response.getJSONObject(1).getString("CREATOR").equals(LoginController.username)) {

                    itemTitle_txt.setDisable(true);
                    description_txt.setDisable(true);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setItemDetail(JSONObject itemDetail) {
        itemTitle_txt.setText(itemDetail.getString("title"));
        description_txt.setText(itemDetail.getString("desc"));
        allComments_txt.setText(itemDetail.getString("comments"));
        switch (itemDetail.getString("status")) {
            case "todo":
                todoCheckBox.setSelected(true);
                break;
            case "inprogress":
                inProgressCheckBox.setSelected(true);
                break;
            case "done":
                doneCheckBox.setSelected(true);
                break;
        }
    }

    @FXML
    private void goToHome(ActionEvent event) {
        boolean status = connection.closeConnection();
        try {
            if (status) {
                Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Home.fxml")));
            }

        } catch (IOException ex) {
            Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void todoSelected(ActionEvent event
    ) {
        inProgressCheckBox.setSelected(false);
        doneCheckBox.setSelected(false);
    }

    @FXML
    private void inProgressSelected(ActionEvent event
    ) {
        todoCheckBox.setSelected(false);
        doneCheckBox.setSelected(false);
    }

    @FXML
    private void doneSelected(ActionEvent event
    ) {
        inProgressCheckBox.setSelected(false);
        todoCheckBox.setSelected(false);
    }

    private void setOnline() {
        online = new JSONObject();
        online.put("functionNumber", "15");
        online.put("username", LoginController.username);
        connection.ps.println(online);
    }

    @FXML
    private void addCollaborators(ActionEvent event) {
        boolean status = connection.closeConnection();
        if (status) {
            dialog = new Stage();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/views/addCollaborators.fxml"));
                Scene scene = new Scene(root, 368, 403);
                dialog.setScene(scene);
                dialog.initOwner(TodoClient.parentStage);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.show();
                dialog.setTitle("Assign Friends");
            } catch (IOException ex) {
                System.out.println("Can Not Open Add Collaborators");
            }
        }

    }

    public void reconnect() {
        if (connection.connectToServer(ConnectToServerController.ip)) {
            setOnline();
        }

    }
}
