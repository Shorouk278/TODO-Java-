/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import todoclient.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author awatef
 */
public class ConnectToServerController implements Initializable {

    @FXML
    private AnchorPane rootpane;
    @FXML
    private Label ip_lbl;
    @FXML
    private Button connect_btn;
    private AnchorPane pane;
    public static String ip;
    @FXML
    private TextField ip_txt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    @FXML
    private void connectAction(ActionEvent event) {
        if (!ip_txt.getText().matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$")) {
            ip_lbl.setText("Use approriate ip pattern");
        } else {
            try {
                ip_lbl.setText("");
                ip = ip_txt.getText();
                pane = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
                rootpane.getChildren().setAll(pane);
            } catch (IOException ex) {
                Logger.getLogger(ConnectToServerController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
