/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import todoclient.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Fouad
 */
public class AddListController implements Initializable, Runnable {

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
    private AnchorPane rootPane;

    private AnchorPane pane;
    @FXML
    private Label title_list_label;
    @FXML
    private Label start_date_label;
    @FXML
    private Label dead_line_label;

    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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

    @FXML
    private void addList(ActionEvent event) {
        if (validateAddList()) {
            connectToServer(ConnectToServerController.ip);
        }
    }

    @FXML
    private void backToHome(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("/views/Home.fxml"));
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            System.out.println("Can Not Open Home Page");
        }

    }

    private boolean validateAddList() {
        boolean flag = true;
        if (list_title_txtfield.getText().trim().isEmpty()) {
            flag = false;
            title_list_label.setText("*");
        }
        if (start_date_picker.getValue() == null) {
            flag = false;
            start_date_label.setText("*");
        }

        if (dead_line_picker.getValue() == null) {
            flag = false;
            dead_line_label.setText("*");
        }

        return flag;

    }

    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            JSONObject addListJson = new JSONObject();
            addListJson.put("functionNumber", "4");
            addListJson.put("username", LoginController.username);
            addListJson.put("listtitle",list_title_txtfield.getText());
            addListJson.put("startdate", start_date_picker.getValue());
            addListJson.put("deadline", dead_line_picker.getValue());
            addListJson.put("color",list_color_picker.getValue());
            ps.println(addListJson);
            thread = new Thread(this);
            thread.start();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
        }
    }
    
    private void closeConnection(){
        try {
            dis.close();
            ps.close();
            socket.close();
            thread.stop();
        } catch (IOException ex) {
            System.out.println("Close Connection ");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                dis.readLine();
                closeConnection();
            } catch (IOException ex) {
                Logger.getLogger(AddListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    private void startDateAction(ActionEvent event) {
        //  dead_line_picker.mi
        dead_line_picker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(start_date_picker.getValue()) < 0);
            }
        });
        dead_line_picker.setDisable(false);
    }

}
