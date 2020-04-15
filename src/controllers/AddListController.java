/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import todoclient.TodoClient;

/**
 * FXML Controller class
 *
 * @author Fouad
 */
public class AddListController implements Initializable, Requests {

    ManageConnection connection = new ManageConnection();
    JSONObject responsejson;
    JSONObject online;
    JSONObject addListJson;
    @FXML
    private DatePicker start_date_picker;
    @FXML
    private TextField list_title_txtfield;
    @FXML
    private DatePicker dead_line_picker;
    @FXML
    private ColorPicker list_color_picker;
    @FXML
    private Button save_btn;
    @FXML
    private Hyperlink home_link;
    @FXML
    private Label title_list_label;
    @FXML
    private Label start_date_label;
    @FXML
    private Label dead_line_label;
    @FXML
    private Button close_addlist;
    @FXML
    private Label result_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setSettings();
        // connect to server 
        if (connection.connectToServer(ConnectToServerController.ip)) {
            setOnline();
        }

    }

    @FXML
    private void addList(ActionEvent event) {
        if (validateAddList()) {
            sendRequestToServer();
            list_title_txtfield.setText("");
        }
    }

    @FXML
    private void backToHome(ActionEvent event) {
        try {
            connection.closeConnection();
            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Home.fxml")));
        } catch (IOException ex) {
            System.out.println("Can Not Open Home Page");
        }

    }

    private boolean validateAddList() {
        boolean flag = true;
        if (list_title_txtfield.getText().trim().isEmpty()) {
            flag = false;
            title_list_label.setText("*");
        } else {
            title_list_label.setText("");
        }
        if (start_date_picker.getValue() == null) {
            flag = false;
            start_date_label.setText("*");
        } else {
            start_date_label.setText("");
        }

        if (dead_line_picker.getValue() == null) {
            flag = false;
            dead_line_label.setText("*");
        } else {
            dead_line_label.setText("");
        }
        return flag;

    }

    private void setOnline() {
        online = new JSONObject();
        online.put("functionNumber", "15");
        online.put("username", LoginController.username);
        connection.ps.println(online);
    }

    @Override
    public void sendRequestToServer() {
        addListJson = new JSONObject();
        addListJson.put("functionNumber", "4");
        addListJson.put("username", LoginController.username);
        addListJson.put("listtitle", list_title_txtfield.getText());
        addListJson.put("startdate", start_date_picker.getValue());
        addListJson.put("deadline", dead_line_picker.getValue());
        addListJson.put("color", list_color_picker.getValue());
        connection.ps.println(addListJson);
        recieveResponseFromServer();
    }

    @Override
    public void recieveResponseFromServer() {
        try {
            responsejson = new JSONObject(connection.dis.readLine());
            if (responsejson.get("addListCondition").toString().equalsIgnoreCase("true")) {
                result_label.setText("List Saved Successfully");
                list_title_txtfield.setText("");
            }
           // connection.closeConnection();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Recieve From Server").show();
        }
    }

    @FXML
    private void startDateAction(ActionEvent event) {
        dead_line_picker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(start_date_picker.getValue()) < 0);
            }
        });
        dead_line_picker.setDisable(false);
    }

    @FXML
    private void hideCloseBtn(MouseEvent event) {
        close_addlist.setVisible(false);
    }

    @FXML
    private void showCloseBtn(MouseEvent event) {
        close_addlist.setVisible(true);
    }

    @FXML
    private void closeAddList(ActionEvent event) {
        connection.closeConnection();
        TodoClient.parentStage.close();
    }

    private void setSettings() {
        close_addlist.setVisible(false);
        // disable deadline
        dead_line_picker.setDisable(true);
        // set start from now
        start_date_picker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

}
