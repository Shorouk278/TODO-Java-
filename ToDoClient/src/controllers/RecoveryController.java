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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

/**
 *
 * @author Shorouk
 */
public class RecoveryController implements Runnable, Initializable {

    boolean flag = true;
    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;

    AnchorPane pane = null;
    @FXML
    private AnchorPane rootPaneRecovery;
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
    private Label ip_lbl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void submitAction(ActionEvent event) {

        if (recoveryValidation()) {

            connectToServer(ConnectToServerController.ip);

        }

    }

    public boolean recoveryValidation() {
        if (user_name_textfield.getText().trim().isEmpty()) {
            user_name_lbl.setText("*");
            flag = false;
        } else {
            user_name_lbl.setText("");
        }
        if (answer_one_textfield.getText().trim().isEmpty()) {
            question1_lbl.setText("*");
            flag = false;
        } else {
            question1_lbl.setText("");
        }

        if (answer_two_textfield.getText().trim().isEmpty()) {
            question2_lbl.setText("*");
            flag = false;
        } else {
            question2_lbl.setText("");
        }
        return flag;
    }

    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            JSONObject recoveryJson = new JSONObject();
            recoveryJson.put("functionNumber", "3");
            recoveryJson.put("username", user_name_textfield.getText().trim());
            recoveryJson.put("answer1", answer_one_textfield.getText());
            recoveryJson.put("answer2", answer_two_textfield.getText());
            ps.println(recoveryJson);
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

    String pass;

    @Override
    public void run() {
        while (true) {
            try {

                pass = dis.readLine();
                JSONObject obj = new JSONObject(pass);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        recovery_password_label.setText(obj.getString("password").toString());
                    }
                });
                closeConnection();
            } catch (IOException ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
                    }
                });

            }

        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Open Login Page").show();
        }
        rootPaneRecovery.getChildren().setAll(pane);
    }

}
