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
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author awatef
 */
public class SignUpController implements Initializable, Runnable {

    @FXML
    private AnchorPane rootPane;
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
    private Label ip_lbl;
    private TextField ip_txt;

    AnchorPane pane = null;
    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;
    private String result;
    @FXML
    private Label result_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void signUpToDB_RetToSignIn(ActionEvent event) {

        if (checkRegister()) {
            user_name_lbl.setText("");
            password_lbl.setText("");
            confirm_password_lbl.setText("");
            question1_lbl.setText("");
            question2_lbl.setText("");
            connectToServer(ConnectToServerController.ip);

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
        if ((password_txt.getText().isEmpty()) || (password_txt.getText().length() < 7)) {
            flag = false;
            password_lbl.setText("password must be more than 7 char");
        } else {
            password_lbl.setText("");
        }
        if (!(confirm_password_txt.getText()).equals(password_txt.getText())) {
            flag = false;
            confirm_password_lbl.setText("Password doesn't match");
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
            question2_lbl.setText("*");
        }
        return flag;
    }

    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            JSONObject signUpJson = new JSONObject();
            signUpJson.put("functionNumber", "2");
            signUpJson.put("username", user_name_txt.getText().trim());
            signUpJson.put("password", password_txt.getText());
            signUpJson.put("answer1", question1_txt.getText());
            signUpJson.put("answer2", Question2_txt.getText());
            System.out.println(signUpJson);
            ps.println(signUpJson);

            thread = new Thread(this);
            thread.start();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
        }
    }

    private void closeConnection() {
        try {
            dis.close();
            ps.close();
            socket.close();
            thread.stop();
        } catch (IOException ex) {
            System.out.println("Close Connection ");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Open Login Page").show();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                result = dis.readLine();
                JSONObject obj = new JSONObject(result);
                if (obj.get("signUpCondition").toString().equalsIgnoreCase("true")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            result_label.setText(" User Registered Successfully");
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            result_label.setText("Can't Register User Name Already Exist!!");
                        }
                    });
                }
                closeConnection();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
            }

        }

    }

}
