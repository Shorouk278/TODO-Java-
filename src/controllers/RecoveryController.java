
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

/**
 *
 * @author Shorouk
 */
public class RecoveryController implements Initializable, Requests {

    boolean flag = true;
    private JSONObject recoveryJson;
    private JSONObject reciever;
    ManageConnection connection = new ManageConnection();

    @FXML
    private TextField user_name_textfield;
    @FXML
    private Label question_one_label;
    @FXML
    private TextField answer_one_textfield;
    @FXML
    private Label question_two_label;
    @FXML
    private TextField answer_two_textfield;
    @FXML
    private Label recovery_password_label;
    @FXML
    private Button submit_btn;
    @FXML
    private Label user_name_lbl;
    @FXML
    private Label question2_lbl;
    @FXML
    private Label question1_lbl;
    @FXML
    private Pane recovery_pane;
    @FXML
    private Button close_recovery;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TodoClient.parentStage.setMaximized(true);
        close_recovery.setVisible(false);
    }

    @FXML
    private void submitAction(ActionEvent event) {
        if (recoveryValidation()) {
            if (connection.connectToServer(ConnectToServerController.ip)) {
                sendRequestToServer();
            }
        } else {
            recovery_password_label.setText("");
        }
    }

    public boolean recoveryValidation() {
        if (user_name_textfield.getText().trim().isEmpty()) {
            user_name_lbl.setText("*");
            flag = false;
        } else {
            user_name_lbl.setText("");
            flag = true;
        }
        if (answer_one_textfield.getText().trim().isEmpty()) {
            question1_lbl.setText("*");
            flag = false;
        } else {
            question1_lbl.setText("");
            flag = true;
        }

        if (answer_two_textfield.getText().trim().isEmpty()) {
            question2_lbl.setText("*");
            flag = false;
        } else {
            question2_lbl.setText("");
            flag = true;
        }
        return flag;
    }

    @Override
    public void sendRequestToServer() {
        recoveryJson = new JSONObject();
        recoveryJson.put("functionNumber", "3");
        recoveryJson.put("username", user_name_textfield.getText().trim());
        recoveryJson.put("answer1", answer_one_textfield.getText());
        recoveryJson.put("answer2", answer_two_textfield.getText());
        connection.ps.println(recoveryJson);
        recieveResponseFromServer();
    }

    @Override
    public void recieveResponseFromServer() {
        try {
            reciever = new JSONObject(connection.dis.readLine());
            recovery_password_label.setText(reciever.getString("password").toString());
            answer_one_textfield.setText("");
            answer_two_textfield.setText("");
            connection.closeConnection();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Reciever  From Server").show();
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Login.fxml")));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Open Login Page").show();
        }

    }

    @FXML
    private void closeRecovery(ActionEvent event) {
        TodoClient.parentStage.close();
    }

    @FXML
    private void hideCloseBtn(MouseEvent event) {
        close_recovery.setVisible(false);
    }

    @FXML
    private void showCloseBtn(MouseEvent event) {
        close_recovery.setVisible(true);
    }
}
