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
 *
 * @author Shorouk
 */
public class LoginController implements Initializable, Requests {

    ManageConnection connection = new ManageConnection();
    public static String username;
    private JSONObject loginJson;
    private JSONObject serverMessage;
    @FXML
    private TextField name_textfield;
    @FXML
    private PasswordField password_textfield;
    @FXML
    private Button login_btn;
    @FXML
    private Hyperlink sign_up_btn;
    @FXML
    private Hyperlink recovery_link;
    @FXML
    private Label user_name_lbl;
    @FXML
    private Label password_lbl;
    @FXML
    private Label ip_lbl;
    @FXML
    private Button close_login;
    @FXML
    private Pane top_pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        close_login.setVisible(false);
        // password space .
        Pattern pattern = Pattern.compile("[a-zA-Z0-9\\-\\+]*");
        password_textfield.setTextFormatter(new TextFormatter<String>(change -> {
            if (pattern.matcher(change.getText()).matches()) {
                return change;
            }
            return null;
        }));
    }

    /**
     * navigates to signUp page when signUp button is pressed .
     *
     * @param event
     */
    @FXML
    private void moveToSignUp(ActionEvent event) {
        try {
            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/SignUp.fxml")));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "cant open sign up page").show();
        }
    }

    /**
     * navigates to Recovery page when signUp button is pressed .
     *
     * @param event
     */
    @FXML
    private void moveToRecovery(ActionEvent event) {
        try {
            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Recovery.fxml")));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "can not open recovery page").show();
        }
    }

    /**
     * validate all login fields are filled then connect to the server and send
     * the data from text fields.
     *
     * @param event
     */
    @FXML
    private void loginAction(ActionEvent event) {
        if (loginValidation()) {
            user_name_lbl.setText("");
            password_lbl.setText("");
            user_name_lbl.setText("");
            if (connection.connectToServer(ConnectToServerController.ip)) {
                sendRequestToServer();
            }
        }
    }

    /**
     * check that all fields meet the constraints required .
     *
     * @return boolean
     */
    private boolean loginValidation() {
        boolean flag = true;
        if (name_textfield.getText().trim().isEmpty()) {
            flag = false;
            user_name_lbl.setText("*");
        } else {
            user_name_lbl.setText("");
        }
        if ((password_textfield.getText().trim().isEmpty())) { //|| (password_textfield.getText().length() < 7)
            flag = false;
            password_lbl.setText("*");
        } else {
            password_lbl.setText("");
        }
        return flag;
    }

    @Override
    public void sendRequestToServer() {
        loginJson = new JSONObject();
        loginJson.put("functionNumber", "1");
        loginJson.put("username", name_textfield.getText());
        loginJson.put("password", password_textfield.getText());
        connection.ps.println(loginJson);
        recieveResponseFromServer();
    }

    @Override
    public void recieveResponseFromServer() {
        try {
            serverMessage = new JSONObject(connection.dis.readLine());
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Recieve From Server").show();
        }
        connection.closeConnection();
        if (serverMessage.get("loginCondition").toString().equals("true")) {
            username = name_textfield.getText().trim();
            try {
                Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Home.fxml")));
            } catch (IOException ex) {
                System.out.println("dibdih");
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ip_lbl.setText("Invalid User-Name or Password");
            name_textfield.setText("");
            password_textfield.setText("");
        }

    }

    @FXML
    private void closeLogin(ActionEvent event) {
        todoclient.TodoClient.parentStage.close();
    }

    @FXML
    private void showCloseBtn(MouseEvent event) {
        close_login.setVisible(true);
    }

    @FXML
    private void hideCloseBtn(MouseEvent event) {
        close_login.setVisible(false);
    }

}
