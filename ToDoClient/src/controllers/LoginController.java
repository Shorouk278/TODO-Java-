/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

/**
 *
 * @author Shorouk
 */
public class LoginController implements Initializable, Runnable {

    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;
    public static String username;
    private AnchorPane pane;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField name_textfield;
    @FXML
    private PasswordField password_textfield;
    @FXML
    private Button login_btn;
    @FXML
    private Button sign_up_btn;
    @FXML
    private Hyperlink recovery_link;
    @FXML
    private Label user_name_lbl;
    @FXML
    private Label password_lbl;
    @FXML
    private Label ip_lbl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
            pane = FXMLLoader.load(getClass().getResource("/views/SignUp.fxml"));
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "cant open sign up page").show();
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
            connectToServer(ConnectToServerController.ip);

        }
    }

    @FXML
    private void moveToRecovery(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("/views/Recovery.fxml"));
            rootPane.getChildren().setAll(pane);

        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "can not open recovery page").show();
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
        if ((password_textfield.getText().trim().isEmpty()) || (password_textfield.getText().length() < 7)) {
            flag = false;
            password_lbl.setText("Password must be more than 7 char");
        } else {
            password_lbl.setText("");
        }
        return flag;
    }

    /**
     * open connection with server .
     *
     * @param ip server IP .
     */
    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            JSONObject loginJson = new JSONObject();
            loginJson.put("functionNumber", "1");
            loginJson.put("username", name_textfield.getText());
            loginJson.put("password", password_textfield.getText());
            ps.println(loginJson);
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

    @Override
    /**
     *
     */
    public void run() {
        String serverResponse;
        while (true) {
            try {
                serverResponse = dis.readLine();
                JSONObject serverMessage = new JSONObject(serverResponse);
                if (serverMessage.get("loginCondition").toString().equals("true")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                username = name_textfield.getText().trim();
                                pane = FXMLLoader.load(getClass().getResource("/views/Home.fxml"));
                                rootPane.getChildren().setAll(pane);

                            } catch (IOException ex) {
                                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ip_lbl.setText("Invalid User-Name or Password");
                        }
                    });
                }
                closeConnection();
            } catch (IOException ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new Alert(Alert.AlertType.ERROR, "Connection Lost to Server");
                    }
                });
            }
        }
    }

}
