/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author awatef
 */
public class ConnectToServerController implements Initializable {

    @FXML
    private Label ip_lbl;
    @FXML
    private TextField ip_txt;
    @FXML
    private BorderPane rootpane;

    private BorderPane pane;
    public static String ip;
    @FXML
    private Button close_btn;
    @FXML
    private Button connect_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ip_txt.setFocusTraversable(false);
    }

    @FXML
    private void connectAction(ActionEvent event) {
        if (!ip_txt.getText().matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$")) {
            ip_lbl.setText("Please Use Approriate Ip Pattern");
        } else {
            try {
                ip_lbl.setText("");
                ip = ip_txt.getText();
                Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Login.fxml")));
            } catch (IOException ex) {
                System.out.println("Can Not Open Login Page");
            }

        }

    }

    @FXML
    private void closePage(ActionEvent event) {
        todoclient.TodoClient.parentStage.close();
    }

}
