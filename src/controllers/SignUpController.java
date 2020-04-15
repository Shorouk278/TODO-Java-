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
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author awatef
 */
public class SignUpController implements Initializable, Requests {

    ManageConnection connection = new ManageConnection();
    JSONObject recieveObj;
    JSONObject signUpJson;

    @FXML
    private Label user_name_lbl;
    @FXML
    private TextField user_name_txt;
    @FXML
    private Label password_lbl;
    @FXML
    private PasswordField password_txt;
    @FXML
    private Label confirm_password_lbl;
    @FXML
    private PasswordField confirm_password_txt;
    @FXML
    private Label question1_lbl;
    @FXML
    private TextField question1_txt;
    @FXML
    private Label question2_lbl;
    @FXML
    private TextField Question2_txt;
    @FXML
    private Button signup_btn;
    @FXML
    private Hyperlink back_btn;
    @FXML
    private Label result_label;
    @FXML
    private Pane sign_pane;
    @FXML
    private Button close_sign;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TodoClient.parentStage.setMaximized(true);
        close_sign.setVisible(false);
        Pattern pattern = Pattern.compile("[a-zA-Z0-9\\-\\+]*");
        password_txt.setTextFormatter(new TextFormatter<String>(change -> {
            if (pattern.matcher(change.getText()).matches()) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void signUpToDB_RetToSignIn(ActionEvent event) {
        if (checkRegister()) {
            user_name_lbl.setText("");
            password_lbl.setText("");
            confirm_password_lbl.setText("");
            question1_lbl.setText("");
            question2_lbl.setText("");
            connection.connectToServer(ConnectToServerController.ip);
            sendRequestToServer();
            recieveResponseFromServer();
            connection.closeConnection();
        } else {
            result_label.setText("");
        }
    }

    private boolean checkRegister() {
        boolean flag = true;
        if (user_name_txt.getText().isEmpty()) {
            flag = false;
            user_name_lbl.setText("*");

        } else {
            user_name_lbl.setText("");
        }
        if ((password_txt.getText().isEmpty())) {
            flag = false;
            password_lbl.setText("*");
        } else {
            password_lbl.setText("");
        }
        if (!(confirm_password_txt.getText()).equals(password_txt.getText())) {
            flag = false;
            confirm_password_lbl.setText("*");
        } else {
            confirm_password_lbl.setText("");
        }
        if (question1_txt.getText().isEmpty()) {
            flag = false;
            question1_lbl.setText("*");
        } else {
            question1_lbl.setText("");
        }
        if (Question2_txt.getText().isEmpty()) {
            flag = false;
            question2_lbl.setText("*");
        } else {
            question2_lbl.setText("");
        }
        return flag;
    }

    @Override
    public void sendRequestToServer() {
        signUpJson = new JSONObject();
        signUpJson.put("functionNumber", "2");
        signUpJson.put("username", user_name_txt.getText().trim());
        signUpJson.put("password", password_txt.getText());
        signUpJson.put("answer1", question1_txt.getText());
        signUpJson.put("answer2", Question2_txt.getText());
        connection.ps.println(signUpJson);
    }

    @Override
    public void recieveResponseFromServer() {
        try {
            recieveObj = new JSONObject(connection.dis.readLine());
            if (recieveObj.get("signUpCondition").toString().equalsIgnoreCase("true")) {
                result_label.setText("User Registered Successfully");
                password_txt.setText("");
                confirm_password_txt.setText("");
                question1_txt.setText("");
                Question2_txt.setText("");
            } else {
                result_label.setText("User Name Already Exist!!");
            }
            user_name_txt.setText("");
        } catch (IOException ex) {
            Logger.getLogger(SignUpController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void closeSign(ActionEvent event) {
        TodoClient.parentStage.close();
    }

    @FXML
    private void hideCloseBtn(MouseEvent event) {
        close_sign.setVisible(false);
    }

    @FXML
    private void showCloseBtn(MouseEvent event) {
        close_sign.setVisible(true);
    }

}
